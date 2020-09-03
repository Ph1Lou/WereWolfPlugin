package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.Sounds;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SiameseTwin extends RolesVillage {

    public SiameseTwin(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }


    @Override
    public String getDescription() {
        return game.translate("werewolf.role.siamese_twin.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.siamese_twin.display";
    }


    @Override
    public void stolen(@NotNull UUID uuid) {

        getPlayerUUID();

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
    }

    @Override
    public Player recoverPower() {
        Player player = super.recoverPower();
        if (player == null) return null;
        VersionUtils.getVersionUtils().setPlayerMaxHealth(player, 24);
        return player;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {

        int counter = 0;
        double health = 0;
        for (UUID uuid : game.getPlayersWW().keySet()) {

            PlayerWW plg = game.getPlayersWW().get(uuid);
            Player c = Bukkit.getPlayer(uuid);

            if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && c != null) {
                counter++;
                health += c.getHealth() / VersionUtils.getVersionUtils().getPlayerMaxHealth(c);
            }
        }
        health /= counter;
        for (UUID uuid : game.getPlayersWW().keySet()) {

            PlayerWW plg = game.getPlayersWW().get(uuid);
            Player c = Bukkit.getPlayer(uuid);

            if (plg.isState(State.ALIVE) && plg.getRole().isDisplay("werewolf.role.siamese_twin.display") && c != null) {

                if (health * VersionUtils.getVersionUtils().getPlayerMaxHealth(c) > 10) {
                    if (health * VersionUtils.getVersionUtils().getPlayerMaxHealth(c) + 1 < c.getHealth()) {
                        Sounds.BURP.play(c);
                    }
                    c.setHealth(health * VersionUtils.getVersionUtils().getPlayerMaxHealth(c));
                }
            }
        }
    }
}
