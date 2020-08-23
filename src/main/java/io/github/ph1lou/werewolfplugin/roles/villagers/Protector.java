package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protector extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Protector(GetWereWolfAPI main, WereWolfAPI game, UUID uuid){
        super(main,game,uuid);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(UUID uuid) {
        this.affectedPlayer.add(uuid);
    }

    @Override
    public void removeAffectedPlayer(UUID uuid) {
        this.affectedPlayer.remove(uuid);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<UUID> getAffectedPlayers() {
        return (this.affectedPlayer);
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (getPlayerUUID() == null) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());
        setPower(true);

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.protector.protection_message", game.getScore().conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.power_duration"))));
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.protector.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.protector.display";
    }
}
