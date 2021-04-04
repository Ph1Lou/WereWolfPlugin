package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.librarian.LibrarianDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IStorage;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends RoleVillage implements ILimitedUse, IAffectedPlayers, IStorage {

    private int use = 0;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final List<String> storage = new ArrayList<>();

    public Librarian(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<IPlayerWW> getAffectedPlayers() {
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
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.librarian.description", 3 - use))
                .setItems(() -> game.translate("werewolf.role.librarian.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    @Override
    public List<String> getStorage() {
        return this.storage;
    }

    @Override
    public void addStorage(String message) {
        this.storage.add(message);
    }

    @Override
    public void clearStorage() {
        this.storage.clear();
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (this.storage.isEmpty()) return;

        Bukkit.broadcastMessage(game.translate("werewolf.role.librarian.death"));
        int i = 1;
        for (String s : this.storage) {
            Bukkit.broadcastMessage(game.translate(
                    "werewolf.role.librarian.book", i, s));
            i++;
        }

        this.getStorage().clear();

        Bukkit.getPluginManager().callEvent(new LibrarianDeathEvent(getPlayerWW()));
    }
}
