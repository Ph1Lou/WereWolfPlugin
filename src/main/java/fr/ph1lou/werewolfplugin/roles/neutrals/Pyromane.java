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
import fr.ph1lou.werewolfapi.buildings.IBuilding;
import fr.ph1lou.werewolfapi.buildings.ICouncilChamber;
import fr.ph1lou.werewolfapi.buildings.ISanctuary;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.pyromane.PyromaneCoverEvent;
import fr.ph1lou.werewolfapi.events.roles.pyromane.PyromaneIgniteEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Role(key = RoleBase.PYROMANE,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        defaultAura = Aura.LIGHT,
        sharpnessDiamondModifier = 1,
        sharpnessIronModifier = 1,
        configurations = {@Configuration(config = @ConfigurationBasic(key = "werewolf.roles.pyromane.configurations.pyromane_every_other_day", defaultValue = true))},
        configValues = {@IntValue(key = IntValueBase.PYROMANE_HEART_LOSS_AMOUNT,
                defaultValue = 4, meetUpValue = 4, step = 1, item = UniversalMaterial.RED_DYE),
                @IntValue(key = IntValueBase.PYROMANE_FIRE_PROXIMITY,
                        defaultValue = 15, meetUpValue = 15, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.PYROMANE_BUILDING_PROXIMITY,
                        defaultValue = 15, meetUpValue = 15, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.PYROMANE_MIN_BURN_RESISTANCE,
                        defaultValue = 150, meetUpValue = 150, step = 10, item = UniversalMaterial.SHIELD),
                @IntValue(key = IntValueBase.PYROMANE_BURN_INITIAL,
                        defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.SHIELD),
                @IntValue(key = IntValueBase.PYROMANE_BURN_INCREMENT,
                        defaultValue = 25, meetUpValue = 25, step = 5, item = UniversalMaterial.SHIELD),
                @IntValue(key = IntValueBase.PYROMANE_BURN_DAMAGE_FACTOR,
                        defaultValue = 20, meetUpValue = 20, step = 5, item = UniversalMaterial.SHIELD)},
        timers = {@Timer(key = TimerBase.PYROMANE_COVER_COOLDOWN,
                defaultValue = 10 * 60, meetUpValue = 10 * 60, step = 30),
                @Timer(key = TimerBase.PYROMANE_HEART_LOSS_DURATION,
                        defaultValue = 10 * 60, meetUpValue = 10 * 60, step = 30),
                @Timer(key = TimerBase.PYROMANE_FIRE_DURATION,
                        defaultValue = 20 * 60, meetUpValue = 20 * 60, step = 60),
                @Timer(key = TimerBase.PYROMANE_IGNITION_DELAY_MIN,
                        defaultValue = 3 * 60, meetUpValue = 3 * 60, step = 15),
                @Timer(key = TimerBase.PYROMANE_IGNITION_DELAY_MAX,
                        defaultValue = 5 * 60, meetUpValue = 5 * 60, step = 15)})
public class Pyromane extends RoleNeutral implements IPower {

    private final Set<UUID> coveredPlayers = new HashSet<>();
    private final Map<UUID, Float> burnResistance = new HashMap<>();

    private int lastCoverTimer = 0;
    private boolean ignitePower = false;
    private boolean burnPower = false;

    @Nullable private IBuilding burnedBuilding;
    @Nullable private Location fireLocation;
    private int fireEndTimer = 0;
    private boolean burnPending = false;
    private boolean burnActive = false;
    private int dayNumber = -8;

