package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.RequireAbility;
import fr.ph1lou.werewolfapi.annotations.RequireAlive;
import fr.ph1lou.werewolfapi.annotations.RequirePower;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.ActionBarEvent;
import fr.ph1lou.werewolfapi.events.game.actionablestory.ActionableStoryEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.AmnesiacLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.DonEvent;
import fr.ph1lou.werewolfapi.events.lovers.LoverDeathEvent;
import fr.ph1lou.werewolfapi.events.lovers.RevealLoversEvent;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.roles.StealEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalLoverDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.rival.RivalLoverEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.lovers.LoverImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Role(key = RoleBase.RIVAL,
        defaultAura = Aura.DARK,
        category = Category.NEUTRAL,
        attribute = RoleAttribute.NEUTRAL,
        sharpnessIronModifier = 1,
        sharpnessDiamondModifier = 1,
        powerModifier = 1,
        requireRoles = { RoleBase.CUPID },
        configValues = {@IntValue(key = IntValueBase.RIVAL_ISOLATE_RANGE,
                defaultValue = 15, meetUpValue = 15, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = IntValueBase.RIVAL_DAMAGE_REDUCTION,
                        defaultValue = 30, meetUpValue = 30, step = 5, item = UniversalMaterial.SHIELD),
                @IntValue(key = IntValueBase.RIVAL_PENALTY_HEALTH,
                        defaultValue = 10, meetUpValue = 10, step = 2, item = UniversalMaterial.RED_DYE),
                @IntValue(key = IntValueBase.RIVAL_ISOLATE_MAX_USE,
                        defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.ARROW),
                @IntValue(key = IntValueBase.RIVAL_REGEN_HEALTH,
                        defaultValue = 2, meetUpValue = 2, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
                @IntValue(key = IntValueBase.RIVAL_REGEN_COUNT,
                        defaultValue = 5, meetUpValue = 5, step = 1, item = UniversalMaterial.EXPERIENCE_BOTTLE),
                @IntValue(key = IntValueBase.RIVAL_KILL_GOLDEN_APPLES,
                        defaultValue = 25, meetUpValue = 25, step = 1, item = UniversalMaterial.GOLDEN_APPLE),
                @IntValue(key = IntValueBase.RIVAL_SPEED_AMPLIFIER,
                        defaultValue = 1, meetUpValue = 1, step = 1, item = UniversalMaterial.SUGAR_CANE_BLOCK)},
        timers = {@Timer(key = TimerBase.RIVAL_ANALYSE_COOLDOWN,
                defaultValue = 15 * 60, meetUpValue = 15 * 60, step = 60),
                @Timer(key = TimerBase.RIVAL_DEVINER_COOLDOWN,
                        defaultValue = 30 * 60, meetUpValue = 30 * 60, step = 60),
                @Timer(key = TimerBase.RIVAL_ISOLATE_DURATION,
                        defaultValue = 3 * 60, meetUpValue = 3 * 60, step = 15),
                @Timer(key = TimerBase.RIVAL_REGEN_INTERVAL,
                        defaultValue = 5 * 60, meetUpValue = 5 * 60, step = 30)})
public class Rival extends RoleNeutral implements IPower, ILimitedUse {

    @Nullable
    private IPlayerWW cupidWW = null;

    @Nullable
    private ILover lover = null;

    private boolean power = true;
    private boolean killed = false;
    private boolean bonusChosen = false;
    private boolean guessed = false;

    private int lastAnalyse;
    private int lastDeviner;

    @Nullable
    private IPlayerWW isolatedLover = null;
    private int isolationEnd = 0;

    private int isolateUse = 0;

    public Rival(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }

    @EventHandler
    public void onSwapEvent(SwapEvent event) {
        if (event.getPlayerWW1().equals(this.cupidWW)) {
            this.cupidWW = event.getPlayerWW2();
        } else if (event.getPlayerWW2().equals(this.cupidWW)) {
            this.cupidWW = event.getPlayerWW1();
        }
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.rival.description",
                        Formatter.format("&number&", g -> String.valueOf(g.getConfig().getValue(IntValueBase.RIVAL_PENALTY_HEALTH) / 2)),
                        Formatter.format("&number1&", g -> String.valueOf(g.getConfig().getValue(IntValueBase.RIVAL_REGEN_HEALTH) / 2)),
                        Formatter.timer(game, TimerBase.RIVAL_REGEN_INTERVAL)))
                .setItems(game.translate("werewolf.roles.rival.item"))
                .setPower(game.translate("werewolf.roles.rival.power"))
                .setCommand(game.translate("werewolf.roles.rival.commands"))
                .build();
    }

    @EventHandler
    @RequireAlive
    public void onLoverReveal(RevealLoversEvent event) {

        if (event.getLovers().isEmpty()) return;

        List<ILover> loverAPIs = event.getLovers().stream()
                .filter(ILover -> !ILover.isKey(LoverBase.CURSED_LOVER))
                .filter(loverAPI1 -> !loverAPI1.getLovers().contains(getPlayerWW()))
                .collect(Collectors.toList());

        if (loverAPIs.isEmpty()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.error");
            return;
        }

        this.lover = loverAPIs.get((int) Math.floor(game.getRandom().nextFloat() * loverAPIs.size()));

        if (lover instanceof LoverImpl) {
            this.cupidWW = game.getPlayersWW()
                    .stream().map(IPlayerWW::getRole)
                    .filter(roles -> roles.isKey(RoleBase.CUPID))
                    .map(roles -> (fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers) roles)
                    .filter(affectedPlayers -> affectedPlayers.getAffectedPlayers().contains(lover.getLovers().get(0)))
                    .map(affectedPlayers -> ((fr.ph1lou.werewolfapi.role.interfaces.IRole) affectedPlayers).getPlayerWW())
                    .findFirst().orElse(null);
        }

        List<IPlayerWW> lovers = new ArrayList<>(lover.getLovers());
        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.rival.lover",
                Formatter.format("&role1&", lovers.isEmpty() ? "" : game.translate(lovers.get(0).getRole().getKey())),
                Formatter.format("&role2&", lovers.size() == 2 ? game.translate(lovers.get(1).getRole().getKey()) : ""));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @RequireAlive
    @RequirePower
    public void onLoverKilled(FinalDeathEvent event) {

        if (lover == null) return;
        if (!lover.getLovers().contains(event.getPlayerWW())) return;

        Optional<IPlayerWW> killerWW = event.getPlayerWW().getLastKiller();
        if (!killerWW.isPresent() || !killerWW.get().equals(getPlayerWW())) return;

        IPlayerWW dead = event.getPlayerWW();
        IPlayerWW survivor = lover.getLovers().stream()
                .filter(p -> !p.equals(dead)).findFirst().orElse(null);

        // Dissout le couple : death=true → le handler HIGH de LoverImpl/AmnesiacLover
        // retourne immédiatement (pas de suicide du survivant), listener désenregistré,
        // membres détachés. Aucune annonce → le nom du Rival n'est pas divulgué.
        lover.dissolve();

        Bukkit.getPluginManager().callEvent(new RivalLoverEvent(this.getPlayerWW(), dead));

        killed = true;
        power = false;
        lover = null;

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.rival.kill_success");
        if (survivor != null) {
            survivor.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.rival.survivor_alone");
        }
    }

    @EventHandler
    public void onLoverDeath(LoverDeathEvent event) {

        String loverKey = event.getLover().getKey();
        if (loverKey.equals(LoverBase.LOVER) || loverKey.equals(LoverBase.AMNESIAC_LOVER)) {
            applyPenalty(event.getLover());
        }
    }

    @EventHandler
    public void onAmnesiacLoverDeath(AmnesiacLoverDeathEvent event) {
        applyPenalty(null);
    }

    private void applyPenalty(@Nullable ILover deadLover) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
        if (!power) return;
        if (lover == null) return;
        if (deadLover != null && !deadLover.equals(lover)) return;

        power = false;
        lover = null;

        this.getPlayerWW().removePlayerMaxHealth(game.getConfig().getValue(IntValueBase.RIVAL_PENALTY_HEALTH));

        long regenIntervalTicks = game.getConfig().getTimerValue(TimerBase.RIVAL_REGEN_INTERVAL) * 20L;
        int task = BukkitUtils.scheduleSyncRepeatingTask(game,
                () -> this.getPlayerWW().addPlayerMaxHealth(game.getConfig().getValue(IntValueBase.RIVAL_REGEN_HEALTH)),
                regenIntervalTicks, regenIntervalTicks);
        BukkitUtils.scheduleSyncDelayedTask(game,
                () -> Bukkit.getScheduler().cancelTask(task),
                (long) game.getConfig().getValue(IntValueBase.RIVAL_REGEN_COUNT) * regenIntervalTicks + 100L);

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.penalty");
        Bukkit.getPluginManager().callEvent(new RivalLoverDeathEvent(this.getPlayerWW(), new ArrayList<>()));
    }

    @EventHandler
    public void onSteal(StealEvent event) {
        if (!event.getPlayerWW().equals(cupidWW)) return;
        cupidWW = event.getPlayerWW();
    }

    public void analyser(IPlayerWW p1, IPlayerWW p2, IPlayerWW p3) {

        if (lover == null) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.no_couple");
            return;
        }
        int now = game.getTimer();
        int remaining = game.getConfig().getTimerValue(TimerBase.RIVAL_ANALYSE_COOLDOWN) - (now - lastAnalyse);
        if (remaining > 0) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.cooldown",
                    Formatter.number((int) Math.ceil(remaining / 60.0)));
            return;
        }
        lastAnalyse = now;

        long count = Arrays.asList(p1, p2, p3).stream()
                .filter(p -> lover.getLovers().contains(p))
                .distinct()
                .count();

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.rival.analyse_result",
                Formatter.number((int) count));
    }

    public void deviner(IPlayerWW p1, IPlayerWW p2) {

        if (lover == null) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.no_couple");
            return;
        }
        if (guessed) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.already_guessed");
            return;
        }
        int now = game.getTimer();
        int remaining = game.getConfig().getTimerValue(TimerBase.RIVAL_DEVINER_COOLDOWN) - (now - lastDeviner);
        if (remaining > 0) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.cooldown",
                    Formatter.number((int) Math.ceil(remaining / 60.0)));
            return;
        }
        lastDeviner = now;

        List<? extends IPlayerWW> couple = lover.getLovers();
        boolean correct = couple.size() == 2
                && couple.contains(p1) && couple.contains(p2) && !p1.equals(p2);

        if (correct) {
            guessed = true;
            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.rival.deviner_success");
        } else {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.deviner_fail");
        }
    }

    public void isoler() {

        if (!guessed) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.isolate_need_guess");
            return;
        }
        if (lover == null) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.no_couple");
            return;
        }
        if (isolateUse >= game.getConfig().getValue(IntValueBase.RIVAL_ISOLATE_MAX_USE)) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.isolate_no_use");
            return;
        }

        IPlayerWW nearest = null;
        double best = game.getConfig().getValue(IntValueBase.RIVAL_ISOLATE_RANGE);
        int inRange = 0;
        for (IPlayerWW l : lover.getLovers()) {
            if (!l.isState(StatePlayer.ALIVE)) continue;
            double d = l.distance(this.getPlayerWW());
            if (d <= best) {
                best = d;
                nearest = l;
            }
            if (d <= game.getConfig().getValue(IntValueBase.RIVAL_ISOLATE_RANGE)) {
                inRange++;
            }
        }

        if (nearest == null) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.isolate_no_target");
            return;
        }

        isolatedLover = nearest;
        isolationEnd = game.getTimer() + game.getConfig().getTimerValue(TimerBase.RIVAL_ISOLATE_DURATION);
        isolateUse++;

        Bukkit.getPluginManager().callEvent(new ActionableStoryEvent(this.getPlayerWW(), "werewolf.roles.rival.isoler"));

        if (inRange >= 2) {
            this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.rival.isolate_both",
                    Formatter.player(nearest.getName()));
        } else {
            this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.rival.isolate_perform",
                    Formatter.player(nearest.getName()));
        }
    }

    public void bonus(int choice) {

        if (!killed) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.bonus_need_kill");
            return;
        }
        if (bonusChosen) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.bonus_already");
            return;
        }
        if (choice < 1 || choice > 4) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.bonus_invalid");
            return;
        }

        bonusChosen = true;

        switch (choice) {
            case 1:
                this.getPlayerWW().addItem(new ItemStack(Material.GOLDEN_APPLE, game.getConfig().getValue(IntValueBase.RIVAL_KILL_GOLDEN_APPLES)));
                break;
            case 2:
                this.getPlayerWW().addPotionModifier(
                        PotionModifier.add(UniversalPotionEffectType.SPEED, Integer.MAX_VALUE, game.getConfig().getValue(IntValueBase.RIVAL_SPEED_AMPLIFIER), this.getKey()));
                break;
            case 3:
                this.getPlayerWW().addPotionModifier(
                        PotionModifier.add(UniversalPotionEffectType.STRENGTH, this.getKey()));
                break;
            case 4:
                this.getPlayerWW().addPotionModifier(
                        PotionModifier.add(UniversalPotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, this.getKey()));
                break;
            default:
                break;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.rival.bonus_perform",
                Formatter.number(choice));
    }

    @EventHandler
    public void onDon(DonEvent event) {

        if (isolatedLover == null) return;
        if (game.getTimer() > isolationEnd) {
            isolatedLover = null;
            return;
        }
        if (event.getPlayerWW().equals(isolatedLover) || event.getReceiverWW().equals(isolatedLover)) {
            event.setCancelled(true);
            if (event.getPlayerWW().equals(isolatedLover)) {
                event.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.rival.isolate_targeted");
            } else {
                event.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.rival.isolate_deceptive");
            }
        }
    }

    @EventHandler
    @RequireAbility
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!guessed) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!event.getEntity().getUniqueId().equals(this.getPlayerUUID())) return;
        if (!(event.getDamager() instanceof Player)) return;

        IPlayerWW damagerWW = game.getPlayerWW(event.getDamager().getUniqueId()).orElse(null);
        if (damagerWW == null || lover == null) return;
        if (lover.getLovers().contains(damagerWW)) {
            event.setDamage(event.getDamage() * (1 - game.getConfig().getValue(IntValueBase.RIVAL_DAMAGE_REDUCTION) / 100.0));
        }
    }

    @Override
    public void second() {
        if (isolatedLover != null && game.getTimer() > isolationEnd) {
            isolatedLover = null;
        }
    }

    @EventHandler
    @RequireAbility
    @RequireAlive
    public void onActionBarRequest(ActionBarEvent event) {

        if (!getPlayerUUID().equals(event.getPlayerUUID())) return;

        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        if (player == null) return;

        StringBuilder stringBuilder = event.getActionBar();

        if (guessed && lover != null) {
            lover.getLovers().stream()
                    .filter(p -> p.isState(StatePlayer.ALIVE))
                    .filter(p -> !p.getUUID().equals(getPlayerUUID()))
                    .findFirst()
                    .ifPresent(l -> stringBuilder
                            .append(" ")
                            .append(ChatColor.LIGHT_PURPLE)
                            .append(game.translate("werewolf.roles.rival.lover_arrow"))
                            .append(" ")
                            .append(Utils.updateArrow(player, l.getLocation())));
        } else if (cupidWW != null && cupidWW.isState(StatePlayer.ALIVE)) {
            stringBuilder
                    .append(" ")
                    .append(ChatColor.WHITE)
                    .append(game.translate(RoleBase.CUPID))
                    .append(" ")
                    .append(Utils.updateArrow(player, cupidWW.getLocation()));
        }
    }

    @Override
    public void recoverPower() {
        this.lastAnalyse = -game.getConfig().getTimerValue(TimerBase.RIVAL_ANALYSE_COOLDOWN);
        this.lastDeviner = -game.getConfig().getTimerValue(TimerBase.RIVAL_DEVINER_COOLDOWN);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    @Override
    public int getUse() {
        return isolateUse;
    }

    @Override
    public void setUse(int use) {
        this.isolateUse = use;
    }
}
