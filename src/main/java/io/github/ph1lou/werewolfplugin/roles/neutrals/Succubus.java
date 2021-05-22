package io.github.ph1lou.werewolfplugin.roles.neutrals;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import io.github.ph1lou.werewolfapi.events.lovers.AroundLover;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.CharmEvent;
import io.github.ph1lou.werewolfapi.events.roles.succubus.SuccubusResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IProgress;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Succubus extends RoleNeutral implements IProgress, IAffectedPlayers, IPower {

    private float progress = 0;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    public Succubus(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
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
    public float getProgress() {
        return (this.progress);
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.succubus.description",
                        Utils.conversion(game.getConfig().getTimerValue(TimersBase.SUCCUBUS_DURATION.getKey()))))
                .addExtraLines(() -> game.translate("werewolf.role.succubus.charm",
                        affectedPlayer.isEmpty() ? this.power ?
                                game.translate("werewolf.role.succubus.charm_command")
                                : game.translate("werewolf.role.succubus.none") :
                                affectedPlayer.get(0).getName()))
                .build();
    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        if (this.power) {
            this.affectedPlayer.clear();
            return;
        }

        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        IPlayerWW affectedWW = getAffectedPlayers().get(0);

        affectedWW.sendMessageWithKey("werewolf.role.succubus.get_charmed",
                getPlayerWW().getName());
    }

    @Override
    public void recoverPower() {
        getPlayerWW().sendMessageWithKey("werewolf.role.succubus.charming_message");
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }

    @Override
    public void second() {


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

        IPlayerWW charmedWW = getAffectedPlayers().get(0);
        Player charmed = Bukkit.getPlayer(charmedWW.getUUID());

        if (charmed == null) return;

        if (!charmedWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        Location succubusLocation = player.getLocation();
        Location playerLocation = charmed.getLocation();

        if (player.getWorld().equals(charmed.getWorld())) {
            if (succubusLocation.distance(playerLocation) >
                    game.getConfig().getDistanceSuccubus()) {
                return;
            }
        } else {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValue(TimersBase.SUCCUBUS_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValue(TimersBase.SUCCUBUS_DURATION.getKey()) + 1)) {
            getPlayerWW().sendMessageWithKey(
                    "werewolf.role.succubus.progress_charm",
                    Math.min(100, Math.floor(temp)));
        }

        if (temp >= 100) {

            CharmEvent charmEvent = new CharmEvent(getPlayerWW()
                    , charmedWW);
            Bukkit.getPluginManager().callEvent(charmEvent);

            if (!charmEvent.isCancelled()) {
                charmedWW.sendMessageWithKey(
                        "werewolf.role.succubus.get_charmed", Sound.PORTAL_TRAVEL,
                        getPlayerWW().getName());
                getPlayerWW().sendMessageWithKey(
                        "werewolf.role.succubus.charming_perform",
                        charmed.getName());
                game.checkVictory(); //pose soucis quand que 2 joueurs
            } else {
                getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            }

            setProgress(0f);
            setPower(false);
        }

    }
    @EventHandler
    public void onTargetIsStolen(StealEvent event) {


        IPlayerWW playerWW = event.getPlayerWW();
        IPlayerWW thiefWW = event.getThiefWW();

        if (!getAffectedPlayers().contains(playerWW)) return;

        removeAffectedPlayer(playerWW);
        addAffectedPlayer(thiefWW);

        thiefWW.sendMessageWithKey("werewolf.role.succubus.get_charmed",
                getPlayerWW().getName());

        playerWW.sendMessageWithKey("werewolf.role.succubus.change",
                thiefWW.getName());
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        if (!getAffectedPlayers().contains(event.getPlayerWW())) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        getPlayerWW().sendMessageWithKey("werewolf.role.succubus.charming_message");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().equals(event.getPlayerWW())) return;

        if (getAffectedPlayers().isEmpty()) return;

        if (hasPower()) return;


        IPlayerWW targetWW = getAffectedPlayers().get(0);
        Player target = Bukkit.getPlayer(targetWW.getUUID());

        if (!targetWW.isState(StatePlayer.ALIVE)) return;

        SuccubusResurrectionEvent succubusResurrectionEvent =
                new SuccubusResurrectionEvent(getPlayerWW(), targetWW);

        Bukkit.getPluginManager().callEvent(succubusResurrectionEvent);

        if (succubusResurrectionEvent.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
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

        IPlayerWW affectedWW = affectedPlayer.get(0);

        if (!affectedWW.isState(StatePlayer.ALIVE)) return;

        List<IPlayerWW> list = new ArrayList<>(Collections.singleton(affectedPlayer.get(0)));


        for (int i = 0; i < list.size(); i++) {

            IPlayerWW playerWW = list.get(i);

            game.getPlayerWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .map(IPlayerWW::getRole)
                    .filter(roles -> roles.isKey(RolesBase.SUCCUBUS.getKey()))
                    .forEach(role -> {
                        if (((IAffectedPlayers) role).getAffectedPlayers().contains(playerWW)) {
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
            for (IPlayerWW playerWW : affectedPlayer) {
                event.addPlayer(playerWW);
            }
            return;
        }

        for (IPlayerWW playerWW : event.getPlayerWWS()) {
            if (affectedPlayer.contains(playerWW)) {
                event.addPlayer(getPlayerWW());
                break;
            }
        }
    }

}
