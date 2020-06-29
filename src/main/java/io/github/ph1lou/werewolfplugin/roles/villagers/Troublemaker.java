package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.TroubleMakerDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Troublemaker extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Troublemaker(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if(!event.getUuid().equals(getPlayerUUID())) return;

        Bukkit.getPluginManager().callEvent(new TroubleMakerDeathEvent(getPlayerUUID()));
        int i = 0;
        for (UUID uuid : game.getPlayersWW().keySet()) {
            PlayerWW plg = game.getPlayersWW().get(uuid);
            if (plg.isState(State.ALIVE)) {
                game.transportation(uuid, i * 2 * Math.PI / game.getScore().getPlayerSize(), game.translate("werewolf.role.troublemaker.troublemaker_death"));
                i++;
            }
        }
    }

    private boolean power=true;
    @Override
    public void setPower(Boolean power) {
        this.power=power;
    }

    @Override
    public Boolean hasPower() {
        return(this.power);
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


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.troublemaker.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.troublemaker.display";
    }
}
