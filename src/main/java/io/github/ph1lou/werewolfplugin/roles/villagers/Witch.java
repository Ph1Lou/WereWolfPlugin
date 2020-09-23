package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Witch extends RolesVillage implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public Witch(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

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

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.witch.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.witch.display";
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW plg = game.getPlayersWW().get(event.getUuid());
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (event.getUuid().equals(getPlayerUUID())) {
            if (game.getConfig().getConfigValues().get("werewolf.menu.global.auto_rez_witch")) {
                WitchResurrectionEvent witchResurrectionEvent = new WitchResurrectionEvent(getPlayerUUID(), event.getUuid());
                Bukkit.getPluginManager().callEvent(witchResurrectionEvent);
                setPower(false);

                if (witchResurrectionEvent.isCancelled()) {
                    if (player != null) {
                        player.sendMessage(game.translate("werewolf.check.cancel"));
                    }
                } else {
                    game.resurrection(getPlayerUUID());
                    event.setCancelled(true);
                }
            }
        } else {

            if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE) ) return;

            if (player != null) {
                TextComponent witch_msg = new TextComponent(game.translate("werewolf.role.witch.resuscitation_message", plg.getName()));
                witch_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ww " + game.translate("werewolf.role.witch.command") + " " + event.getUuid()));
                player.spigot().sendMessage(witch_msg);
            }
        }
    }

}
