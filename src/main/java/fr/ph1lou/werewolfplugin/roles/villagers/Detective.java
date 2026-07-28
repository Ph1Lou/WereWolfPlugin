package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
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
        attribute = RoleAttribute.INFORMATION,
        defaultAura = Aura.LIGHT,
        configurations = {@Configuration(config = @ConfigurationBasic(key = ConfigBase.DETECTIVE_EVERY_OTHER_DAY))})
public class Detective extends RoleWithLimitedSelectionDuration implements IAffectedPlayers {

    private static final String BLUE = "bleu";
    private static final String YELLOW = "jaune";
    private static final int FIRST_EPISODE = 3;
    private static final int GUEST_EPISODE = 5;

    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    private int dayNumber = -8;
    private boolean sameIsBlue;
    private boolean powerLost = false;
    private boolean canGuess = false;

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

        if (event.getNumber() < FIRST_EPISODE) {
            return;
        }

        if (game.getConfig().isConfigActive(ConfigBase.DETECTIVE_EVERY_OTHER_DAY) &&
                event.getNumber() == dayNumber + 1) {
            return;
        }

        dayNumber = event.getNumber();
        this.canGuess = false;

        if (this.powerLost) {
            return;
        }

        setPower(true);

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.detective.inspection_message",
                Formatter.timer(game, TimerBase.POWER_DURATION));
    }

    public String getCode(boolean sameCamp) {
        String sameColor = this.sameIsBlue ? BLUE : YELLOW;
        String otherColor = this.sameIsBlue ? YELLOW : BLUE;
        return sameCamp ? sameColor : otherColor;
    }

    public boolean isSameColorCode(String color) {
        String sameColor = this.sameIsBlue ? BLUE : YELLOW;
        return sameColor.equalsIgnoreCase(color);
    }

    public void onInvestigated() {
        this.canGuess = this.dayNumber >= GUEST_EPISODE;
    }

    public boolean canGuess() {
        return this.canGuess && !this.powerLost;
    }

    public void clearGuess() {
        this.canGuess = false;
    }

    public boolean isPowerLost() {
        return this.powerLost;
    }

    public void setPowerLost(boolean powerLost) {
        this.powerLost = powerLost;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.detective.description"))
                .build();
    }

    @Override
    public void recoverPower() {
        this.sameIsBlue = game.getRandom().nextBoolean();
    }
}
