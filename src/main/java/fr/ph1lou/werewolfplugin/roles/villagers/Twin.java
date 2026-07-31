package fr.ph1lou.werewolfplugin.roles.villagers;


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
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.AutoTwinEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinListEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRevealEvent;
import fr.ph1lou.werewolfapi.events.roles.twin.TwinRoleEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Role(key = RoleBase.TWIN,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        defaultAura = Aura.LIGHT,
        requireDouble = true,
        timers = { @Timer(key = TimerBase.TWIN_DURATION,
                defaultValue = 2700, meetUpValue = 300,
                decrementAfterRole = true,
                onZero = AutoTwinEvent.class) },
        configValues = { @IntValue(key = IntValueBase.TWIN_DISTANCE,
                defaultValue = 50,
                meetUpValue = 50, step = 5, item = UniversalMaterial.GREEN_WOOL) })
public class Twin extends RoleImpl {

    @Nullable
    private List<IPlayerWW> twinList;

    private static final float DEFAULT_WALK_SPEED = 0.2f;
    private int dayNumber = -8;
    private boolean rageActive = false;

    public Twin(WereWolfAPI api, IPlayerWW playerWW) {
        super(api, playerWW);
    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.twin.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)),
                        Formatter.format("&number2&", game.getConfig().getValue(IntValueBase.TWIN_DISTANCE) * 2)))
                .setEffects(game.translate("werewolf.roles.twin.effects", Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE))))
                .setPower(this.twinList == null ?
                        game.translate("werewolf.roles.twin.timer", Formatter.timer(game, TimerBase.TWIN_DURATION))
                        : game.translate("werewolf.roles.twin.twin_list", Formatter.format("&list&", this.twinList
                        .stream()
                        .map(IPlayerWW::getName)
                        .collect(Collectors.joining(", "))))
                )
                .build();
    }

    @EventHandler
    public void onTwinReveal(AutoTwinEvent event) {

        this.twinList = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.getRole().isKey(RoleBase.TWIN))
                .filter(playerWW -> !playerWW.equals(this.getPlayerWW()))
                .collect(Collectors.toList());

        TwinRevealEvent twinRevealEvent = new TwinRevealEvent(this.getPlayerWW(), new HashSet<>(this.twinList));

        Bukkit.getPluginManager().callEvent(twinRevealEvent);

        if (twinRevealEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.BLUE, "werewolf.roles.twin.twin_list",
                Formatter.format("&list&", this.twinList
                        .stream()
                        .map(IPlayerWW::getName)
                        .collect(Collectors.joining(", "))));
    }

    @Override
    public void recoverPower() {
    }

    @EventHandler
    public void onDay(DayEvent event) {
        this.dayNumber = event.getNumber();
    }

    @EventHandler
    public void onTwinDeath(FinalDeathEvent event) {

        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        if (!event.getPlayerWW().getRole().isKey(RoleBase.TWIN)) {
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.twin.death", Formatter.player(event.getPlayerWW().getName()));

        this.rageActive = true;
        this.getPlayerWW().setWalkSpeed(DEFAULT_WALK_SPEED * 1.1f);

        new BukkitRunnable() {
            @Override
            public void run() {
                Twin.this.rageActive = false;
                Twin.this.getPlayerWW().setWalkSpeed(DEFAULT_WALK_SPEED);
            }
        }.runTaskLater(JavaPlugin.getPlugin(Main.class), 6000);
    }

    @EventHandler
    public void onTwinRageDamage(EntityDamageByEntityEvent event) {

        if (!this.rageActive) return;
        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        if (!damager.getUniqueId().equals(this.getPlayerUUID())) return;

        if (damager.hasPotionEffect(UniversalPotionEffectType.STRENGTH.getPotionEffectType())) return;

        event.setDamage(event.getDamage() * (1 + game.getConfig().getStrengthRate() / 200f));
    }

    @EventHandler
    public void onNight(NightEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (this.twinList == null) {
            return;
        }

        if (this.getPlayerWW().getPotionModifiers().stream()
                .anyMatch(pm -> pm.getPotionEffectType() == UniversalPotionEffectType.WEAKNESS
                        || pm.getPotionEffectType() == UniversalPotionEffectType.SLOWNESS)) {
            return;
        }

        this.twinList.stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .findFirst()
                .ifPresent(playerWW -> {

                    if (playerWW.distance(getPlayerWW()) < game.getConfig().getValue(IntValueBase.TWIN_DISTANCE) * 2) {
                        this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.twin.too_near");
                        return;
                    }

                    List<IPlayerWW> nearPlayers = game.getAlivePlayersWW()
                            .stream()
                            .filter(playerWW1 -> !playerWW1.equals(playerWW))
                            .filter(playerWW1 -> !playerWW1.equals(this.getPlayerWW()))
                            .filter(playerWW1 -> playerWW.distance(playerWW1) < game.getConfig().getValue(IntValueBase.TWIN_DISTANCE))
                            .collect(Collectors.toList());

                    int twinIndex = this.getPlayerUUID().compareTo(playerWW.getUUID()) < 0 ? 0 : 1;
                    boolean giveList = ((this.dayNumber + twinIndex) % 2 == 0);

                    if (giveList) {

                        if (nearPlayers.isEmpty()) {
                            return;
                        }

                        TwinListEvent twinListEvent = new TwinListEvent(this.getPlayerWW(), new HashSet<>(nearPlayers));

                        Bukkit.getPluginManager().callEvent(twinListEvent);

                        if (twinListEvent.isCancelled()) {
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                            return;
                        }

                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.twin.list_near",
                                Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)),
                                Formatter.format("&list&", twinListEvent.getPlayerWWS().stream().map(IPlayerWW::getName).collect(Collectors.joining(", "))));
                    } else {
                        Collections.shuffle(nearPlayers, game.getRandom());

                        if (nearPlayers.isEmpty()) {
                            return;
                        }

                        TwinRoleEvent twinRoleEvent = new TwinRoleEvent(this.getPlayerWW(), nearPlayers.get(0));

                        Bukkit.getPluginManager().callEvent(twinRoleEvent);

                        if (twinRoleEvent.isCancelled()) {
                            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
                            return;
                        }

                        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.twin.role_near",
                                Formatter.number(game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)),
                                Formatter.role(game.translate(twinRoleEvent.getTargetWW().getRole().getDisplayRole())));
                    }
                });
    }

    @EventHandler
    public void onGoldenAppleEat(PlayerItemConsumeEvent event) {

        if (!isAbilityEnabled()) return;
        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;
        if (this.twinList == null) return;

        if (!event.getPlayer().getUniqueId().equals(this.getPlayerUUID())) return;

        ItemStack item = event.getItem();
        if (UniversalMaterial.ENCHANTED_GOLDEN_APPLE.getStack(item.getAmount()).equals(item)) return;
        if (!item.getType().equals(Material.GOLDEN_APPLE)) return;

        this.twinList.stream()
                .filter(twin -> twin.isState(StatePlayer.ALIVE))
                .findFirst()
                .ifPresent(twin -> {
                    if (this.getPlayerWW().distance(twin) > game.getConfig().getValue(IntValueBase.TWIN_DISTANCE)) {
                        twin.addPotionModifier(PotionModifier.add(
                                UniversalPotionEffectType.ABSORPTION, 2400, 0, this.getKey()));
                        twin.sendMessageWithKey(Prefix.BLUE, "werewolf.roles.twin.absorption_shared",
                                Formatter.player(this.getPlayerWW().getName()));
                    }
                });
    }

}
