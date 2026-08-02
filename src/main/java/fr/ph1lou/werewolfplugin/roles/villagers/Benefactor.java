package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Role(key = RoleBase.BENEFACTOR,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        defaultAura = Aura.LIGHT,
        configValues = {@IntValue(key = IntValueBase.BENEFACTOR_MIN_CLIENTS,
                defaultValue = 3, meetUpValue = 3, step = 1, item = UniversalMaterial.PLAYER_HEAD),
                @IntValue(key = IntValueBase.BENEFACTOR_REGEN_HEALTH,
                        defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.RED_DYE)},
        timers = {@Timer(key = TimerBase.BENEFACTOR_COOLDOWN,
                defaultValue = 5 * 60, meetUpValue = 5 * 60, step = 30),
                @Timer(key = TimerBase.BENEFACTOR_REGEN_INTERVAL,
                        defaultValue = 60, meetUpValue = 60, step = 5)})
public class Benefactor extends RoleImpl implements IAffectedPlayers {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private int lastUseTimer;

    public Benefactor(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this)
                .setDescription(this.game.translate("werewolf.roles.benefactor.description",
                        Formatter.timer(this.game, TimerBase.BENEFACTOR_COOLDOWN)))
                .setItems(this.game.translate("werewolf.roles.benefactor.items"))
                .setCommand(this.game.translate("werewolf.roles.benefactor.command_description"))
                .setEffects(this.game.translate("werewolf.roles.benefactor.effect"))
                .build();
    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) return;

        if (this.affectedPlayers.size() < this.game.getConfig().getValue(IntValueBase.BENEFACTOR_MIN_CLIENTS)) return;

        if (this.game.getTimer() % this.game.getConfig().getTimerValue(TimerBase.BENEFACTOR_REGEN_INTERVAL) != 0) return;

        this.getPlayerWW().addPlayerHealth(this.game.getConfig().getValue(IntValueBase.BENEFACTOR_REGEN_HEALTH));
    }

    @Override
    public void recoverPower() {
        this.lastUseTimer = -this.game.getConfig().getTimerValue(TimerBase.BENEFACTOR_COOLDOWN);
    }

    public boolean canGive() {
        return this.game.getTimer() - this.lastUseTimer >= this.game.getConfig().getTimerValue(TimerBase.BENEFACTOR_COOLDOWN);
    }

    public void onGiven() {
        this.lastUseTimer = this.game.getTimer();
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
}