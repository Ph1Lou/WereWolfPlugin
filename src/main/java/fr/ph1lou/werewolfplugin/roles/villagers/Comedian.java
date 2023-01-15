package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.ComedianMask;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.COMEDIAN,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER)
public class Comedian extends RoleWithLimitedSelectionDuration {

    private final List<ComedianMask> comedianMasks = new ArrayList<>();

    public Comedian(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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
                        this.getKey(),
                        0)));

        if (getMasks().size() >= 3) return;

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.comedian.wear_mask_message",
                Formatter.timer(game, TimerBase.POWER_DURATION));

    }

    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.comedian.description"))
                .addExtraLines(game.translate("werewolf.roles.comedian.masks",
                        Formatter.format("&mask&", comedianMasks.isEmpty() ?
                                game.translate("werewolf.roles.comedian.none") :
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
