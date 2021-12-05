package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.ComedianMask;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
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

    public Comedian(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        setPower(false);
    }


    public List<ComedianMask> getMasks() {
        return comedianMasks;
    }

    public Optional<ComedianMask> getLastMask() {
        if (comedianMasks.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(comedianMasks.get(comedianMasks.size() - 1));
    }


    public void addMask(ComedianMask mask) {
        this.comedianMasks.add(mask);
    }


    @EventHandler
    public void onDay(DayEvent event) {

        getLastMask().ifPresent(comedianMask -> this.getPlayerWW()
                .addPotionModifier(PotionModifier.remove(comedianMask.getPotionEffectType(),
                        "comedian",
                        0)));

        if (getMasks().size() >= 3) return;

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.comedian.wear_mask_message",
                Formatter.timer(Utils.conversion(
                        game.getConfig().getTimerValue(
                                TimerBase.POWER_DURATION.getKey()))));

    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.comedian.description"))
                .addExtraLines(game.translate("werewolf.role.comedian.masks",
                                Formatter.format("&mask&",comedianMasks.isEmpty() ?
                                game.translate("werewolf.role.comedian.none") :
                                comedianMasks.stream()
                                        .map(comedianMasks1 -> game.translate(comedianMasks1.getKey()))
                                        .collect(Collectors.joining(" ")))))
                .build();

    }


    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

}
