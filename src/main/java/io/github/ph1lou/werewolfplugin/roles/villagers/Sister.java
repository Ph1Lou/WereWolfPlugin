package io.github.ph1lou.werewolfplugin.roles.villagers;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import io.github.ph1lou.werewolfapi.events.roles.sister.SisterDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Sister extends RoleVillage implements IAffectedPlayers {

    final List<IPlayerWW> killerWWS = new ArrayList<>();

    public Sister(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }


    @Override
    public @NotNull String getDescription() {

        String extraLines;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST.getKey()) > 0) {
            extraLines= game.translate("werewolf.role.sister.sisters_list",
                    Formatter.format("&list&",
                            Utils.conversion(game.getConfig()
                                    .getTimerValue(TimerBase.WEREWOLF_LIST.getKey()))));
        } else {
            extraLines= game.translate("werewolf.role.sister.sisters_list",
                    Formatter.format("&list&",this.getSister()));
        }

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.sister.description"))
                .setEffects(game.translate("werewolf.role.sister.effect",
                        Formatter.format("&number&", game.getConfig().getDistanceSister())))
                .addExtraLines(extraLines)
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        this.getPlayerWW().sendMessageWithKey("werewolf.role.sister.sisters_list",
                Formatter.format("&list&",this.getSister()));
    }

    private String getSister() {

        StringBuilder list = new StringBuilder();

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.getRole().isKey(RolesBase.SISTER.getKey()))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));
        return list.toString();
    }

    @Override
    public void second() {

        Player sister = Bukkit.getPlayer(getPlayerUUID());

        if (sister == null) {
            return;
        }
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Location location = sister.getLocation();

        boolean recoverResistance = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.isKey(RolesBase.SISTER.getKey()))
                .map(IRole::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> player.getWorld().equals(location.getWorld()) &&
                        location.distance(player.getLocation()) < game.getConfig().getDistanceSister())
                .findFirst()
                .orElse(null) != null;


        if (recoverResistance) {

            this.getPlayerWW().addPotionModifier(PotionModifier.add(
                    PotionEffectType.DAMAGE_RESISTANCE,
                    100,
                    0,
                    "sister"));
        }


    }

    @EventHandler
    public void onSisterDeathReveal(SisterDeathEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        event.getAllSisters().add(getPlayerWW());

        IPlayerWW sisterWW = event.getSister();
        IPlayerWW killerWW = event.getKiller();
        TextComponent textComponent = new TextComponent(game.translate(Prefix.YELLOW.getKey() , "werewolf.role.sister.choice"));

        TextComponent name = new TextComponent(
                game.translate("werewolf.role.sister.name"));
        name.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.role.sister.command_name"),
                                killerWW == null ? "pve" : killerWW.getUUID().toString())));
        name.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(game.translate("werewolf.role.sister.see_name"))
                                .create()));

        textComponent.addExtra(name);

        textComponent.addExtra(game.translate("werewolf.role.sister.or"));

        TextComponent role =
                new TextComponent(game.translate("werewolf.role.sister.role"));

        role.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.role.sister.command_role"),
                                killerWW == null ? "pve" : killerWW.getUUID().toString())));

        role.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(game.translate("werewolf.role.sister.see_role"))
                                .create()));

        textComponent.addExtra(role);

        textComponent.addExtra(new TextComponent(game.translate("werewolf.role.sister.end_message",
                Formatter.format("&player&",sisterWW.getName()))));

        this.getPlayerWW().sendMessage(textComponent);

        killerWWS.add(sisterWW.getLastKiller().orElse(null));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSisterDeathRevealEnd(SisterDeathEvent event) {
        if (event.getAllSisters().isEmpty()) event.setCancelled(true);
    }

    @EventHandler
    public void onSisterDeath(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (!playerWW.equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new SisterDeathEvent(playerWW,
                new HashSet<>(), this.getPlayerWW().getLastKiller().isPresent() ? this.getPlayerWW().getLastKiller().get() : null));

    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        killerWWS.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        killerWWS.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        killerWWS.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
        return killerWWS;
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.GRAY_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.sister",
                                        Formatter.format("&number&",config.getDistanceSister())))
                        .setLore(lore).build()), e -> {

            if (e.isLeftClick()) {
                config.setDistanceSister((config.getDistanceSister() + 2));
            } else if (config.getDistanceSister() - 2 > 0) {
                config.setDistanceSister(config.getDistanceSister() - 2);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.sister",
                                    Formatter.format("&number&",config.getDistanceSister())))
                    .build());

        });
    }
}
