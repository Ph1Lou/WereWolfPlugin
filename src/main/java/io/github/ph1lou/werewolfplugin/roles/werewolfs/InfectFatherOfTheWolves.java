package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfectFatherOfTheWolves extends RolesWereWolf implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public InfectFatherOfTheWolves(GetWereWolfAPI main,
                                   PlayerWW playerWW,
                                   String key) {
        super(main, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
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
    public @NotNull String getDescription() {
        return game.translate("werewolf.role.infect_father_of_the_wolves.description");
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW playerWW = event.getPlayerWW();


        PlayerWW killerWW = playerWW.getLastKiller();
        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) return;

        if (killerWW == null) {
            return;
        }

        if (!killerWW.getRole().isWereWolf()) {
            return;
        }

        if (playerWW.equals(getPlayerWW())) {
            event.setCancelled(autoResurrection(player));
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        TextComponent infect_msg = new TextComponent(
                game.translate(
                        "werewolf.role.infect_father_of_the_wolves.infection_message",
                        playerWW.getName()));
        infect_msg.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.role.infect_father_of_the_wolves.command"),
                                playerWW.getUUID())));
        player.spigot().sendMessage(infect_msg);
    }

    private boolean autoResurrection(Player player) {

        if (!game.getConfig()
                .getConfigValues()
                .get(ConfigsBase.AUTO_REZ_INFECT.getKey())) {
            return false;
        }

        InfectionEvent infectionEvent =
                new InfectionEvent(getPlayerWW(), getPlayerWW());
        Bukkit.getPluginManager().callEvent(infectionEvent);
        setPower(false);

        if (!infectionEvent.isCancelled()) {
            game.resurrection(getPlayerWW());
            return true;
        }

        player.sendMessage(game.translate("werewolf.check.cancel"));

        return false;
    }


}
