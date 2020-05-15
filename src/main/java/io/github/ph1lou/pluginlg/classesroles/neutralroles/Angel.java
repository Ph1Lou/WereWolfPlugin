package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.events.AutoAngelEvent;
import io.github.ph1lou.pluginlg.events.TargetDeathEvent;
import io.github.ph1lou.pluginlg.events.TargetStealEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Angel extends RolesNeutral implements AffectedPlayers, Power {

    private RoleLG choice=RoleLG.ANGEL;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Angel(GameManager game, UUID uuid) {
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
        return RoleLG.ANGEL;
    }

    public boolean isChoice(RoleLG roleLG) {
        return roleLG==choice;
    }

    public RoleLG getChoice() {
        return this.choice;
    }

    public void setChoice(RoleLG choice) {
        this.choice = choice;
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.angel.description");
    }

    @Override
    public String getDisplay() {
        return game.translate("werewolf.role.angel.display");
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player =Bukkit.getPlayer(getPlayerUUID());

        if(hasPower()){
            player.sendMessage(game.translate("werewolf.role.angel.angel_choice", game.score.conversion(game.config.getTimerValues().get(TimerLG.ANGEL_DURATION))));
            player.setMaxHealth(player.getMaxHealth()+4);
        }
        else{
            if(!getAffectedPlayers().isEmpty()){
                UUID targetUUID = getAffectedPlayers().get(0);
                PlayerLG target = game.playerLG.get(targetUUID);

                if(target.isState(State.DEATH)) {
                    if (isChoice(RoleLG.FALLEN_ANGEL)) {
                        if(target.getKillers().contains(getPlayerUUID())){
                            player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
                            player.setMaxHealth(player.getMaxHealth()+10);
                        }
                    }
                    else {
                        player.sendMessage(game.translate("werewolf.role.guardian_angel.protege_death"));
                    }

                } else {
                    player.setMaxHealth(player.getMaxHealth()+4);
                    if (isChoice(RoleLG.FALLEN_ANGEL)) {
                        player.sendMessage(game.translate("werewolf.role.fallen_angel.reveal_target", target.getName()));
                    }
                    else player.sendMessage(game.translate("werewolf.role.guardian_angel.reveal_protege", target.getName()));
                }
            }
        }
    }

    @Override
    public void recoverPower(Player player) {

        if (isChoice(RoleLG.ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.angel.angel_choice", game.score.conversion(game.config.getTimerValues().get(TimerLG.ANGEL_DURATION))));
        }
        player.setMaxHealth(24);
        player.setHealth(24);
    }

    @EventHandler
    public void onAutoAngel(AutoAngelEvent event){

        if(!event.getUuid().equals(game.getGameUUID())){
            return;
        }

        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(hasPower()){
            setPower(false);
            if(game.getRandom().nextBoolean()){
                setChoice(RoleLG.FALLEN_ANGEL);
            }
            else setChoice(RoleLG.GUARDIAN_ANGEL);
        }

        UUID targetUUID = game.roleManage.autoSelect(getPlayerUUID());
        addAffectedPlayer(targetUUID);
        PlayerLG target = game.playerLG.get(targetUUID);

        if (!plg.isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID()) != null){
            Player player = Bukkit.getPlayer(getPlayerUUID());
            if(isChoice(RoleLG.FALLEN_ANGEL)){
                player.sendMessage(game.translate("werewolf.role.fallen_angel.reveal_target",target.getName()));
            }
            else player.sendMessage(game.translate("werewolf.role.guardian_angel.reveal_protege",target.getName()));
            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER,1,20);
        }

        if(!game.isState(StateLG.END)) {
            game.endlg.check_victory();
        }
    }

    @EventHandler
    public void onTargetDeath(TargetDeathEvent event) {

        UUID uuid = event.getUuid();

        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(!getAffectedPlayers().contains(uuid)) return;
        
        PlayerLG target = game.playerLG.get(uuid);

        if (!plg.isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());
        
        if(isChoice(RoleLG.FALLEN_ANGEL)){
            if (target.getLastKiller().equals(uuid)) {
                player.setMaxHealth(player.getMaxHealth() + 6);
                player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
            }
        }
        else if (isChoice(RoleLG.GUARDIAN_ANGEL)) {
            player.setMaxHealth(player.getMaxHealth() - 4);
            player.sendMessage(game.translate("werewolf.role.guardian_angel.protege_death"));
            if (!isCamp(Camp.WEREWOLF)) {
                setCamp(Camp.VILLAGER);
            }
        }
    }

    @EventHandler
    public void onTargetIsStolen(TargetStealEvent event){

        UUID newUUID = event.getNewUUID();
        UUID oldUUID = event.getOldUUID();
        PlayerLG plg = game.playerLG.get(getPlayerUUID());

        if(!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if(!plg.isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());
        String targetName = game.playerLG.get(newUUID).getName();

        if (isChoice(RoleLG.FALLEN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.fallen_angel.new_target", targetName));
        } else if (isChoice(RoleLG.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.new_protege",targetName));
        }
    }
}
