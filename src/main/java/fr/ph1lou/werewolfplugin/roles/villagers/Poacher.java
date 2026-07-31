package fr.ph1lou.werewolfplugin.roles.villagers;


import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.poacher.PoacherRecoverFurEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.registers.IRegisterManager;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Role(key = RoleBase.POACHER, category = Category.VILLAGER,
        defaultAura = Aura.NEUTRAL,
        attribute = RoleAttribute.VILLAGER,
        timers = { @Timer(key = TimerBase.POACHER_PROGRESS, defaultValue = 10, meetUpValue = 10) },
        configValues = {
                @IntValue(key = IntValueBase.POACHER_DISTANCE, defaultValue = 10, meetUpValue = 10, step = 1, item = UniversalMaterial.ARROW)
        })
public class Poacher extends RoleImpl {

    private static final int DETECTION_DISTANCE = 50;
    private static final int CARCASS_DURATION = 15 * 60;
    private static final String AURA_MODIFIER_NAME = "poacher_fur";

    private final Map<UUID, Carcass> carcasses = new LinkedHashMap<>();
    private int furNumbers = 0;
    private int progress = 0;
    @Nullable
    private IPlayerWW trackedCarcass;
    private int switchColor = 0;
    @Nullable
    private IRegisterManager registerManager;

