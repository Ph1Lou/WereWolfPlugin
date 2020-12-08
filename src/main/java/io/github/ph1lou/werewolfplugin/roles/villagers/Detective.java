package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.enumlg.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Detective extends RolesWithLimitedSelectionDuration implements AffectedPlayers {


    private final List<PlayerWW> affectedPlayer = new ArrayList<>();


    public Detective(GetWereWolfAPI main, PlayerWW playerWW, String key) {
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
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());
        setPower(true);

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.detective.inspection_message", game.getScore().conversion(game.getConfig().getTimerValues().get(TimersBase.POWER_DURATION.getKey()))));
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.detective.description");
    }


    @Override
    public void recoverPower() {

    }


}
