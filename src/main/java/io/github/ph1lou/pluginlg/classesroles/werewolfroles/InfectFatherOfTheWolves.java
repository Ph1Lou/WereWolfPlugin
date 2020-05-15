package io.github.ph1lou.pluginlg.classesroles.werewolfroles;


import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfectFatherOfTheWolves extends RolesWereWolf implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public InfectFatherOfTheWolves(GameManager game, UUID uuid) {
        super(game,uuid);
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
    public RoleLG getRoleEnum() {
        return RoleLG.INFECT;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.infect_father_of_the_wolves.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.infect_father_of_the_wolves.display");
    }
}
