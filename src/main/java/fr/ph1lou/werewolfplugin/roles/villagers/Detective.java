package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWithLimitedSelectionDuration;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.DETECTIVE,
        category = Category.VILLAGER,
        attributes = {RoleAttribute.VILLAGER,
                RoleAttribute.INFORMATION},
        configurations = {@Configuration(config = @ConfigurationBasic(key = ConfigBase.DETECTIVE_EVERY_OTHER_DAY))})
public class Detective extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int dayNumber = -8;

    public Detective(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
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

        if (game.getConfig().isConfigActive(ConfigBase.DETECTIVE_EVERY_OTHER_DAY) &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW , "werewolf.roles.detective.inspection_message",
                Formatter.timer(game, TimerBase.POWER_DURATION));
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.detective.description"))
                .build();
    }


    @Override
    public void recoverPower() {

    }
}
