package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.*;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.ALERTER_WEREWOLF,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        defaultAura = Aura.NEUTRAL,
        configValues = @IntValue(key = IntValueBase.ALERTER_DISTANCE,
                defaultValue = 20, meetUpValue = 20, step = 5, item = UniversalMaterial.COMPASS),
        configurations = {@Configuration(config = @ConfigurationBasic(key = "werewolf.roles.alerter_werewolf.configurations.alerter_werewolf_every_other_day", defaultValue = true))})
public class AlerterWereWolf extends RoleWereWolf implements IPower, IAffectedPlayers {

    public static final int PROXIMITY_REQUIRED = 180;

    private boolean power = false;
    @Nullable
    private IPlayerWW target;
    private int proximitySeconds = 0;
    private int dayNumber = -8;

    public AlerterWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.alerter_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .build();
    }

    @Override
    public void recoverPower() {
        this.setPower(false);
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onDay(DayEvent event) {
        if (game.getConfig().isConfigActive("werewolf.roles.alerter_werewolf.configurations.alerter_werewolf_every_other_day")
                && event.getNumber() == dayNumber + 1) {
            return;
        }
        dayNumber = event.getNumber();
        this.setPower(true);
        this.proximitySeconds = 0;
    }

    @Override
    public void second() {
        if (!this.isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
        if (this.target == null) return;

        if (this.getPlayerWW().distance(this.target) <= game.getConfig().getValue(IntValueBase.ALERTER_DISTANCE)) {
            this.proximitySeconds++;
        }

        if (this.proximitySeconds >= PROXIMITY_REQUIRED) {
            this.alert();
            this.setPower(false);
            this.clearAffectedPlayer();
            this.resetProximity();
        }
    }

    private void alert() {
        if (this.target == null) return;

        if (this.target.getRole().isWereWolf()) {
            return;
        }

        List<IPlayerWW> wolves = game.getAlivePlayersWW().stream()
                .filter(iPlayerWW -> !this.getPlayerWW().equals(iPlayerWW))
                .filter(iPlayerWW -> iPlayerWW.getRole().isWereWolf())
                .collect(Collectors.toList());

        if (wolves.isEmpty()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.alerter_werewolf.alert_no_wolf");
            return;
        }

        IPlayerWW alertedWolf = wolves.get(game.getRandom().nextInt(wolves.size()));

        alertedWolf.sendMessageWithKey(Prefix.YELLOW,
                "werewolf.roles.alerter_werewolf.alert_received",
                Formatter.player(this.getPlayerWW().getName()),
                Formatter.role(game.translate(this.target.getRole().getDisplayRole())));

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.alerter_werewolf.alerter_notified",
                Formatter.player(alertedWolf.getName()));
    }

    public void resetProximity() {
        this.proximitySeconds = 0;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW playerWW) {
        this.target = playerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW playerWW) {
        if (playerWW.equals(this.target)) {
            this.target = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.target = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        if (this.target == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.target);
    }
}
