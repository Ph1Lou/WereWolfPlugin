package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Seer extends RolesWithLimitedSelectionDuration implements AffectedPlayers {

    private int dayNumber = -8;
    private final List<PlayerWW> affectedPlayer = new ArrayList<>();

    private boolean disablePower = false;

    public Seer(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
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

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigsBase.SEER_EVERY_OTHER_DAY.getKey()) &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        if (disablePower) {
            disablePower = false;
            return;
        }

        setPower(true);

        getPlayerWW().sendMessage(game.translate(
                "werewolf.role.seer.see_camp_message",
                game.getScore().conversion(
                        game.getConfig()
                                .getTimerValue(TimersBase.POWER_DURATION.getKey()))));
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.seer.description")) +
                game.translate("werewolf.description.item", game.translate("werewolf.role.seer.items")) +
                game.translate("werewolf.description.effect", game.translate("werewolf.role.seer.effect"));
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();

        getPlayerWW().addPotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public void setDisablePower() {
        this.disablePower = true;
    }

}
