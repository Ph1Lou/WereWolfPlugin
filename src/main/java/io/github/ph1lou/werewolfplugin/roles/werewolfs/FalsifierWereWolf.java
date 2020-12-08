package io.github.ph1lou.werewolfplugin.roles.werewolfs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.NewDisplayRole;
import io.github.ph1lou.werewolfapi.events.SelectionEndEvent;
import io.github.ph1lou.werewolfapi.events.StealEvent;
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

    public FalsifierWereWolf(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    @Override
    public void setDisplayCamp(Camp camp) {
        this.displayCamp = camp;
    }

    @Override
    public boolean isDisplayCamp(Camp camp) {
        return (this.displayCamp.equals(camp));
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


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        List<UUID> players = new ArrayList<>();
        for (PlayerWW playerWW1 : game.getPlayerWW()) {
            if (playerWW1.isState(StatePlayer.ALIVE) && !playerWW1.equals(getPlayerWW())) {
                players.add(playerWW1.getUUID());
            }
        }
        if (players.size() <= 0) {
            return;
        }

        PlayerWW displayWW = game.autoSelect(getPlayerWW());

        Roles roles = displayWW.getRole();
        NewDisplayRole newDisplayRole = new NewDisplayRole(getPlayerWW(), roles.getKey(), roles.getCamp());
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


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        player.sendMessage(game.translate("werewolf.role.falsifier_werewolf.display_role_message", game.translate(getDisplayRole().getKey())));
    }

    @Override
    public void recoverPower() {

    }
}
