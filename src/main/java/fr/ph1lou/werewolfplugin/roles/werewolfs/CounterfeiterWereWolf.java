package fr.ph1lou.werewolfplugin.roles.werewolfs;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.counterfeiter_werewolf.CounterfeiterFalsifyEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.COUNTERFEITER_WEREWOLF,
        auraDescriptionSpecialUseCase = "werewolf.roles.counterfeiter_werewolf.aura",
        defaultAura = Aura.NEUTRAL,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        configValues = @IntValue(key = IntValueBase.COUNTERFEITER_HEART_LOSS,
                defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.RED_DYE))
public class CounterfeiterWereWolf extends RoleWereWolf implements IAffectedPlayers, IPower {

    public static final String AURA_MODIFIER_NAME = "counterfeiter_werewolf_facade";

    private boolean power = true;
    private IPlayerWW falsifiedTarget = null;
    private String savedDisplayRole = null;
    private String savedDisplayCamp = null;

    public CounterfeiterWereWolf(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.counterfeiter_werewolf.description"))
                .setEffects(game.translate("werewolf.description.werewolf"))
                .setPower(game.translate("werewolf.roles.counterfeiter_werewolf.power"))
                .setCommand(game.translate("werewolf.roles.counterfeiter_werewolf.commands"))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    public void falsify(IPlayerWW target) {

        if (target.equals(this.falsifiedTarget)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.counterfeiter_werewolf.already_use_on_player");
            return;
        }

        boolean targetIsWolf = target.getRole().isWereWolf();
        Aura facadeAura = targetIsWolf ? Aura.LIGHT : Aura.DARK;

        List<IRole> pool = game.getAlivePlayersWW().stream()
                .filter(p -> !p.equals(target))
                .filter(p -> !p.equals(this.getPlayerWW()))
                .map(IPlayerWW::getRole)
                .filter(r -> targetIsWolf ? !r.isWereWolf() : (r.isWereWolf() || r.isSolitary()))
                .collect(Collectors.toList());

        if (pool.isEmpty()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.counterfeiter_werewolf.no_role_available");
            return;
        }

        Collections.shuffle(pool, game.getRandom());
        IRole facadeSource = pool.get(0);
        String facadeRole = facadeSource.getKey();
        String facadeCamp = facadeSource.getCamp().getKey();

        CounterfeiterFalsifyEvent event = new CounterfeiterFalsifyEvent(
                this.getPlayerWW(), target, facadeRole, facadeCamp, facadeAura);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        if (this.falsifiedTarget != null) {
            restoreFacade(this.falsifiedTarget);
        }

        IRole targetRole = target.getRole();
        this.savedDisplayRole = targetRole.getDisplayRole();
        this.savedDisplayCamp = targetRole.getDisplayCamp();

        targetRole.setDisplayRole(facadeRole);
        targetRole.setDisplayCamp(facadeCamp);
        targetRole.addAuraModifier(new AuraModifier(AURA_MODIFIER_NAME, facadeAura, 1, false));

        this.falsifiedTarget = target;

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(target));

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                "werewolf.roles.counterfeiter_werewolf.perform",
                Formatter.player(target.getName()),
                Formatter.role(game.translate(facadeRole)));
    }

    private void restoreFacade(IPlayerWW target) {
        if (target == null) return;
        if (!target.isState(StatePlayer.ALIVE)) return;
        IRole targetRole = target.getRole();
        targetRole.setDisplayRole(this.savedDisplayRole);
        targetRole.setDisplayCamp(this.savedDisplayCamp);
        targetRole.removeAuraModifier(AURA_MODIFIER_NAME);
        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(target));
    }

    @EventHandler
    @RequireAlive
    public void onDay(DayEvent event) {
        if (this.falsifiedTarget != null) {
            restoreFacade(this.falsifiedTarget);
            this.falsifiedTarget = null;
        }
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            if (this.falsifiedTarget != null) {
                restoreFacade(this.falsifiedTarget);
                this.falsifiedTarget = null;
            }
            return;
        }

        if (this.falsifiedTarget == null) return;
        if (!event.getPlayerWW().equals(this.falsifiedTarget)) return;

        if (this.getPlayerWW().isState(StatePlayer.ALIVE) && isAbilityEnabled()) {
            this.getPlayerWW().addPlayerMaxHealth(-2.0 * game.getConfig().getValue(IntValueBase.COUNTERFEITER_HEART_LOSS));
            this.getPlayerWW().sendMessageWithKey(Prefix.RED,
                    "werewolf.roles.counterfeiter_werewolf.lose_health",
                    Formatter.number(game.getConfig().getValue(IntValueBase.COUNTERFEITER_HEART_LOSS)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnnouncementDeath(AnnouncementDeathEvent event) {
        if (this.falsifiedTarget == null) return;
        if (!event.getPlayerWW().equals(this.falsifiedTarget)) return;
        event.setRole(this.falsifiedTarget.getRole().getDisplayRole());
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.falsifiedTarget = iPlayerWW;
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        if (iPlayerWW != null && iPlayerWW.equals(this.falsifiedTarget)) {
            this.falsifiedTarget = null;
        }
    }

    @Override
    public void clearAffectedPlayer() {
        this.falsifiedTarget = null;
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        if (this.falsifiedTarget == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.falsifiedTarget);
    }
}
