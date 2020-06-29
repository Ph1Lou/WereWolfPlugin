package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.Storage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Librarian extends RolesVillage implements LimitedUse, AffectedPlayers, Storage {

    private int use = 0;
    private final List<UUID> affectedPlayer = new ArrayList<>();
    private final List<String> storage= new ArrayList<>();

    public Librarian(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
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


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.librarian.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.librarian.display";
    }

    @Override
    public List<String> getStorage() {
        return this.storage;
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event){

        if(!event.getUuid().equals(getPlayerUUID())) return;

        if(this.storage.isEmpty()) return;

        Bukkit.broadcastMessage(game.translate("werewolf.role.librarian.death"));
        int i=1;
        for(String s:this.storage){
            Bukkit.broadcastMessage(game.translate("werewolf.role.librarian.book",i,s));
            i++;
        }
    }
}
