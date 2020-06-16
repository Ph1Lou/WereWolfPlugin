package io.github.ph1lou.pluginlg.classesroles.villageroles;


import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.events.ActionBarEvent;
import io.github.ph1lou.pluginlgapi.events.DayEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trapper extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();

    public Trapper(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {

        super(main,game,uuid);
        setPower(false);
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

        player.sendMessage(game.translate("werewolf.role.trapper.tracking_message"));
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.trapper.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.trapper.display";
    }

    @EventHandler
    public void onActionBarRequest(ActionBarEvent event){

        if(!getPlayerUUID().equals(event.getPlayerUUID())) return;

        StringBuilder stringBuilder=new StringBuilder(event.getActionBar());

        if(Bukkit.getPlayer(event.getPlayerUUID())==null) return;

        if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE)) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());

        if(hasPower()) return;

        for (UUID uuid : getAffectedPlayers()) {
            if (game.getPlayersWW().get(uuid).isState(State.ALIVE) && Bukkit.getPlayer(uuid) != null) {
                stringBuilder.append("§b ").append(game.getPlayersWW().get(uuid).getName()).append(" ").append(game.updateArrow(player, Bukkit.getPlayer(uuid).getLocation()));
            }
        }

        event.setActionBar(stringBuilder.toString());
    }
}
