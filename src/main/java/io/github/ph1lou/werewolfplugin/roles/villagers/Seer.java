package io.github.ph1lou.werewolfplugin.roles.villagers;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
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

public class Seer extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private int dayNumber = -8;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();

    private boolean disablePower = false;

    public Seer(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
        setPower(false);
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

    @EventHandler
    public void onDay(DayEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (game.getConfig().isSeerEveryOtherDay() &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        if (disablePower) {
            disablePower = false;
            this.getPlayerWW().sendMessageWithKey("werewolf.role.seer.disable");
            return;
        }

        setPower(true);

        this.getPlayerWW().sendMessageWithKey(
                "werewolf.role.seer.see_camp_message",
                Utils.conversion(
                        game.getConfig()
                                .getTimerValue(TimerBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.seer.description"))
                .setItems(game.translate("werewolf.role.seer.items"))
                .setEffects(game.translate("werewolf.role.seer.effect"))
                .build();
    }


    @Override
    public void recoverPower() {

    }


    @Override
    public void recoverPotionEffect() {

        this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.NIGHT_VISION,"seer"));
    }

    public void setDisablePower() {
        this.disablePower = true;
    }

    public static ClickableItem config(WereWolfAPI game) {
        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(Material.GOLD_BLOCK)
                        .setLore(game.translate(config.isSeerEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                        .setDisplayName(game.translate("werewolf.role.seer.seer_every_other_day"))
                        .build(), e -> {
                    config.setSeerEveryOtherDay(!config.isSeerEveryOtherDay());

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setLore(game.translate(config.isSeerEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                            .build());

                });
    }
}
