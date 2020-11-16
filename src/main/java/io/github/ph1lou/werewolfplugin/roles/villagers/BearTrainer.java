package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class BearTrainer extends RolesVillage {

    public BearTrainer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }
        if (player == null) return;

        Location oursLocation = player.getLocation();
        List<UUID> growled = Bukkit.getOnlinePlayers()
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
                        ((Display) roles).isDisplayCamp(Camp.WEREWOLF))
                .map(Roles::getPlayerUUID)
                .collect(Collectors.toList());

        GrowlEvent growlEvent = new GrowlEvent(getPlayerUUID(), growled);
        Bukkit.getPluginManager().callEvent(growlEvent);
    }

    @EventHandler
    public void onGrowl(GrowlEvent event) {

        if (event.getPlayersUUID().isEmpty()) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (event.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        String builder = event.getPlayersUUID().stream().map(ignored ->
                game.translate("werewolf.role.bear_trainer.growling"))
                .collect(Collectors.joining());

        Bukkit.getOnlinePlayers()
                .forEach(Sounds.WOLF_GROWL::play);

        Bukkit.broadcastMessage(game.translate("werewolf.role.bear_trainer.growling_message", builder));
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.bear_trainer.description");
    }

}