    public Pyromane(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    public boolean canCover() {
        return game.getTimer() - this.lastCoverTimer >= game.getConfig().getTimerValue(TimerBase.PYROMANE_COVER_COOLDOWN);
    }

    public int getCoverRemaining() {
        return game.getConfig().getTimerValue(TimerBase.PYROMANE_COVER_COOLDOWN) - (game.getTimer() - this.lastCoverTimer);
    }

    public boolean hasMet(IPlayerWW target) {
        return this.getPlayerWW().getPlayersMet().stream()
                .anyMatch(p -> p.getUUID().equals(target.getUUID()));
    }

    public void cover(IPlayerWW target) {
        this.coveredPlayers.add(target.getUUID());
        target.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.covered_notify");
    }

    public void onCovered() {
        this.lastCoverTimer = game.getTimer();
    }

    public void ignite() {
        setPower(false);
        Set<IPlayerWW> targets = new HashSet<>();
        for (UUID uuid : new HashSet<>(this.coveredPlayers)) {
            IPlayerWW p = game.getPlayerWW(uuid).orElse(null);
            if (p != null && p.isState(StatePlayer.ALIVE)) {
                targets.add(p);
            }
        }

        PyromaneIgniteEvent event = new PyromaneIgniteEvent(this.getPlayerWW(), targets);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            setPower(true);
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        for (IPlayerWW target : targets) {
            this.coveredPlayers.remove(target.getUUID());

            target.removePlayerMaxHealth(game.getConfig().getValue(IntValueBase.PYROMANE_HEART_LOSS_AMOUNT));
            final UUID targetUUID = target.getUUID();
            BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                IPlayerWW p = game.getPlayerWW(targetUUID).orElse(null);
                if (p != null) {
                    p.addPlayerMaxHealth(game.getConfig().getValue(IntValueBase.PYROMANE_HEART_LOSS_AMOUNT));
                }
            }, game.getConfig().getTimerValue(TimerBase.PYROMANE_HEART_LOSS_DURATION) * 20L);

            float current = this.burnResistance.getOrDefault(targetUUID, 0f);
            if (current == 0f) {
                current = -game.getConfig().getValue(IntValueBase.PYROMANE_BURN_INITIAL) / 100f;
            } else {
                current = Math.max(-game.getConfig().getValue(IntValueBase.PYROMANE_MIN_BURN_RESISTANCE) / 100f, current - game.getConfig().getValue(IntValueBase.PYROMANE_BURN_INCREMENT) / 100f);
            }
            this.burnResistance.put(targetUUID, current);

            target.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.ignited_target",
                    Formatter.player(this.getPlayerWW().getName()));
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.ignited_perform",
                Formatter.number(targets.size()));
    }

    public boolean canBurn() {
        return this.burnPower;
    }

    @Nullable
    public IBuilding findNearestBuilding() {
        IBuilding nearest = null;
        double nearestDist = (double) game.getConfig().getValue(IntValueBase.PYROMANE_BUILDING_PROXIMITY);
        Location loc = this.getPlayerWW().getLocation();

        for (IVoteOffice office : game.getBuildingsManager().getVoteOffices()) {
            double d = distToBuilding(loc, office);
            if (d < nearestDist) { nearestDist = d; nearest = office; }
        }
        for (ICouncilChamber chamber : game.getBuildingsManager().getCouncilChambers()) {
            double d = distToBuilding(loc, chamber);
            if (d < nearestDist) { nearestDist = d; nearest = chamber; }
        }
        for (ISanctuary sanctuary : game.getBuildingsManager().getSanctuaries()) {
            double d = distToBuilding(loc, sanctuary);
            if (d < nearestDist) { nearestDist = d; nearest = sanctuary; }
        }
        return nearest;
    }

    private static double distToBuilding(Location loc, IBuilding b) {
        Location bl = new Location(b.getWorld(), b.getBaseX(), b.getBaseY(), b.getBaseZ());
        if (bl.getWorld() != loc.getWorld()) return Double.MAX_VALUE;
        return loc.distance(bl);
    }

    public void burnBuilding(IBuilding building) {
        this.burnPower = false;
        this.burnedBuilding = building;
        this.burnPending = true;

        int delay = game.getConfig().getTimerValue(TimerBase.PYROMANE_IGNITION_DELAY_MIN) + game.getRandom().nextInt(Math.max(1, game.getConfig().getTimerValue(TimerBase.PYROMANE_IGNITION_DELAY_MAX) - game.getConfig().getTimerValue(TimerBase.PYROMANE_IGNITION_DELAY_MIN)));
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.burn_pending",
                Formatter.player(this.getPlayerWW().getName()));

        BukkitUtils.scheduleSyncDelayedTask(game, this::startFire, delay * 20L);
    }

    private void startFire() {
        if (this.burnedBuilding == null) return;
        this.burnPending = false;
        this.burnActive = true;
        this.fireLocation = new Location(this.burnedBuilding.getWorld(),
                this.burnedBuilding.getBaseX(), this.burnedBuilding.getBaseY(), this.burnedBuilding.getBaseZ());
        this.fireEndTimer = game.getTimer() + game.getConfig().getTimerValue(TimerBase.PYROMANE_FIRE_DURATION);

        ((fr.ph1lou.werewolfplugin.buildings.BuildingsManager) game.getBuildingsManager())
                .setDisabled(this.burnedBuilding, true);

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.burn_start");
    }

    private void endFire() {
        this.burnActive = false;
        ((fr.ph1lou.werewolfplugin.buildings.BuildingsManager) game.getBuildingsManager())
                .setDisabled(this.burnedBuilding, false);
        if (this.burnedBuilding instanceof IVoteOffice) {
            IVoteOffice office = (IVoteOffice) this.burnedBuilding;
            int newCap = office.getCapacity() / 2;
            office.reduceCapacity(office.getCapacity() - newCap);
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.burn_end");
        this.burnedBuilding = null;
        this.fireLocation = null;
    }

    private void spawnFireParticles() {
        if (this.fireLocation == null) return;
        org.bukkit.World w = this.fireLocation.getWorld();
        if (w == null) return;
        double cx = this.fireLocation.getX();
        double cy = this.fireLocation.getY();
        double cz = this.fireLocation.getZ();
        Location base = new Location(w, cx, cy, cz);
        double t = game.getTimer();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getLocation().getWorld() != w) continue;
            try {
                if (online.getLocation().distance(this.fireLocation) > 48) continue;
            } catch (Throwable ignored) {
                continue;
            }

            online.spawnParticle(org.bukkit.Particle.FLAME, base.clone().add(0, 1.5, 0), 60, 5.0, 3.0, 5.0, 0.02);
            online.spawnParticle(org.bukkit.Particle.FLAME, base.clone().add(0, 3.0, 0), 40, 3.5, 2.0, 3.5, 0.01);

            double[] radii = {2.0, 4.0, 6.0};
            for (int r = 0; r < radii.length; r++) {
                double radius = radii[r];
                int count = 20;
                for (int i = 0; i < count; i++) {
                    double angle = (Math.PI * 2 * i) / count + t * 0.15 * (r + 1);
                    Location loc = new Location(w, cx + Math.cos(angle) * radius, cy + 0.8 + r * 0.6, cz + Math.sin(angle) * radius);
                    online.spawnParticle(org.bukkit.Particle.FLAME, loc, 2, 0.15, 0.15, 0.15, 0.01);
                }
            }

            online.spawnParticle(org.bukkit.Particle.LAVA, base, 8, 4.0, 0.8, 4.0);
            online.spawnParticle(org.bukkit.Particle.LAVA, base.clone().add(0, 1.5, 0), 5, 2.5, 0.5, 2.5);
        }
    }

    @Override
    public void second() {

        if (this.burnActive && this.fireLocation != null) {
            if (game.getTimer() >= this.fireEndTimer) {
                endFire();
                return;
            }
        }

        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (this.burnActive && this.fireLocation != null) {
            spawnFireParticles();
            for (IPlayerWW p : game.getAlivePlayersWW()) {
                if (p.equals(this.getPlayerWW())) continue;
                if (this.coveredPlayers.contains(p.getUUID())) continue;
                Location pl = p.getLocation();
                if (pl.getWorld() != this.fireLocation.getWorld()) continue;
                if (pl.distance(this.fireLocation) <= game.getConfig().getValue(IntValueBase.PYROMANE_FIRE_PROXIMITY)) {
                    this.coveredPlayers.add(p.getUUID());
                    p.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.covered_by_fire");
                    this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                            "werewolf.roles.pyromane.fire_covered",
                            Formatter.player(p.getName()));
                }
            }
        }
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onDay(DayEvent event) {
        if (game.getConfig().isConfigActive("werewolf.roles.pyromane.configurations.pyromane_every_other_day")
                && event.getNumber() == dayNumber + 1) {
            return;
        }
        dayNumber = event.getNumber();
        setPower(true);
        this.burnPower = true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        if (!damager.getUniqueId().equals(this.getPlayerWW().getUUID())) return;
        if (!isAbilityEnabled()) return;

        Player entity = (Player) event.getEntity();
        float res = this.burnResistance.getOrDefault(entity.getUniqueId(), 0f);
        if (res != 0f) {
            event.setDamage(event.getDamage() * (1 - res * game.getConfig().getValue(IntValueBase.PYROMANE_BURN_DAMAGE_FACTOR) / 100.0));
        }
    }

    public Set<UUID> getCoveredPlayers() {
        return this.coveredPlayers;
    }

    @Override
    public boolean hasPower() { return this.ignitePower; }

    @Override
    public void setPower(boolean power) { this.ignitePower = power; }

    @Override
    public void recoverPower() {
        setPower(false);
        this.burnPower = false;
        this.lastCoverTimer = -game.getConfig().getTimerValue(TimerBase.PYROMANE_COVER_COOLDOWN);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.pyromane.description",
                        Formatter.timer(game, TimerBase.PYROMANE_COVER_COOLDOWN),
                        Formatter.format("&number&", g -> String.valueOf(g.getConfig().getValue(IntValueBase.PYROMANE_HEART_LOSS_AMOUNT) / 2)),
                        Formatter.format("&timer1&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.PYROMANE_HEART_LOSS_DURATION))),
                        Formatter.format("&timer2&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.PYROMANE_IGNITION_DELAY_MIN))),
                        Formatter.format("&timer3&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.PYROMANE_IGNITION_DELAY_MAX))),
                        Formatter.format("&timer4&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.PYROMANE_FIRE_DURATION)))))
                .setCommand(game.translate("werewolf.roles.pyromane.cover_command"))
                .build();
    }
}
