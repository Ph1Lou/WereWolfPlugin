package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cupid extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Cupid(GameManager game, UUID uuid) {
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
        return RoleLG.CUPID;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.cupid.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.cupid.display");
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (hasPower()) {
            player.sendMessage(game.translate("werewolf.role.cupid.lover_designation_message", game.score.conversion(game.config.getTimerValues().get(TimerLG.LOVER_DURATION))));
        } else {
            player.sendMessage(game.translate("werewolf.role.cupid.designation_perform",game.playerLG.get(getAffectedPlayers().get(0)).getName(), game.playerLG.get(getAffectedPlayers().get(1)).getName()));
        }
    }

    @Override
    public void recoverPower(Player player) {
        player.sendMessage(game.translate("werewolf.role.cupid.lover_designation_message", game.score.conversion(game.config.getTimerValues().get(TimerLG.LOVER_DURATION))));
    }
}
