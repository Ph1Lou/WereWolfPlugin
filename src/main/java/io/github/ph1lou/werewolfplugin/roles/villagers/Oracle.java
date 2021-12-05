package io.github.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Material;
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

        if (game.getConfig().isOracleEveryOtherDay() &&
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

    public static ClickableItem config(WereWolfAPI game) {

        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(Material.DIAMOND_BLOCK)
                        .setLore(game.translate(config.isOracleEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                        .setDisplayName(game.translate("werewolf.role.oracle.oracle_every_other_day"))
                        .build(), e -> {
                    config.setOracleEveryOtherDay(!config.isOracleEveryOtherDay());

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setLore(game.translate(config.isOracleEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                            .build());

                });
    }
}
