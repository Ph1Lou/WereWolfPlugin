package io.github.ph1lou.pluginlg.classesroles.villageroles;

import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.LimitedUse;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.events.VoteEndEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Citizen extends RolesVillage implements LimitedUse, AffectedPlayers , Power {

    private int use = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Citizen(GameManager game, UUID uuid) {
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
    public int getUse() {
        return use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @EventHandler
    public void onVoteEnd(VoteEndEvent event) {

        if(!game.playerLG.get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }
        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (getUse() < 2 || hasPower()) {
            player.sendMessage(game.translate("werewolf.role.citizen.affect_votes",hasPower() ? 1 : 0, 2 - getUse(), game.score.conversion(game.config.getTimerValues().get(TimerLG.CITIZEN_DURATION))));
        }
    }


    @Override
    public RoleLG getRoleEnum() {
        return RoleLG.CITIZEN;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.citizen.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.citizen.display");
    }
}
