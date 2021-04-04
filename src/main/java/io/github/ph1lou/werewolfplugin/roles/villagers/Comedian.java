package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.ComedianMask;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Comedian extends RoleWithLimitedSelectionDuration {

    private final List<ComedianMask> comedianMasks = new ArrayList<>();

    public Comedian(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);
        setPower(false);
    }


    public List<ComedianMask> getMasks() {
        return comedianMasks;
    }

    public Optional<ComedianMask> getLastMask() {

        return Optional.ofNullable(comedianMasks.get(comedianMasks.size() - 1));
    }


    public void addMask(ComedianMask mask) {
        this.comedianMasks.add(mask);
    }


    @EventHandler
    public void onDay(DayEvent event) {

        getLastMask().ifPresent(comedianMask -> getPlayerWW().removePotionEffect(comedianMask.getPotionEffectType()));

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (getMasks().size() >= 3) return;

        setPower(true);

        getPlayerWW().sendMessageWithKey("werewolf.role.comedian.wear_mask_message",
                Utils.conversion(
                        game.getConfig().getTimerValue(
                                TimersBase.POWER_DURATION.getKey())));

    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.comedian.description"))
                .addExtraLines(() -> game.translate("werewolf.role.comedian.masks",
                        comedianMasks.isEmpty() ?
                                game.translate("werewolf.role.comedian.none") :
                                comedianMasks.stream()
                                        .map(comedianMasks1 -> game.translate(comedianMasks1.getKey()))
                                        .collect(Collectors.joining(" "))))
                .build();

    }


    @Override
    public void recoverPower() {

    }

}