    public Poacher(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnnouncementDeath(AnnouncementDeathEvent event) {

        if (!event.getTargetPlayer().equals(this.getPlayerWW())) {
            return;
        }

        IPlayerWW deadPlayer = event.getPlayerWW();
        UUID deadUUID = deadPlayer.getUUID();

        if (this.carcasses.containsKey(deadUUID)) {
            return;
        }

        String deathRole = event.getRole();

        this.resolveCategory(deathRole).ifPresent(category -> {
            if (category == Category.WEREWOLF
                    || RoleBase.WOLF_DOG.equals(deathRole)
                    || RoleBase.WHITE_WEREWOLF.equals(deathRole)) {
                this.carcasses.put(deadUUID, new Carcass(deadPlayer, CARCASS_DURATION));
            }
        });
    }

    private Optional<Category> resolveCategory(String roleKey) {
        if (this.registerManager == null) {
            GetWereWolfAPI api = Bukkit.getServer().getServicesManager().load(GetWereWolfAPI.class);
            if (api == null) {
                return Optional.empty();
            }
            this.registerManager = api.getRegisterManager();
        }
        return this.registerManager.getCategory(roleKey);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.poacher.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.POACHER_DISTANCE)),
                        Formatter.timer(game, TimerBase.POACHER_PROGRESS)))
                .setEffects(game.translate("werewolf.roles.poacher.effects"))
                .setPower(game.translate("werewolf.roles.poacher.fur_numbers",
                        Formatter.number(this.furNumbers)))
                .build();
    }

    @Override
    public void recoverPower() {
    }

    @Override
    public void recoverPotionEffect() {
        if (this.furNumbers > 0) {
            this.addAuraModifier(new AuraModifier(AURA_MODIFIER_NAME, Aura.DARK, 1, false));
        }
    }

    @Override
    public String getDisplayCamp() {
        if (this.furNumbers > 0 || this.isWereWolf()) {
            return Category.WEREWOLF.getKey();
        }
        return Category.VILLAGER.getKey();
    }

    @Override
    public String getDisplayRole() {
        if (this.furNumbers > 0 || this.isWereWolf()) {
            return RoleBase.WEREWOLF;
        }
        return super.getDisplayRole();
    }

    @Override
    public void second() {

        if (!isAbilityEnabled()) return;
        if (!this.carcasses.isEmpty()) {
            List<UUID> expired = new ArrayList<>();
            for (Map.Entry<UUID, Carcass> entry : this.carcasses.entrySet()) {
                entry.getValue().secondsRemaining--;
                if (entry.getValue().secondsRemaining <= 0) {
                    expired.add(entry.getKey());
                }
            }
            expired.forEach(this.carcasses::remove);
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (this.carcasses.isEmpty()) {
            this.trackedCarcass = null;
            this.progress = 0;
            return;
        }

        Location poacherLoc = this.getPlayerWW().getLocation();
        Player poacher = Bukkit.getPlayer(this.getPlayerWW().getUUID());

        IPlayerWW nearestForRecovery = null;

        for (Carcass carcass : this.carcasses.values()) {
            Location deathLoc = carcass.playerWW.getDeathLocation();
            if (deathLoc == null || deathLoc.getWorld() != poacherLoc.getWorld()) {
                continue;
            }

            double dist = deathLoc.distance(poacherLoc);

            if (poacher != null && dist <= DETECTION_DISTANCE) {
                poacher.playEffect(deathLoc, Effect.MOBSPAWNER_FLAMES, null);
            }

            if (nearestForRecovery == null
                    && dist < game.getConfig().getValue(IntValueBase.POACHER_DISTANCE)) {
                nearestForRecovery = carcass.playerWW;
            }
        }

        if (nearestForRecovery != null) {
            this.trackedCarcass = nearestForRecovery;
            this.progress++;
        } else {
            this.trackedCarcass = null;
            this.progress = 0;
        }

        if (this.progress >= game.getConfig().getTimerValue(TimerBase.POACHER_PROGRESS)) {
            this.progress = 0;
            if (this.trackedCarcass != null) {
                IPlayerWW recovered = this.trackedCarcass;
                this.carcasses.remove(recovered.getUUID());

                PoacherRecoverFurEvent event =
                        new PoacherRecoverFurEvent(this.getPlayerWW(), this.furNumbers + 1);

                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    this.trackedCarcass = null;
                    this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                    return;
                }

                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.poacher.recover",
                        Formatter.player(recovered.getName()));

                this.furNumbers++;
                this.recoverPotionEffect();
                this.trackedCarcass = null;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onActionBarEvent(ActionBarEvent event) {

        if (!this.getPlayerWW().getUUID().equals(event.getPlayerUUID())) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (this.carcasses.isEmpty()) {
            return;
        }

        Player player = Bukkit.getPlayer(this.getPlayerWW().getUUID());
        if (player == null) {
            return;
        }

        Location poacherLoc = player.getLocation();

        IPlayerWW nearest = null;
        double nearestDist = Double.MAX_VALUE;
        boolean withinRecovery = false;

        for (Carcass carcass : this.carcasses.values()) {
            Location deathLoc = carcass.playerWW.getDeathLocation();
            if (deathLoc == null || deathLoc.getWorld() != poacherLoc.getWorld()) {
                continue;
            }
            double dist = deathLoc.distance(poacherLoc);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = carcass.playerWW;
            }
            if (dist < game.getConfig().getValue(IntValueBase.POACHER_DISTANCE)) {
                withinRecovery = true;
            }
        }

        StringBuilder sb = event.getActionBar();

        if (withinRecovery) {
            String actionBar = this.switchColor++ < 20 ?
                    game.translate("werewolf.roles.poacher.actionbar_1") :
                    game.translate("werewolf.roles.poacher.actionbar_2");
            this.switchColor %= 40;

            sb.insert(0, actionBar + " ");
            sb.append(" ").append(actionBar);
        } else if (nearest != null && nearestDist <= DETECTION_DISTANCE) {
            int palier = (int) Math.round(nearestDist / 10.0) * 10;
            String arrow = Utils.getArrow(player, nearest.getDeathLocation());
            sb.insert(0, game.translate("werewolf.roles.poacher.actionbar_distance",
                    Formatter.format("&text&", palier + " " + arrow)) + " ");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPatchPotion(EntityDamageByEntityEvent event) {

        if (this.furNumbers <= 0) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!player.getUniqueId().equals(this.getPlayerWW().getUUID())) {
            return;
        }

        double resistanceLevels = 0.5 + 0.1 * (this.furNumbers - 1);
        double reduction = resistanceLevels * 0.20;

        event.setDamage(event.getDamage() * (1 - reduction));
    }

    private static class Carcass {
        final IPlayerWW playerWW;
        int secondsRemaining;

        Carcass(IPlayerWW playerWW, int secondsRemaining) {
            this.playerWW = playerWW;
            this.secondsRemaining = secondsRemaining;
        }
    }
}
