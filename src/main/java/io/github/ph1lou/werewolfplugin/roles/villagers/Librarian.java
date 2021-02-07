package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.events.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.LibrarianDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.LimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfapi.rolesattributs.Storage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends RolesVillage implements LimitedUse, AffectedPlayers, Storage {

    private int use = 0;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private final List<String> storage = new ArrayList<>();

    public Librarian(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
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

        Bukkit.getPluginManager().callEvent(new LibrarianDeathEvent(getPlayerWW()));
    }
}
