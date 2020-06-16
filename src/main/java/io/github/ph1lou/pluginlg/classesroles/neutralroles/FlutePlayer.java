package io.github.ph1lou.pluginlg.classesroles.neutralroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.events.EnchantedEvent;
import io.github.ph1lou.pluginlgapi.events.SelectionEndEvent;
import io.github.ph1lou.pluginlgapi.events.WinConditionsCheckEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlutePlayer extends RolesNeutral implements Power, AffectedPlayers {


    private boolean power=false;
    private final List<UUID> affectedPlayer = new ArrayList<>();

    public FlutePlayer(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {

        if (!hasPower()) return;

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        setPower(false);

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.check.end_selection"));
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

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)){
            return;
        }

        setPower(true);

        if(Bukkit.getPlayer(getPlayerUUID())==null){
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());

        player.sendMessage(game.translate("werewolf.role.flute_player.power", game.conversion(game.getConfig().getTimerValues().get(TimerLG.POWER_DURATION))));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.flute_player.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.flute_player.display";
    }



    @EventHandler
    public void onDetectVictory(WinConditionsCheckEvent event){

        if(event.isCancelled()) return;

        if(!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        int counter=1;
        int playerAlive=0;

        for(PlayerWW playerWW:game.getPlayersWW().values()){
            if(playerWW.isState(State.ALIVE)){
                playerAlive++;
            }
        }

        for(UUID uuid1:affectedPlayer){
            if(game.getPlayersWW().get(uuid1).isState(State.ALIVE)){
                counter++;
            }
        }

        if(counter==playerAlive){

            if(!affectedPlayer.isEmpty()){
                UUID uuid1=affectedPlayer.get(0);

                if(game.getPlayersWW().get(uuid1).isState(State.ALIVE)){

                    affectedPlayer.remove(uuid1);
                    game.death(uuid1);
                }
            }
            if(playerAlive==1){
                event.setCancelled(true);
                event.setVictoryTeam(getDisplay());
            }
        }

    }

    @EventHandler
    public void onEnchantedPlayer(EnchantedEvent event){

        if(!event.getPlayerUUID().equals(getPlayerUUID())) return;

        for(UUID uuid:affectedPlayer){
            if(Bukkit.getPlayer(uuid)!=null){
                Bukkit.getPlayer(uuid).sendMessage(enchantedList());
            }
        }
    }


    public String enchantedList(){
        StringBuilder sb = new StringBuilder(game.translate("werewolf.role.flute_player.list"));

        for(UUID uuid1:affectedPlayer){
            sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
        }
        return sb.toString();
    }

    @Override
    public void stolen(UUID uuid) {

        if(Bukkit.getPlayer(getPlayerUUID())==null) return;

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if(!enchantedList().isEmpty()){
            player.sendMessage(enchantedList());
        }

    }

    @Override
    public void recoverPower(Player player) {

    }


    @Override
    public void setPower(Boolean aBoolean) {
        this.power=aBoolean;
    }

    @Override
    public Boolean hasPower() {
        return this.power;
    }
}
