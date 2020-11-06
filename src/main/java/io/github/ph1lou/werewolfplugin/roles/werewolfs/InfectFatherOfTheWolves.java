package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
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

    public InfectFatherOfTheWolves(GetWereWolfAPI main, WereWolfAPI game, UUID uuid, String key) {
        super(main,game,uuid, key);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW plg = game.getPlayersWW().get(event.getUuid());
        UUID killerUUID = plg.getLastKiller();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;


        if (!game.getPlayersWW().containsKey(killerUUID)) {
            return;
        }

        if (!game.getPlayersWW().get(killerUUID).getRole().isWereWolf()) {
            return;
        }

        if (event.getUuid().equals(getPlayerUUID())) {
            event.setCancelled(autoResurrection(player));
            return;
        }

        if (!game.getPlayersWW().get(getPlayerUUID())
                .isState(StatePlayer.ALIVE)) return;

        TextComponent infect_msg = new TextComponent(
                game.translate(
                        "werewolf.role.infect_father_of_the_wolves.infection_message",
                        plg.getName()));
        infect_msg.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.role.infect_father_of_the_wolves.command"),
                                event.getUuid())));
        player.spigot().sendMessage(infect_msg);
    }

    private boolean autoResurrection(Player player) {

        if (!game.getConfig()
                .getConfigValues()
                .get(ConfigsBase.AUTO_REZ_INFECT.getKey())) {
            return false;
        }

        InfectionEvent infectionEvent =
                new InfectionEvent(getPlayerUUID(), getPlayerUUID());
        Bukkit.getPluginManager().callEvent(infectionEvent);
        setPower(false);

        if (!infectionEvent.isCancelled()) {
            game.resurrection(getPlayerUUID());
            return true;
        }

        player.sendMessage(game.translate("werewolf.check.cancel"));

        return false;
    }


}
