package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Oracle extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private int dayNumber = -8;
    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    public Oracle(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
        setPower(false);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.oracle.description"))
                .setEffects(() -> game.translate("werewolf.role.oracle.effect"))
                .build();
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (game.getConfig().isOracleEveryOtherDay() &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        setPower(true);

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.oracle.perform",
                Utils.conversion(
                        game.getConfig()
                                .getTimerValue(TimersBase.POWER_DURATION.getKey())));
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        getPlayerWW().addPotionEffect(PotionEffectType.NIGHT_VISION);
    }
}
