package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.buildings.SanctuaryVisitEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.honor.HonorChangeEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.vote.AbsentionistListVoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Role(key = RoleBase.RONIN,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        defaultAura = Aura.DARK,
        configurations = {@Configuration(config = @ConfigurationBasic(key = "werewolf.roles.ronin.configurations.ronin_every_other_day", defaultValue = true))},
        configValues = {@IntValue(key = IntValueBase.RONIN_HEMORRHAGE_DAMAGE,
                defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.RED_DYE),
                @IntValue(key = IntValueBase.RONIN_GROUP_DISTANCE,
                        defaultValue = 30, meetUpValue = 30, step = 5, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.RONIN_HONOR_MAX,
                        defaultValue = 4, meetUpValue = 4, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
                @IntValue(key = IntValueBase.RONIN_HONOR_MIN,
                        defaultValue = -4, meetUpValue = -4, step = 1, item = UniversalMaterial.SKELETON_SKULL)},
        timers = @Timer(key = TimerBase.RONIN_HEMORRHAGE_DELAY,
                defaultValue = 5, meetUpValue = 5, step = 1))
public class Ronin extends RoleNeutral implements IPower {

    private enum HonorSource {
        KILL_WEREWOLF, KILL_NEUTRAL, KILL_VILLAGER,
        VOTED, VOTING, ABSTENTION,
        GROUP_NON_COMPLIANCE, SANCTUARIES
    }

    private static final float DEFAULT_WALK_SPEED = 0.2f;

    private int roninHonor = -4;
    private int highestHonor = -4;
    private boolean initialized = false;
    private boolean power = false;
    private boolean syncing = false;

    private int currentHealthBonus = 0;
    private float currentResistanceLevel = 0f;
    private boolean hasSpeedI = false;

    private final Set<org.bukkit.DyeColor> visitedSanctuaries = new HashSet<>();

    @Nullable
    private IPlayerWW hemorrhageTarget;
    private int hemorrhageEndTimer = 0;
    private float hemorrhageResistanceDebuff = 0f;
    @Nullable
    private Float originalWalkSpeed;

    @Nullable
    private IPlayerWW silenceTarget;
    private int silenceEndTimer = 0;

    private int particleTick = 0;
    private boolean honorZeroToggle = false;
    private int dayNumber = -8;

    public Ronin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @Override
    public void recoverPower() {
        if (!this.initialized) {
            this.initialized = true;
            this.roninHonor = game.getConfig().getValue(IntValueBase.RONIN_HONOR_MIN);
            this.highestHonor = game.getConfig().getValue(IntValueBase.RONIN_HONOR_MIN);
            setPower(false);
            applyHonorEffects();
        }
    }

    @Override
    public void recoverPotionEffect() {
        if (this.currentResistanceLevel == 1.0f) {
            this.getPlayerWW().addPotionModifier(
                    PotionModifier.add(UniversalPotionEffectType.RESISTANCE, getKey()));
        }
        if (this.hasSpeedI) {
            this.getPlayerWW().addPotionModifier(
                    PotionModifier.add(UniversalPotionEffectType.SPEED, getKey()));
        }
    }

    @Override
    public void disableAbilitiesRole() {
        this.getPlayerWW().addPotionModifier(
                PotionModifier.remove(UniversalPotionEffectType.RESISTANCE, getKey(), 0));
        this.getPlayerWW().addPotionModifier(
                PotionModifier.remove(UniversalPotionEffectType.SPEED, getKey(), 0));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHonorChange(HonorChangeEvent event) {
        if (event.getPlayerWW().equals(this.getPlayerWW()) && !syncing) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSwap(SwapEvent event) {
        if (!event.getPlayerWW1().equals(this.getPlayerWW()) && !event.getPlayerWW2().equals(this.getPlayerWW())) {
            return;
        }
        syncing = true;
        int clamped = Math.max(-3, Math.min(3, this.roninHonor));
        int delta = clamped - this.getPlayerWW().getHonor();
        if (delta != 0) {
            this.getPlayerWW().modifyHonor(delta);
        }
        syncing = false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFinalDeath(FinalDeathEvent event) {
        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            endHemorrhage();
            endSilence(true);
            return;
        }
        if (event.getPlayerWW().equals(this.silenceTarget)) {
            this.silenceTarget = null;
        }
        if (event.getPlayerWW().equals(this.hemorrhageTarget)) {
            endHemorrhage();
        }
        Optional<IPlayerWW> killer = event.getPlayerWW().getLastKiller();
        if (killer.isPresent() && killer.get().equals(this.getPlayerWW()) && isAbilityEnabled()) {
            Camp deadCamp = event.getPlayerWW().getRole().getCamp();
            switch (deadCamp) {
                case WEREWOLF:
                    applyHonorChange(HonorSource.KILL_WEREWOLF);
                    break;
                case NEUTRAL:
                    applyHonorChange(HonorSource.KILL_NEUTRAL);
                    break;
                case VILLAGER:
                    applyHonorChange(HonorSource.KILL_VILLAGER);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVoteResult(VoteResultEvent event) {
        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            applyHonorChange(HonorSource.VOTED);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVote(VoteEvent event) {
        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            applyHonorChange(HonorSource.VOTING);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAbstention(AbsentionistListVoteEvent event) {
        if (event.getAbstentionistList().contains(this.getPlayerWW())) {
            applyHonorChange(HonorSource.ABSTENTION);
        }
    }

    @EventHandler
    public void onSanctuaryVisit(SanctuaryVisitEvent event) {
        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;
        this.visitedSanctuaries.add(event.getColor());
        int total = 4;
        try {
            total = Math.max(1, game.getConfig().getValue(IntValueBase.BUILDINGS_SANCTUARY_COUNT));
        } catch (Exception ignored) {
        }
        if (this.visitedSanctuaries.size() >= total) {
            applyHonorChange(HonorSource.SANCTUARIES);
            this.visitedSanctuaries.clear();
        }
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onDay(DayEvent event) {
        if (game.getConfig().isConfigActive("werewolf.roles.ronin.configurations.ronin_every_other_day")
                && event.getNumber() == dayNumber + 1) {
            return;
        }
        dayNumber = event.getNumber();
        setPower(true);
        if (isInOversizedGroup()) {
            applyHonorChange(HonorSource.GROUP_NON_COMPLIANCE);
        }
    }

    @Override
    public void second() {
        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (this.hemorrhageTarget != null) {
            if (!this.hemorrhageTarget.isState(StatePlayer.ALIVE)
                    || game.getTimer() >= this.hemorrhageEndTimer) {
                endHemorrhage();
            }
        }
        if (this.silenceTarget != null) {
            if (!this.silenceTarget.isState(StatePlayer.ALIVE)
                    || game.getTimer() >= this.silenceEndTimer) {
                endSilence(false);
            }
        }

        this.particleTick++;
        if (this.particleTick >= 2) {
            this.particleTick = 0;
            sendHonorParticles();
        }
    }

    private void sendHonorParticles() {
        Player roninPlayer = Bukkit.getPlayer(this.getPlayerWW().getUUID());
        if (roninPlayer == null) return;
        Location roninLoc = roninPlayer.getLocation();

        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.equals(this.getPlayerWW())) continue;
            Location loc = p.getLocation();
            if (loc.getWorld() != roninLoc.getWorld()) continue;
            if (roninLoc.distance(loc) > 60) continue;

            Player targetPlayer = Bukkit.getPlayer(p.getUUID());
            if (targetPlayer == null) continue;

            int honor = p.getHonor();
            Color color;
            if (honor > 0) {
                color = Color.WHITE;
            } else if (honor < 0) {
                color = Color.RED;
            } else {
                this.honorZeroToggle = !this.honorZeroToggle;
                color = this.honorZeroToggle ? Color.WHITE : Color.RED;
            }
            VersionUtils.getVersionUtils().spawnColoredParticle(
                    roninPlayer, targetPlayer.getLocation().clone().add(0, 1, 0), color);
        }
    }

    private void applyHonorChange(HonorSource source) {
        int actual;
        switch (source) {
            case KILL_NEUTRAL:
            case SANCTUARIES:
                actual = 2;
                break;
            case GROUP_NON_COMPLIANCE:
                actual = -1;
                break;
            case KILL_WEREWOLF:
            case VOTING:
                actual = 2;
                break;
            case KILL_VILLAGER:
                actual = -1;
                break;
            case VOTED:
                actual = -2;
                break;
            case ABSTENTION:
                actual = -1;
                break;
            default:
                actual = 0;
        }
        int newHonor = Math.max(game.getConfig().getValue(IntValueBase.RONIN_HONOR_MIN), Math.min(game.getConfig().getValue(IntValueBase.RONIN_HONOR_MAX), this.roninHonor + actual));
        if (newHonor == this.roninHonor) return;
        this.roninHonor = newHonor;
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.ronin.honor_change",
                Formatter.number(this.roninHonor));
        if (this.roninHonor > this.highestHonor) {
            this.highestHonor = this.roninHonor;
            applyHonorEffects();
        }
    }

    private void applyHonorEffects() {
        int h = this.highestHonor;

        int newBonus = getMaxHealthBonus(h);
        if (this.currentHealthBonus > 0) {
            this.getPlayerWW().removePlayerMaxHealth(this.currentHealthBonus);
        } else if (this.currentHealthBonus < 0) {
            this.getPlayerWW().addPlayerMaxHealth(-this.currentHealthBonus);
        }
        if (newBonus > 0) {
            this.getPlayerWW().addPlayerMaxHealth(newBonus);
        } else if (newBonus < 0) {
            this.getPlayerWW().removePlayerMaxHealth(-newBonus);
        }
        this.currentHealthBonus = newBonus;

        this.removeAuraModifier(getKey());
        this.addAuraModifier(new AuraModifier(getKey(), getAuraForHonor(h), 1, false));

        this.getPlayerWW().addPotionModifier(
                PotionModifier.remove(UniversalPotionEffectType.RESISTANCE, getKey(), 0));
        this.getPlayerWW().addPotionModifier(
                PotionModifier.remove(UniversalPotionEffectType.SPEED, getKey(), 0));
        this.hasSpeedI = false;

        this.currentResistanceLevel = getResistanceLevel(h);
        if (this.currentResistanceLevel == 1.0f) {
            this.getPlayerWW().addPotionModifier(
                    PotionModifier.add(UniversalPotionEffectType.RESISTANCE, getKey()));
        }
        if (h == 4) {
            this.hasSpeedI = true;
            this.getPlayerWW().addPotionModifier(
                    PotionModifier.add(UniversalPotionEffectType.SPEED, getKey()));
        }
    }

    public void startDuel(IPlayerWW target) {
        setPower(false);
        int targetHonor = target.getHonor();
        boolean isHemorrhage;
        if (targetHonor > 0) {
            isHemorrhage = true;
        } else if (targetHonor < 0) {
            isHemorrhage = false;
        } else {
            isHemorrhage = game.getRandom().nextBoolean();
        }
        if (isHemorrhage) {
            startHemorrhage(target);
        } else {
            startSilence(target);
        }
    }

    private void startHemorrhage(IPlayerWW target) {
        endHemorrhage();
        this.hemorrhageTarget = target;
        this.hemorrhageEndTimer = game.getTimer() + getHemorrhageDuration(this.highestHonor);
        this.hemorrhageResistanceDebuff = getHemorrhageResistanceDebuff(this.highestHonor);

        float speedDebuff = getHemorrhageSpeedDebuff(this.highestHonor);
        if (speedDebuff != 0) {
            Player tp = Bukkit.getPlayer(target.getUUID());
            if (tp != null) {
                this.originalWalkSpeed = tp.getWalkSpeed();
                tp.setWalkSpeed(DEFAULT_WALK_SPEED * (1 + speedDebuff));
            }
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.ronin.hemorrhage_start",
                Formatter.player(target.getName()));
        target.sendMessageWithKey(Prefix.RED, "werewolf.roles.ronin.hemorrhage_target",
                Formatter.player(this.getPlayerWW().getName()));
    }

    private void startSilence(IPlayerWW target) {
        endSilence(true);
        this.silenceTarget = target;
        this.silenceEndTimer = game.getTimer() + getSilenceDuration(this.highestHonor);
        target.getRole().disableAbilities();
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.ronin.silence_start",
                Formatter.player(target.getName()));
        target.sendMessageWithKey(Prefix.RED, "werewolf.roles.ronin.silence_target",
                Formatter.player(this.getPlayerWW().getName()));
    }

    private void endHemorrhage() {
        if (this.hemorrhageTarget == null) return;
        if (this.originalWalkSpeed != null) {
            Player tp = Bukkit.getPlayer(this.hemorrhageTarget.getUUID());
            if (tp != null) tp.setWalkSpeed(this.originalWalkSpeed);
            this.originalWalkSpeed = null;
        }
        this.hemorrhageTarget = null;
        this.hemorrhageResistanceDebuff = 0f;
    }

    private void endSilence(boolean roninDied) {
        if (this.silenceTarget == null) return;
        IPlayerWW target = this.silenceTarget;
        this.silenceTarget = null;
        if (!roninDied && target.isState(StatePlayer.ALIVE)) {
            target.getRole().enableAbilities();
            target.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.ronin.silence_end");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player entityPlayer = (Player) event.getEntity();
        UUID entityUUID = entityPlayer.getUniqueId();

        if (entityUUID.equals(this.getPlayerWW().getUUID()) && this.isAbilityEnabled()) {
            if (this.currentResistanceLevel == 0.5f) {
                event.setDamage(event.getDamage() * (1 - 0.5 * 0.20));
            } else if (this.currentResistanceLevel == -0.5f) {
                event.setDamage(event.getDamage() * (1 + 0.5 * 0.20));
            }
        }

        if (this.hemorrhageTarget != null && entityUUID.equals(this.hemorrhageTarget.getUUID())) {
            if (this.hemorrhageResistanceDebuff != 0) {
                event.setDamage(event.getDamage() * (1 - this.hemorrhageResistanceDebuff * 0.20));
            }
            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if (damager.getUniqueId().equals(this.getPlayerWW().getUUID())
                        && this.isAbilityEnabled()) {
                    final IPlayerWW target = this.hemorrhageTarget;
                    BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                        Player p = Bukkit.getPlayer(target.getUUID());
                        if (p != null && target.isState(StatePlayer.ALIVE)) {
                            p.damage(game.getConfig().getValue(IntValueBase.RONIN_HEMORRHAGE_DAMAGE));
                        }
                    }, game.getConfig().getTimerValue(TimerBase.RONIN_HEMORRHAGE_DELAY) * 20L);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @RequireAlive
    public void onActionBar(ActionBarEvent event) {
        if (!this.getPlayerWW().getUUID().equals(event.getPlayerUUID())) return;
        event.getActionBar().insert(0, game.translate("werewolf.roles.ronin.actionbar",
                Formatter.number(this.roninHonor),
                Formatter.format("&number1&", this.highestHonor)) + " ");
    }

    private boolean isInOversizedGroup() {
        Location loc = this.getPlayerWW().getLocation();
        int nearby = 0;
        for (IPlayerWW p : game.getAlivePlayersWW()) {
            if (p.equals(this.getPlayerWW())) continue;
            Location l = p.getLocation();
            if (l.getWorld() != loc.getWorld()) continue;
            if (loc.distance(l) < game.getConfig().getValue(IntValueBase.RONIN_GROUP_DISTANCE)) nearby++;
        }
        return nearby >= game.getGroup();
    }

    private static int getMaxHealthBonus(int h) {
        switch (h) {
            case 4:
                return 6;
            case 3:
                return 4;
            case 2:
                return 2;
            case 1:
            case 0:
                return 0;
            case -1:
            case -2:
                return -2;
            case -3:
            case -4:
                return -4;
            default:
                return 0;
        }
    }

    private static Aura getAuraForHonor(int h) {
        if (h >= 2) return Aura.LIGHT;
        if (h >= -1) return Aura.NEUTRAL;
        return Aura.DARK;
    }

    private static float getResistanceLevel(int h) {
        if (h >= 3) return 1.0f;
        if (h >= 1) return 0.5f;
        if (h <= -4) return -0.5f;
        return 0f;
    }

    private static int getHemorrhageDuration(int h) {
        switch (h) {
            case 4:
                return 120;
            case 3:
                return 105;
            case 2:
                return 90;
            case 1:
                return 75;
            case 0:
                return 60;
            case -1:
            case -2:
                return 45;
            default:
                return 30;
        }
    }

    private static float getHemorrhageResistanceDebuff(int h) {
        if (h >= 3) return -0.75f;
        if (h >= 0) return -0.5f;
        if (h >= -3) return -0.25f;
        return 0f;
    }

    private static float getHemorrhageSpeedDebuff(int h) {
        if (h >= 3) return -0.75f;
        if (h >= 0) return -0.5f;
        if (h >= -1) return -0.25f;
        return 0f;
    }

    private static int getSilenceDuration(int h) {
        switch (h) {
            case 4:
                return 20 * 60;
            case 3:
                return 17 * 60;
            case 2:
                return 14 * 60;
            case 1:
                return 12 * 60;
            case 0:
                return 10 * 60;
            case -1:
                return 8 * 60;
            case -2:
                return 6 * 60;
            default:
                return 4 * 60;
        }
    }

    public int getRoninHonor() {
        return this.roninHonor;
    }

    public int getHighestHonor() {
        return this.highestHonor;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.ronin.description",
                        Formatter.number(this.roninHonor),
                        Formatter.format("&number1&", this.highestHonor)))
                .setCommand(game.translate("werewolf.roles.ronin.command"))
                .build();
    }
}
