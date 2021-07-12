package io.github.ph1lou.werewolfplugin.roles.villagers;


import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleWithLimitedSelectionDuration;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import io.github.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Detective extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {


    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int dayNumber = -8;

    public Detective(WereWolfAPI api, IPlayerWW playerWW, String key) {
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

        if (game.getConfig().isDetectiveEveryOtherDay() &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        setPower(true);


        this.getPlayerWW().sendMessageWithKey("werewolf.role.detective.inspection_message",
                Utils.conversion(game.getConfig().getTimerValue(TimerBase.POWER_DURATION.getKey())));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.detective.description"))
                .build();
    }


    @Override
    public void recoverPower() {

    }

    public static ClickableItem config(WereWolfAPI game) {

        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(UniversalMaterial.LEAD.getType())
                        .setLore(game.translate(config.isDetectiveEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                        .setDisplayName(game.translate("werewolf.role.detective.detective_every_other_day"))
                        .build(), e -> {
                    config.setDetectiveEveryOtherDay(!config.isDetectiveEveryOtherDay());

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setLore(game.translate(config.isDetectiveEveryOtherDay() ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                            .build());

                });
    }
}
