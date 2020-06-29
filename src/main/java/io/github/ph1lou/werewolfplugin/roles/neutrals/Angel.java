package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.TimerLG;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Angel extends RolesNeutral implements AffectedPlayers, LimitedUse, AngelRole, Transformed {

    private int use = 0;
    private AngelForm choice=AngelForm.ANGEL;
    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean transformed=false;

    public Angel(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public int getUse() {
        return use;
    }


    @Override
    public void setUse(int use) {
        this.use = use;
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
    public boolean isChoice(AngelForm AngelForm) {
        return AngelForm==choice;
    }

    @Override
    public AngelForm getChoice() {
        return this.choice;
    }

    @Override
    public void setChoice(AngelForm choice) {
        this.choice = choice;
    }

    @Override
    public String getDescription() {
        if(choice.equals(AngelForm.FALLEN_ANGEL)) return game.translate("werewolf.role.fallen_angel.description");
        if(choice.equals(AngelForm.GUARDIAN_ANGEL)) return game.translate("werewolf.role.guardian_angel.description");
        return game.translate("werewolf.role.angel.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.angel.display";
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }

        Player player =Bukkit.getPlayer(getPlayerUUID());

        if(isChoice(AngelForm.ANGEL)){
            player.sendMessage(game.translate("werewolf.role.angel.angel_choice", game.getScore().conversion(game.getConfig().getTimerValues().get(TimerLG.ANGEL_DURATION))));
            player.setMaxHealth(player.getMaxHealth() + 4);
        }
        else{
            if(!getAffectedPlayers().isEmpty()){
                UUID targetUUID = getAffectedPlayers().get(0);
                PlayerWW target = game.getPlayersWW().get(targetUUID);

                if(target.isState(State.DEATH)) {
                    player.setMaxHealth(player.getMaxHealth()+4);
                    if (isChoice(AngelForm.FALLEN_ANGEL)) {
                        if(target.getKillers().contains(getPlayerUUID())){
                            player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
                            player.setMaxHealth(player.getMaxHealth()+6);
                        }
                    }
                    else player.sendMessage(game.translate("werewolf.role.guardian_angel.protege_death"));


                } else {
                    player.setMaxHealth(player.getMaxHealth()+4);
                    if (isChoice(AngelForm.FALLEN_ANGEL)) {
                        player.sendMessage(game.translate("werewolf.role.fallen_angel.reveal_target", target.getName()));
                    }
                    else {
                        player.setMaxHealth(player.getMaxHealth()+6);
                        player.sendMessage(game.translate("werewolf.role.guardian_angel.reveal_protege", target.getName()));
                    }
                }
            }
        }
    }

    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if(player == null) return null;
        if (isChoice(AngelForm.ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.angel.angel_choice", game.getScore().conversion(game.getConfig().getTimerValues().get(TimerLG.ANGEL_DURATION))));
        }
        player.setMaxHealth(24);
        player.setHealth(24);
        return player;
    }


    @EventHandler
    public void onAutoAngel(AutoAngelEvent event){

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(isChoice(AngelForm.ANGEL)){

            if(game.getRandom().nextBoolean()){
                if(Bukkit.getPlayer(getPlayerUUID())!=null){
                    Bukkit.getPlayer(getPlayerUUID()).sendMessage(game.translate("werewolf.role.angel.angel_choice_perform",game.translate("werewolf.role.fallen_angel.display")));
                }
                setChoice(AngelForm.FALLEN_ANGEL);
            }
            else {
                if(Bukkit.getPlayer(getPlayerUUID())!=null){
                    Bukkit.getPlayer(getPlayerUUID()).sendMessage(game.translate("werewolf.role.angel.angel_choice_perform",game.translate("werewolf.role.guardian_angel.display")));
                }
                setChoice(AngelForm.GUARDIAN_ANGEL);
            }
            Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(getPlayerUUID(),getChoice()));
        }

        UUID targetUUID = game.autoSelect(getPlayerUUID());
        addAffectedPlayer(targetUUID);
        PlayerWW target = game.getPlayersWW().get(targetUUID);

        if (!plg.isState(State.ALIVE)){
            return;
        }

        if(Bukkit.getPlayer(getPlayerUUID()) != null){
            Player player = Bukkit.getPlayer(getPlayerUUID());
            if(isChoice(AngelForm.FALLEN_ANGEL)){
                player.sendMessage(game.translate("werewolf.role.fallen_angel.reveal_target",target.getName()));
            }
            else {
                player.setMaxHealth(player.getMaxHealth()+6);
                player.sendMessage(game.translate("werewolf.role.guardian_angel.reveal_protege",target.getName()));
            }
            player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER,1,20);
        }

        Bukkit.getPluginManager().callEvent(new AngelTargetEvent(getPlayerUUID(),targetUUID));

        game.checkVictory();
    }




    @EventHandler
    public void onEndPlayerMessage(EndPlayerMessageEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        StringBuilder sb = event.getEndMessage();
        if(isDisplay("werewolf.role.angel.display") && !isChoice(AngelForm.ANGEL)){
            sb.append(", ").append(game.translate("werewolf.role.angel.choice",game.translate(isChoice(AngelForm.ANGEL)?"werewolf.role.angel.display":isChoice(AngelForm.FALLEN_ANGEL)?"werewolf.role.fallen_angel.display":"werewolf.role.guardian_angel.display")));
        }
    }


    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        UUID uuid = event.getUuid();

        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(!getAffectedPlayers().contains(uuid)) return;
        
        PlayerWW target = game.getPlayersWW().get(uuid);

        if (!plg.isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        Bukkit.getPluginManager().callEvent(new AngelTargetDeathEvent(getPlayerUUID(),uuid));
        if(isChoice(AngelForm.FALLEN_ANGEL)){
            if (target.getLastKiller().equals(getPlayerUUID())) {
                player.setMaxHealth(player.getMaxHealth() + 6);
                player.sendMessage(game.translate("werewolf.role.fallen_angel.deadly_target"));
            }
        }
        else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            player.setMaxHealth(player.getMaxHealth() - 6);
            player.sendMessage(game.translate("werewolf.role.guardian_angel.protege_death"));
            transformed=true;
        }
    }

    @EventHandler
    public void onTargetIsStolen(StealEvent event){

        UUID newUUID = event.getNewUUID();
        UUID oldUUID = event.getOldUUID();
        PlayerWW plg = game.getPlayersWW().get(getPlayerUUID());

        if(!getAffectedPlayers().contains(oldUUID)) return;

        removeAffectedPlayer(oldUUID);
        addAffectedPlayer(newUUID);

        if(!plg.isState(State.ALIVE)) return;

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());
        String targetName = game.getPlayersWW().get(newUUID).getName();

        if (isChoice(AngelForm.FALLEN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.fallen_angel.new_target", targetName));
        } else if (isChoice(AngelForm.GUARDIAN_ANGEL)) {
            player.sendMessage(game.translate("werewolf.role.guardian_angel.new_protege",targetName));
        }
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event){

        if(!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder=new StringBuilder(event.getActionBar());

        if(Bukkit.getPlayer(event.getPlayerUUID())==null) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if(!isChoice(AngelForm.GUARDIAN_ANGEL)) {
            return;
        }

        for (UUID uuid : getAffectedPlayers()) {
            if (game.getPlayersWW().get(uuid).isState(State.ALIVE) && Bukkit.getPlayer(uuid) != null) {
                stringBuilder.append("§b ").append(game.getPlayersWW().get(uuid).getName()).append(" ").append(game.getScore().updateArrow(player, Bukkit.getPlayer(uuid).getLocation()));
            }
        }

        event.setActionBar(stringBuilder.toString());
    }

    @Override
    public boolean getTransformed() {
        return this.transformed;
    }

    @Override
    public void setTransformed(boolean b) {
        this.transformed=b;
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() && !transformed;
    }
}
