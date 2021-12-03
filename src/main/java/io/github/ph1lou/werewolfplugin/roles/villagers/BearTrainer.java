package io.github.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.roles.bear_trainer.GrowlEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BearTrainer extends RoleVillage {

    public BearTrainer(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (player == null) return;

        if (!isAbilityEnabled()) return;

        Location oursLocation = player.getLocation();
        Set<IPlayerWW> growled = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> player1.getWorld().equals(oursLocation.getWorld())
                        && oursLocation.distance(player1.getLocation())
                        < game.getConfig().getDistanceBearTrainer())
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> roles.isDisplayCamp(Camp.WEREWOLF.getKey()))
                .map(IRole::getPlayerWW)
                .collect(Collectors.toSet());

        GrowlEvent growlEvent = new GrowlEvent(this.getPlayerWW(), growled);
        Bukkit.getPluginManager().callEvent(growlEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowl(GrowlEvent event) {

        if (event.getPlayerWWS().isEmpty()) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (event.isCancelled()) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.cancel"));
            return;
        }

        String builder = event.getPlayerWWS().stream().map(ignored ->
                game.translate("werewolf.role.bear_trainer.growling"))
                .collect(Collectors.joining());

        Bukkit.getOnlinePlayers()
                .forEach(Sound.WOLF_GROWL::play);

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW.getKey() , "werewolf.role.bear_trainer.growling_message",
                Formatter.format("&growling&",builder)));
    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.bear_trainer.description",
                                Formatter.number(game.getConfig().getDistanceBearTrainer())))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BROWN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.bear_trainer",
                                        Formatter.number(config.getDistanceBearTrainer())))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceBearTrainer((config.getDistanceBearTrainer() + 5));
            } else if (config.getDistanceBearTrainer() - 5 > 0) {
                config.setDistanceBearTrainer(config.getDistanceBearTrainer() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.bear_trainer",
                                    Formatter.number(config.getDistanceBearTrainer())))
                    .build());

        });
    }
}
