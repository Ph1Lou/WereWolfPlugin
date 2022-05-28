package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.roles.sister.SisterDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Role(key = RoleBase.SISTER, 
        category = Category.VILLAGER, 
        attributes = RoleAttribute.VILLAGER, 
        configValues = {@IntValue(key = Sister.DISTANCE,
                defaultValue = 20, 
                meetUpValue = 20, 
                step = 2, 
                item = UniversalMaterial.GRAY_WOOL)},
        requireDouble = true)
public class Sister extends RoleVillage implements IAffectedPlayers {

    public static final String DISTANCE = "werewolf.role.sister.distance";
    final List<IPlayerWW> killerWWS = new ArrayList<>();

    public Sister(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {

        String extraLines;

        if (game.getConfig().getTimerValue(TimerBase.WEREWOLF_LIST) > 0) {
            extraLines= game.translate("werewolf.role.sister.sisters_list",
                    Formatter.format("&list&",
                            Utils.conversion(game.getConfig()
                                    .getTimerValue(TimerBase.WEREWOLF_LIST))));
        } else {
            extraLines= game.translate("werewolf.role.sister.sisters_list",
                    Formatter.format("&list&",this.getSister()));
        }

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.sister.description"))
                .setEffects(game.translate("werewolf.role.sister.effect",
                        Formatter.number( game.getConfig().getValue(DISTANCE))))
                .addExtraLines(extraLines)
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onWerewolfList(WereWolfListEvent event) {
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW,"werewolf.role.sister.sisters_list",
                Formatter.format("&list&",this.getSister()));
    }

    private String getSister() {

        StringBuilder list = new StringBuilder();

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.getRole().equals(this))
                .filter(playerWW -> playerWW.getRole().isKey(RoleBase.SISTER))
                .forEach(playerWW -> list.append(playerWW.getName()).append(" "));
        return list.toString();
    }

    @Override
    public void second() {


        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Location location = this.getPlayerWW().getLocation();

        boolean recoverResistance = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.isKey(RoleBase.SISTER))
                .map(IRole::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> player.getWorld().equals(location.getWorld()) &&
                        location.distance(player.getLocation()) < game.getConfig().getValue(DISTANCE))
                .findFirst()
                .orElse(null) != null;


        if (recoverResistance) {

            this.getPlayerWW().addPotionModifier(PotionModifier.add(
                    PotionEffectType.DAMAGE_RESISTANCE,
                    100,
                    0,
                    this.getKey()));
        }


    }

    @EventHandler
    public void onSisterDeathReveal(SisterDeathEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        event.getAllSisters().add(getPlayerWW());

        IPlayerWW sisterWW = event.getSister();
        IPlayerWW killerWW = event.getKiller();
        TextComponent textComponent = new TextComponent(game.translate(Prefix.YELLOW , "werewolf.role.sister.choice"));

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
                Formatter.player(sisterWW.getName()))));

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
}
