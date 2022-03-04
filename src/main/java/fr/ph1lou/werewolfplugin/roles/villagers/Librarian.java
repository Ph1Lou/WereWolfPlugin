package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.librarian.LibrarianDeathEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends RoleVillage implements ILimitedUse, IAffectedPlayers {

    private int use = 0;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private final List<String> storage = new ArrayList<>();

    public Librarian(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
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
                .setDescription(game.translate("werewolf.role.librarian.description",
                                Formatter.number(3 - use)))
                .setItems(game.translate("werewolf.role.librarian.items"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    public List<String> getStorage() {
        return this.storage;
    }

    public void addStorage(String message) {
        this.storage.add(message);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (this.storage.isEmpty()) return;

        if (!isAbilityEnabled()) return;

        Bukkit.broadcastMessage(game.translate(Prefix.YELLOW.getKey() , "werewolf.role.librarian.death"));
        int page = 1;
        for (String message : this.storage) {
            Bukkit.broadcastMessage(game.translate(
                    Prefix.YELLOW.getKey() , "werewolf.role.librarian.book",
                    Formatter.format("&page&",page),
                    Formatter.format("&message&",message)));
            page++;
        }

        this.getStorage().clear();

        Bukkit.getPluginManager().callEvent(new LibrarianDeathEvent(getPlayerWW()));
    }
}
