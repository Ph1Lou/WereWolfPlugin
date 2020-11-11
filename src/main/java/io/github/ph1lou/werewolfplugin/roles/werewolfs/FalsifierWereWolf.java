package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.NewDisplayRole;
import io.github.ph1lou.werewolfapi.events.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FalsifierWereWolf extends RolesWereWolf implements Display {

    private Camp displayCamp = Camp.WEREWOLF;
    private Roles displayRole = this;

    public FalsifierWereWolf(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
    }

    @Override
    public void setDisplayCamp(Camp camp) {
        this.displayCamp =camp;
    }

    @Override
    public boolean isDisplayCamp(Camp camp) {
        return(this.displayCamp.equals(camp));
    }

    @Override
    public Camp getDisplayCamp() {
        return(this.displayCamp);
    }

    @Override
    public Roles getDisplayRole() {
        return(this.displayRole);
    }

    @Override
    public void setDisplayRole(Roles role) {
        this.displayRole =role;
    }

    @EventHandler
    public void onSelectionEnd(SelectionEndEvent event) {


        if (!game.getPlayersWW().get(getPlayerUUID()).isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        List<UUID> players = new ArrayList<>();
        for (UUID uuid : game.getPlayersWW().keySet()) {
            if (game.getPlayersWW().get(uuid).isState(StatePlayer.ALIVE) && !uuid.equals(player.getUniqueId())) {
                players.add(uuid);
            }
        }
        if (players.size() <= 0) {
            return;
        }

        UUID pc = players.get((int) Math.floor(Math.random() * players.size()));
        Roles roles = game.getPlayersWW().get(pc).getRole();
        NewDisplayRole newDisplayRole = new NewDisplayRole(getPlayerUUID(), roles.getKey(), roles.getCamp());
        Bukkit.getPluginManager().callEvent(newDisplayRole);

        if (newDisplayRole.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            setDisplayCamp(Camp.WEREWOLF);
            setDisplayRole(this);
        } else {
            setDisplayRole(roles);
            setDisplayCamp(newDisplayRole.getNewDisplayCamp());
        }
        player.sendMessage(game.translate("werewolf.role.falsifier_werewolf.display_role_message", game.translate(getDisplayRole().getKey())));
    }


    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.falsifier_werewolf.description");
    }


    @Override
    public void recoverPowerAfterStolen() {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.falsifier_werewolf.display_role_message", game.translate(getDisplayRole().getKey())));
    }
}
