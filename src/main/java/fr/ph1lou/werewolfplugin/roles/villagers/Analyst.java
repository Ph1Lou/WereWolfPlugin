package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Role(key = RoleBase.ANALYST,
        category = Category.VILLAGER,
        auraDescriptionSpecialUseCase = "werewolf.roles.analyst.aura",
        attribute = RoleAttribute.MINOR_INFORMATION,
        timers = { @Timer(key = TimerBase.ANALYSE_DURATION, defaultValue = 1800,
                meetUpValue = 6 * 60,
                decrementAfterRole = true) })
public class Analyst extends RoleImpl implements ILimitedUse, IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private int use = 0;
    private boolean power = true;
    private boolean coolDownDisabledPower = false;

    public Analyst(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.analyst.description",
                        Formatter.number(5 - this.use),
                        Formatter.timer(
                                Utils.conversion(
                                        Math.max(0,
                                                Math.max(0,
                                                        game.getConfig().getTimerValue(TimerBase.ROLE_DURATION)) +
                                                game.getConfig().getTimerValue(TimerBase.ANALYSE_DURATION))))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public int getUse() {
        return this.use;
    }

    @Override
    public void setUse(int use) {
        this.use = use;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public boolean isCoolDownDisabledPower() {
        return coolDownDisabledPower;
    }

    public void setCoolDownDisabledPower(boolean coolDownDisabledPower) {
        this.coolDownDisabledPower = coolDownDisabledPower;
    }

}
