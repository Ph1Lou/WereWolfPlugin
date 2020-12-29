package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.Sounds;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.*;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Progress;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesNeutral;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Succubus extends RolesNeutral implements Progress, AffectedPlayers, Power {

    private float progress = 0;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    public Succubus(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }

    private boolean power = true;

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
    public float getProgress() {
        return (this.progress);
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
    }

    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.succubus.description")) +
                game.translate("werewolf.description._");
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        if (hasPower()) {
            getPlayerWW().sendMessage(game.translate(
                    "werewolf.role.succubus.charming_message"));
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        PlayerWW affectedWW = getAffectedPlayers().get(0);
        Player affected = Bukkit.getPlayer(affectedWW.getUUID());

        getPlayerWW().sendMessage(game.translate(
                "werewolf.role.succubus.charming_perform",
                affectedWW.getName()));

        if (affected != null) {
            affected.sendMessage(game.translate(
                    "werewolf.role.succubus.get_charmed",
                    getPlayerWW().getName()));
        }
    }

    @Override
    public void recoverPower() {

        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        player.sendMessage(game.translate(
                "werewolf.role.succubus.charming_message"));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {


        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        if (!hasPower()) {
            return;
        }

        PlayerWW charmedWW = getAffectedPlayers().get(0);
        Player charmed = Bukkit.getPlayer(charmedWW.getUUID());

        if (charmed == null) return;

        if (!charmedWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        Location succubusLocation = player.getLocation();
        Location playerLocation = charmed.getLocation();

        try {
            if (succubusLocation.distance(playerLocation) >
                    game.getConfig().getDistanceSuccubus()) {
                return;
            }
        } catch (Exception ignored) {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValue(TimersBase.SUCCUBUS_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValue(TimersBase.SUCCUBUS_DURATION.getKey()) + 1)) {
            player.sendMessage(game.translate(
                    "werewolf.role.succubus.progress_charm",
                    Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            CharmEvent charmEvent = new CharmEvent(getPlayerWW()
                    , charmedWW);
            Bukkit.getPluginManager().callEvent(charmEvent);

            if (!charmEvent.isCancelled()) {
                Sounds.PORTAL_TRAVEL.play(charmed);
                charmed.sendMessage(game.translate(
                        "werewolf.role.succubus.get_charmed",
                        getPlayerWW().getName()));
                player.sendMessage(game.translate(
                        "werewolf.role.succubus.charming_perform",
                        charmed.getName()));
                game.checkVictory(); //pose soucis quand que 2 joueurs
            } else player.sendMessage(game.translate("werewolf.check.cancel"));

            setProgress(0f);
            setPower(false);
        }

    }
    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        PlayerWW playerWW = event.getPlayerWW();
        PlayerWW thiefWW = event.getThiefWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        thiefWW.sendMessage(game.translate(
                "werewolf.role.succubus.get_charmed",
                getPlayerWW().getName()));

        playerWW.sendMessage(game.translate(
                "werewolf.role.succubus.change",
                thiefWW.getName()));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        if (!getAffectedPlayers().contains(event.getPlayerWW())) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        getPlayerWW().sendMessage(game.translate(
                "werewolf.role.succubus.charming_message"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().equals(event.getPlayerWW())) return;

        if (getAffectedPlayers().isEmpty()) return;

        if (hasPower()) return;


        PlayerWW targetWW = getAffectedPlayers().get(0);
        Player target = Bukkit.getPlayer(targetWW.getUUID());

        if (!targetWW.isState(StatePlayer.ALIVE)) return;

        SuccubusResurrectionEvent succubusResurrectionEvent =
                new SuccubusResurrectionEvent(getPlayerWW(), targetWW);

        Bukkit.getPluginManager().callEvent(succubusResurrectionEvent);

        if (succubusResurrectionEvent.isCancelled()) {
            getPlayerWW().sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        clearAffectedPlayer();
        event.setCancelled(true);

        if (target == null) {
            game.death(targetWW);
        } else {
            target.damage(10000);
            target.sendMessage(game.translate(
                    "werewolf.role.succubus.free_of_succubus"));
        }

        game.resurrection(getPlayerWW());
    }

    @EventHandler
    public void onDetectVictoryWitchCharmed(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (affectedPlayer.isEmpty()) return;

        PlayerWW affectedWW = affectedPlayer.get(0);

        if (!affectedWW.isState(StatePlayer.ALIVE)) return;

        List<PlayerWW> list = new ArrayList<>(Collections.singleton(affectedPlayer.get(0)));


        for (int i = 0; i < list.size(); i++) {

            PlayerWW playerWW = list.get(i);

            game.getPlayerWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .map(PlayerWW::getRole)
                    .filter(roles -> roles.isKey(RolesBase.SUCCUBUS.getKey()))
                    .forEach(role -> {
                        if (((AffectedPlayers) role).getAffectedPlayers().contains(playerWW)) {
                            if (!list.contains(role.getPlayerWW())) {
                                list.add(role.getPlayerWW());
                            }
                        }
                    });

        }

        if (game.getScore().getPlayerSize() == list.size()) {
            event.setCancelled(true);
            event.setVictoryTeam(RolesBase.SUCCUBUS.getKey());
        }
    }

    @EventHandler
    public void onLover(AroundLover event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (event.getPlayerWWS().contains(getPlayerWW())) {
            for (PlayerWW playerWW : affectedPlayer) {
                event.addPlayer(playerWW);
            }
            return;
        }

        for (PlayerWW playerWW : event.getPlayerWWS()) {
            if (affectedPlayer.contains(playerWW)) {
                event.addPlayer(getPlayerWW());
                break;
            }
        }
    }

}
