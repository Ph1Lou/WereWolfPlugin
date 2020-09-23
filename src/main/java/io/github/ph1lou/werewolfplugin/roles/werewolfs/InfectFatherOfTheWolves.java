package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.InfectionEvent;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfectFatherOfTheWolves extends RolesWereWolf implements AffectedPlayers, Power {

    private final List<UUID> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public InfectFatherOfTheWolves(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
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
        return game.translate("werewolf.role.infect_father_of_the_wolves.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.infect_father_of_the_wolves.display";
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW plg = game.getPlayersWW().get(event.getUuid());
        UUID killerUUID = plg.getLastKiller();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (game.getPlayersWW().containsKey(killerUUID) && game.getPlayersWW().get(killerUUID).getRole().isWereWolf()) {

            if (event.getUuid().equals(getPlayerUUID())) {
                if (game.getConfig().getConfigValues().get("werewolf.menu.global.auto_rez_infect")) {
                    InfectionEvent infectionEvent = new InfectionEvent(event.getUuid(), event.getUuid());
                    Bukkit.getPluginManager().callEvent(infectionEvent);
                    setPower(false);

                    if (infectionEvent.isCancelled()) {
                        if (player != null) {
                            player.sendMessage(game.translate("werewolf.check.cancel"));
                        }
                    } else {
                        game.resurrection(getPlayerUUID());
                        event.setCancelled(true);
                    }

                }
            } else {
                plg.setCanBeInfect(true);

                if (!game.getPlayersWW().get(getPlayerUUID()).isState(State.ALIVE) ) return;

                if (player != null) {
                    TextComponent infect_msg = new TextComponent(game.translate("werewolf.role.infect_father_of_the_wolves.infection_message", plg.getName()));
                    infect_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ww " + game.translate("werewolf.role.infect_father_of_the_wolves.command") + " " + event.getUuid()));
                    player.spigot().sendMessage(infect_msg);
                }
            }
        }
    }


}
