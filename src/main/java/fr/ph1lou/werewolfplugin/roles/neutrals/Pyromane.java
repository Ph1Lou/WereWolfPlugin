package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.buildings.IBuilding;
import fr.ph1lou.werewolfapi.buildings.ICouncilChamber;
import fr.ph1lou.werewolfapi.buildings.ISanctuary;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
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
        sharpnessIronModifier = 1)
public class Pyromane extends RoleNeutral implements IPower {

    private static final int COVER_COOLDOWN = 10 * 60;
    private static final int HEART_LOSS_DURATION = 10 * 60;
    private static final int HEART_LOSS_AMOUNT = 4;
    private static final int FIRE_DURATION = 20 * 60;
    private static final int FIRE_PROXIMITY = 15;
    private static final int BUILDING_PROXIMITY = 15;
    private static final float MIN_BURN_RESISTANCE = -1.5f;

    private final Set<UUID> coveredPlayers = new HashSet<>();
    private final Map<UUID, Float> burnResistance = new HashMap<>();

    private int lastCoverTimer = -COVER_COOLDOWN;
    private boolean ignitePower = false;
    private boolean burnPower = false;

    @Nullable private IBuilding burnedBuilding;
    @Nullable private Location fireLocation;
    private int fireEndTimer = 0;
    private boolean burnPending = false;
    private boolean burnActive = false;

    public Pyromane(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    public boolean canCover() {
        return game.getTimer() - this.lastCoverTimer >= COVER_COOLDOWN;
    }

    public int getCoverRemaining() {
        return COVER_COOLDOWN - (game.getTimer() - this.lastCoverTimer);
    }

    public boolean hasMet(IPlayerWW target) {
        return this.getPlayerWW().getPlayersMet().stream()
                .anyMatch(p -> p.getUUID().equals(target.getUUID()));
    }

    public void cover(IPlayerWW target) {
        this.coveredPlayers.add(target.getUUID());
        target.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.covered_notify",
                Formatter.player(this.getPlayerWW().getName()));
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

            target.removePlayerMaxHealth(HEART_LOSS_AMOUNT);
            final UUID targetUUID = target.getUUID();
            BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                IPlayerWW p = game.getPlayerWW(targetUUID).orElse(null);
                if (p != null) {
                    p.addPlayerMaxHealth(HEART_LOSS_AMOUNT);
                }
            }, HEART_LOSS_DURATION * 20L);

            float current = this.burnResistance.getOrDefault(targetUUID, 0f);
            if (current == 0f) {
                current = -0.5f;
            } else {
                current = Math.max(MIN_BURN_RESISTANCE, current - 0.25f);
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
        double nearestDist = (double) BUILDING_PROXIMITY;
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

        int delay = 3 * 60 + game.getRandom().nextInt(3 * 60);
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
        this.fireEndTimer = game.getTimer() + FIRE_DURATION;

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.burn_start");
    }

    private void endFire() {
        this.burnActive = false;
        if (this.burnedBuilding instanceof IVoteOffice) {
            IVoteOffice office = (IVoteOffice) this.burnedBuilding;
            int newCap = office.getCapacity() / 2;
            office.reduceCapacity(office.getCapacity() - newCap);
        }
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.pyromane.burn_end");
        this.burnedBuilding = null;
        this.fireLocation = null;
    }

    @Override
    public void second() {
        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (this.burnActive && this.fireLocation != null) {
            if (game.getTimer() >= this.fireEndTimer) {
                endFire();
            } else {
                for (IPlayerWW p : game.getAlivePlayersWW()) {
                    if (p.equals(this.getPlayerWW())) continue;
                    if (this.coveredPlayers.contains(p.getUUID())) continue;
                    Location pl = p.getLocation();
                    if (pl.getWorld() != this.fireLocation.getWorld()) continue;
                    if (pl.distance(this.fireLocation) <= FIRE_PROXIMITY) {
                        this.coveredPlayers.add(p.getUUID());
                        p.sendMessageWithKey(Prefix.RED, "werewolf.roles.pyromane.covered_by_fire");
                        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN,
                                "werewolf.roles.pyromane.fire_covered",
                                Formatter.player(p.getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
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
            event.setDamage(event.getDamage() * (1 - res * 0.20));
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
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.pyromane.description"))
                .setCommand(game.translate("werewolf.roles.pyromane.cover_command"))
                .build();
    }
}
