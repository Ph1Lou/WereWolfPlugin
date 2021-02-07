package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.AnnouncementDeathEvent;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Priestess extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public Priestess(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);

        setPower(false);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @EventHandler
    public void onDeath(FinalDeathEvent event) {

        if (!this.affectedPlayer.contains(event.getPlayerWW())) return;

        if (!event.getPlayerWW().getRole().isWereWolf()) return;

        getPlayerWW().sendMessageWithKey("werewolf.role.priestess.werewolf_death");

        this.affectedPlayer.remove(event.getPlayerWW());

        getPlayerWW().addPlayerMaxHealth(2);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.priestess.perform",
                game.getConfig().getDistancePriestess(),
                game.getScore().conversion(
                        game.getConfig()
                                .getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.priestess.description"))
                .setItems(() -> game.translate("werewolf.role.priestess.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        if (event.isCancelled()) return;

        event.setCancelled(true);

        WereWolfAPI game = main.getWereWolfAPI();

        game.getPlayerWW()
                .stream()
                .map(PlayerWW::getRole)
                .forEach(roles -> {
                    if (roles.isNeutral()) {
                        roles.getPlayerWW().sendMessage(game.translate(event.getFormat())
                                .replace("&player&", event.getPlayerName())
                                .replace("&role&", game.translate(event.getRole())));
                    } else
                        sendDeathMessage(roles.getPlayerWW(), event.getPlayerWW(),
                                roles.isWereWolf(), event.getFormat(), event.getRole());
                });

        game.getModerationManager().getModerators().stream()
                .filter(uuid -> game.getPlayerWW(uuid) == null)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(game.translate("werewolf.announcement.death_message_with_role")
                        .replace("&player&", event.getPlayerName())
                        .replace("&role&", game.translate(event.getPlayerWW().getRole().getKey()))));

        Bukkit.getConsoleSender()
                .sendMessage(game.translate("werewolf.announcement.death_message_with_role")
                        .replace("&player&", event.getPlayerName())
                        .replace("&role&",
                                game.translate(event.getPlayerWW().getRole().getKey())));
        event.setRole(event.getPlayerWW().getRole().getCamp().getKey());
    }

    private void sendDeathMessage(PlayerWW playerWW, PlayerWW targetWW, boolean isWerewolf, String format, String role) {

        String message = game.translate(format).replace("&player&", targetWW.getName());

        if (game.getRandom().nextFloat() < 0.8) {

            if (getPlayerWW().isState(StatePlayer.ALIVE) && isWerewolf) {
                playerWW.sendMessage(message.replace("&role&", ChatColor.MAGIC + "Coucou"));
            } else {
                playerWW.sendMessage(message.replace("&role&", game.translate(role)));
            }
        } else {

            if (getPlayerWW().isState(StatePlayer.ALIVE) && isWerewolf) {
                playerWW.sendMessage(message.replace("&role&", game.translate(role)));
            } else {
                playerWW.sendMessage(message.replace("&role&", ChatColor.MAGIC + "Coucou"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdatePlayerNameTag event) {

        WereWolfAPI game = main.getWereWolfAPI();

        PlayerWW playerWW = game.getPlayerWW(event.getPlayerUUID());

        if (playerWW == null) {
            return;
        }

        if (!playerWW.isState(StatePlayer.DEATH)) return;

        event.setSuffix("");
    }
}
