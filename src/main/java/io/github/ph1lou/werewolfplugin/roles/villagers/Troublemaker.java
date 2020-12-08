package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.TroubleMakerDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Troublemaker extends RolesVillage implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public Troublemaker(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        Bukkit.getPluginManager().callEvent(new TroubleMakerDeathEvent(getPlayerWW()));
        int i = 0;
        for (PlayerWW playerWW1 : game.getPlayerWW()) {

            if (playerWW1.isState(StatePlayer.ALIVE)) {
                game.getMapManager().transportation(playerWW1,
                        i * 2 * Math.PI / game.getScore().getPlayerSize(),
                        game.translate("werewolf.role.troublemaker.troublemaker_death"));
                i++;
            }
        }
    }

    private boolean power=true;

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
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


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.troublemaker.description");
    }


    @Override
    public void recoverPower() {

    }


}
