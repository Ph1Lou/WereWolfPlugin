package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.Camp;
import io.github.ph1lou.werewolfapi.enums.Sound;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.GrowlEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BearTrainer extends RolesVillage {

    public BearTrainer(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (player == null) return;

        Location oursLocation = player.getLocation();
        Set<PlayerWW> growled = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player1 -> {
                    try {
                        return oursLocation.distance(player1.getLocation())
                                < game.getConfig().getDistanceBearTrainer();
                    } catch (Exception ignored) {
                        return false;
                    }
                })
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Objects::nonNull)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(PlayerWW::getRole)
                .filter(roles -> roles.isWereWolf() || roles instanceof Display)
                .filter(roles -> !(roles instanceof Display) ||
                        ((Display) roles).isDisplayCamp(Camp.WEREWOLF.getKey()))
                .map(Roles::getPlayerWW)
                .collect(Collectors.toSet());

        GrowlEvent growlEvent = new GrowlEvent(getPlayerWW(), growled);
        Bukkit.getPluginManager().callEvent(growlEvent);
    }

    @EventHandler
    public void onGrowl(GrowlEvent event) {

        if (event.getPlayerWWS().isEmpty()) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (event.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        String builder = event.getPlayerWWS().stream().map(ignored ->
                game.translate("werewolf.role.bear_trainer.growling"))
                .collect(Collectors.joining());

        Bukkit.getOnlinePlayers()
                .forEach(Sound.WOLF_GROWL::play);

        Bukkit.broadcastMessage(game.translate("werewolf.role.bear_trainer.growling_message", builder));
    }

    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description",
                        game.translate("werewolf.role.bear_trainer.description",
                                game.getConfig().getDistanceBearTrainer()));
    }


    @Override
    public void recoverPower() {

    }

}
