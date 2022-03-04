package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.CharmEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.SuccubusResurrectionEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
                .setDescription(game.translate("werewolf.role.succubus.description",
                                Formatter.timer(
                                        Utils.conversion(game.getConfig()
                                                .getTimerValue(TimerBase.SUCCUBUS_DURATION.getKey())))))
                .setPower(game.translate( "werewolf.role.succubus.progress_charm",
                        Formatter.format("&progress&",Math.min(100, Math.floor(this.getProgress())))))
                .addExtraLines(game.translate("werewolf.role.succubus.charm",
                                Formatter.format("&list&",affectedPlayer.isEmpty() ? this.power ?
                                game.translate("werewolf.role.succubus.charm_command")
                                : game.translate("werewolf.role.succubus.none") :
                                affectedPlayer.get(0).getName())))
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

        affectedWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.succubus.get_charmed",
                Formatter.player(this.getPlayerWW().getName()));
    }

    @Override
    public void recoverPower() {
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.succubus.charming_message");
    }

    @Override
    public Aura getDefaultAura() {
        return Aura.DARK;
    }

    @Override
    public void second() {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (getAffectedPlayers().isEmpty()) {
            return;
        }

        if (!hasPower()) {
            return;
        }

        IPlayerWW charmedWW = getAffectedPlayers().get(0);

        if (!charmedWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        Location succubusLocation = this.getPlayerWW().getLocation();
        Location playerLocation = charmedWW.getLocation();

        if (succubusLocation.getWorld() == playerLocation.getWorld()) {
            if (succubusLocation.distance(playerLocation) >
                    game.getConfig().getDistanceSuccubus()) {
                return;
            }
        } else {
            return;
        }

        float temp = getProgress() + 100f /
                (game.getConfig().getTimerValue(TimerBase.SUCCUBUS_DURATION.getKey()) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                (game.getConfig().getTimerValue(TimerBase.SUCCUBUS_DURATION.getKey()) + 1)) {
            this.getPlayerWW().sendMessageWithKey(
                    Prefix.YELLOW.getKey() , "werewolf.role.succubus.progress_charm",
                    Formatter.format("&progress&",Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            CharmEvent charmEvent = new CharmEvent(this.getPlayerWW()
                    , charmedWW);
            Bukkit.getPluginManager().callEvent(charmEvent);

            if (!charmEvent.isCancelled()) {
                charmedWW.sendMessageWithKey(
                        Prefix.YELLOW.getKey() , "werewolf.role.succubus.get_charmed",
                        Formatter.player(this.getPlayerWW().getName()));
                charmedWW.sendSound(Sound.PORTAL_TRAVEL);
                this.getPlayerWW().sendMessageWithKey(
                        Prefix.GREEN.getKey() , "werewolf.role.succubus.charming_perform",
                        Formatter.player(charmedWW.getName()));
                game.checkVictory(); //todo pose soucis quand que 2 joueurs
            } else {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
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

        thiefWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.succubus.get_charmed",
                Formatter.player(this.getPlayerWW().getName()));

        playerWW.sendMessageWithKey(Prefix.ORANGE.getKey() , "werewolf.role.succubus.change",
                Formatter.player(thiefWW.getName()));
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        if (!getAffectedPlayers().contains(event.getPlayerWW())) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.succubus.charming_message");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().equals(event.getPlayerWW())) return;

        if (getAffectedPlayers().isEmpty()) return;

        if (hasPower()) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW targetWW = getAffectedPlayers().get(0);
        Player target = Bukkit.getPlayer(targetWW.getUUID());

        if (!targetWW.isState(StatePlayer.ALIVE)) return;

        SuccubusResurrectionEvent succubusResurrectionEvent =
                new SuccubusResurrectionEvent(this.getPlayerWW(), targetWW);

        Bukkit.getPluginManager().callEvent(succubusResurrectionEvent);

        if (succubusResurrectionEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        clearAffectedPlayer();
        event.setCancelled(true);

        if (target == null) {
            game.death(targetWW);
        } else {
            target.damage(10000);
            target.sendMessage(game.translate(
                    Prefix.YELLOW.getKey() , "werewolf.role.succubus.free_of_succubus"));
        }

        game.resurrection(getPlayerWW());
    }

    @EventHandler
    public void onDetectVictoryWitchCharmed(WinConditionsCheckEvent event) {

        if (event.isCancelled()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (affectedPlayer.isEmpty()) return;

        IPlayerWW affectedWW = affectedPlayer.get(0);

        if (!affectedWW.isState(StatePlayer.ALIVE)) return;

        List<IPlayerWW> list = new ArrayList<>(Collections.singleton(affectedPlayer.get(0)));


        for (int i = 0; i < list.size(); i++) {

            IPlayerWW playerWW = list.get(i);

            game.getPlayersWW()
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

        if (game.getPlayersCount() == list.size()) {
            event.setCancelled(true);
            event.setVictoryTeam(RolesBase.SUCCUBUS.getKey());
        }
    }

    @EventHandler
    public void onLover(AroundLoverEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

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

    public static ClickableItem config(WereWolfAPI game) {

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((new ItemBuilder(
                UniversalMaterial.PURPLE_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.succubus",
                                Formatter.number(config.getDistanceSuccubus())))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceSuccubus((config.getDistanceSuccubus() + 5));
            } else if (config.getDistanceSuccubus() - 5 > 0) {
                config.setDistanceSuccubus(config.getDistanceSuccubus() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.succubus",
                                    Formatter.number(config.getDistanceSuccubus())))
                    .build());

        });
    }
}
