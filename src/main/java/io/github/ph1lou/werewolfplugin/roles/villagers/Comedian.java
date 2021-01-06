package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.ComedianMask;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWithLimitedSelectionDuration;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Comedian extends RolesWithLimitedSelectionDuration {

    private final List<ComedianMask> comedianMasks = new ArrayList<>();

    public Comedian(GetWereWolfAPI main, PlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }


    public List<ComedianMask> getMasks() {
        return comedianMasks;
    }

    @Nullable
    public ComedianMask getLastMask() {
        if (comedianMasks.isEmpty()) return null;
        return comedianMasks.get(comedianMasks.size() - 1);
    }


    public void addMask(ComedianMask mask) {
        this.comedianMasks.add(mask);
    }


    public void removeMask(ComedianMask mask) {
        this.comedianMasks.remove(mask);
    }


    @EventHandler
    public void onDay(DayEvent event) {

        if (getLastMask() != null) {
            getPlayerWW().removePotionEffect(getLastMask().getPotionEffectType());
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (getMasks().size() >= 3) return;

        setPower(true);

        getPlayerWW().sendMessage(game.translate("werewolf.role.comedian.wear_mask_message",
                game.getScore().conversion(
                        game.getConfig().getTimerValue(
                                TimersBase.POWER_DURATION.getKey()))));

    }

    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description",
                        game.translate("werewolf.role.comedian.description")) +
                game.translate("werewolf.role.comedian.masks",
                        comedianMasks.isEmpty() ?
                                game.translate("werewolf.role.comedian.none") :
                                comedianMasks.stream()
                                        .map(comedianMasks1 -> game.translate(comedianMasks1.getKey()))
                                        .collect(Collectors.joining(" ")));
    }


    @Override
    public void recoverPower() {

    }

}
