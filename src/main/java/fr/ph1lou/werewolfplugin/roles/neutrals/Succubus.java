package fr.ph1lou.werewolfplugin.roles.neutrals;


import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.SecondDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.events.lovers.AroundLoverEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.CharmEvent;
import fr.ph1lou.werewolfapi.events.roles.succubus.SuccubusResurrectionEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IProgress;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Role(key = RoleBase.SUCCUBUS,
        category = Category.NEUTRAL,
        defaultAura = Aura.DARK,
        attribute = RoleAttribute.NEUTRAL,
        timers = @Timer(key = TimerBase.SUCCUBUS_DURATION, defaultValue = 180, meetUpValue = 120),
        configValues = @IntValue(key = IntValueBase.SUCCUBUS_DISTANCE,
                defaultValue = 20,
                meetUpValue = 20,
                step = 4,
                item = UniversalMaterial.PURPLE_WOOL))
public class Succubus extends RoleNeutral implements IProgress, IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private float progress = 0;
    private boolean power = true;

    public Succubus(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
                .setDescription(game.translate("werewolf.roles.succubus.description",
                        Formatter.timer(
                                game, TimerBase.SUCCUBUS_DURATION)))
                .setPower(game.translate("werewolf.roles.succubus.progress_charm",
                        Formatter.format("&progress&", Math.min(100, Math.floor(this.getProgress())))))
                .addExtraLines(game.translate("werewolf.roles.succubus.charm",
                        Formatter.format("&list&", affectedPlayer.isEmpty() ? this.power ?
                                game.translate("werewolf.roles.succubus.charm_command")
                                : game.translate("werewolf.roles.succubus.none") :
                                affectedPlayer.get(0).getName())))
                .build();
    }

    @Override
    public void recoverPower() {
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.succubus.charming_message");
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

        if (this.getPlayerWW().distance(charmedWW) > game.getConfig().getValue(IntValueBase.SUCCUBUS_DISTANCE)) {
            return;
        }

        float temp = getProgress() + 100f /
                                     (game.getConfig().getTimerValue(TimerBase.SUCCUBUS_DURATION) + 1);

        setProgress(temp);

        if (temp % 10 > 0 && temp % 10 <= 100f /
                                          (game.getConfig().getTimerValue(TimerBase.SUCCUBUS_DURATION) + 1)) {
            this.getPlayerWW().sendMessageWithKey(
                    Prefix.YELLOW, "werewolf.roles.succubus.progress_charm",
                    Formatter.format("&progress&", Math.min(100, Math.floor(temp))));
        }

        if (temp >= 100) {

            CharmEvent charmEvent = new CharmEvent(this.getPlayerWW()
                    , charmedWW);
            Bukkit.getPluginManager().callEvent(charmEvent);

            setProgress(0f);
            setPower(false);

            if (!charmEvent.isCancelled()) {
                charmedWW.sendMessageWithKey(
                        Prefix.YELLOW, "werewolf.roles.succubus.get_charmed",
                        Formatter.player(this.getPlayerWW().getName()));
                charmedWW.sendSound(Sound.PORTAL_TRAVEL);
                this.getPlayerWW().sendMessageWithKey(
                        Prefix.GREEN, "werewolf.roles.succubus.charming_perform",
                        Formatter.player(charmedWW.getName()));
                game.checkVictory();
            } else {
                this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            }
        }

    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {


        if (!getAffectedPlayers().contains(event.getPlayerWW())) return;

        clearAffectedPlayer();
        setPower(true);
        setProgress(0f);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.succubus.charming_message");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSecondDeathEvent(SecondDeathEvent event) {

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
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        clearAffectedPlayer();
        event.setCancelled(true);

        if (target == null) {
            game.death(targetWW);
        } else {
            target.damage(10000);
            target.sendMessage(game.translate(
                    Prefix.YELLOW, "werewolf.roles.succubus.free_of_succubus"));
        }

        game.resurrection(getPlayerWW());
    }

    @EventHandler
    public void onDetectVictoryWitchCharmed(WinConditionsCheckEvent event) {

        if (event.isWin()) return;

        if (hasPower()) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (affectedPlayer.isEmpty()) return;

        IPlayerWW affectedWW = affectedPlayer.get(0);

        if (!affectedWW.isState(StatePlayer.ALIVE)) return;

        List<IPlayerWW> list = new ArrayList<>(Collections.singleton(affectedPlayer.get(0)));


        for (int i = 0; i < list.size(); i++) {

            IPlayerWW playerWW = list.get(i);

            game.getAlivePlayersWW()
                    .stream()
                    .map(IPlayerWW::getRole)
                    .filter(roles -> roles.isKey(RoleBase.SUCCUBUS))
                    .forEach(role -> {
                        if (((IAffectedPlayers) role).getAffectedPlayers().contains(playerWW)) {
                            if (!list.contains(role.getPlayerWW())) {
                                list.add(role.getPlayerWW());
                            }
                        }
                    });

        }

        if (game.getPlayersCount() == list.size()) {
            event.setWin();
            event.setVictoryTeam(RoleBase.SUCCUBUS);
        }
    }

    @EventHandler
    public void onLover(AroundLoverEvent event) {

        if (hasPower()) return;

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

}
