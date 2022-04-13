package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.enums.ConfigBase;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
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
                .setDescription(game.translate("werewolf.role.oracle.description"))
                .setEffects(game.translate("werewolf.role.oracle.effect"))
                .build();
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (game.getConfig().isConfigActive(ConfigBase.ORACLE_EVERY_OTHER_DAY.getKey()) &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(
                Prefix.YELLOW.getKey() , "werewolf.role.oracle.perform",
                Formatter.timer(Utils.conversion(
                        game.getConfig()
                                .getTimerValue(TimerBase.POWER_DURATION.getKey()))));
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {

        this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.NIGHT_VISION,"oracle",0));

    }
}
