package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import fr.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispRecoverRoleEvent;
import fr.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispTeleportEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IInvisible;
import fr.ph1lou.werewolfapi.role.interfaces.ILimitedUse;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import fr.ph1lou.werewolfapi.enums.UniversalPotionEffectType;
import org.bukkit.util.Vector;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Role(key = RoleBase.WILL_O_THE_WISP,
        category = Category.NEUTRAL, attribute = RoleAttribute.NEUTRAL,
        configValues = {@IntValue(key = IntValueBase.WILL_O_THE_WISP_DISTANCE,
                defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.YELLOW_WOOL)},
        timers = {@Timer (key = TimerBase.WILL_O_THE_WISP_DURATION_INCENDIARY_MADNESS, defaultValue = 45, meetUpValue = 45),
                @Timer(key = TimerBase.WILL_O_THE_WISP_COOLDOWN_INCENDIARY_MADNESS, defaultValue = 10*60, meetUpValue = 5*60),
                @Timer(key = TimerBase.WILL_O_THE_WISP_COOLDOWN_TP, defaultValue = 10*60, meetUpValue = 5*60)})
public class WillOTheWisp extends RoleNeutral implements IInvisible, ILimitedUse, IPower {

    public final static String INCENDIARY_MADNESS = "incendiary_madness";
    private boolean invisible = false;
    private int use = 0;
    private int timer = -1;
    private boolean power = true;

    private boolean canBeTeleported = true;
    private final Map<UUID, Integer> onFirePlayers = new HashMap<>();

    public WillOTheWisp(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.will_o_the_wisp.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.WILL_O_THE_WISP_DISTANCE))))
                .setEffects(game.translate("werewolf.roles.will_o_the_wisp.effects"))
                .addExtraLines(game.translate("werewolf.roles.will_o_the_wisp.feather",
                        Formatter.number(game.getConfig().getValue(IntValueBase.WILL_O_THE_WISP_DISTANCE)),
                        Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_COOLDOWN_TP)))))
                .setCommand(game.translate("werewolf.roles.will_o_the_wisp.incendiary_madness",
                        Formatter.timer(Utils.conversion(game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_COOLDOWN_INCENDIARY_MADNESS))),
                        Formatter.format("&timer2&", Utils.conversion(game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_DURATION_INCENDIARY_MADNESS)))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void second() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!this.isInvisible()) {
            return;
        }

        game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles instanceof IInvisible)
                .map(roles -> (IInvisible) roles)
                .filter(IInvisible::isInvisible)
                .map(IInvisible -> {
                    if (((IRole) IInvisible).isWereWolf()) {
                        return new Pair<>(Material.REDSTONE_BLOCK,
                                ((IRole) IInvisible).getPlayerUUID());
                    } else return new Pair<>(Material.LAPIS_BLOCK,
                            ((IRole) IInvisible).getPlayerUUID());
                })
                .map(objects -> new Pair<>(objects.getValue0(),
                        Bukkit.getPlayer(objects.getValue1())))
                .filter(objects -> objects.getValue1() != null)
                .map(objects -> new Pair<>(objects.getValue0(),
                        objects.getValue1().getLocation()))
                .forEach(objects -> player.playEffect(objects.getValue1(),
                        Effect.STEP_SOUND, objects.getValue0()));

    }


    @EventHandler
    public void onNight(NightEvent event) {


        if (!this.isAbilityEnabled()) {
            return;
        }

        List<IPlayerWW> playerWWList = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .filter(uuid -> !this.getPlayerUUID().equals(uuid))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> {
                    Location wildLocation = this.getPlayerWW().getLocation();
                    Location playerLocation = playerWW.getLocation();
                    return wildLocation.getWorld() == playerLocation.getWorld() &&
                            wildLocation.distance(playerLocation) < game.getConfig().getValue(IntValueBase.WILL_O_THE_WISP_DISTANCE);
                })
                .collect(Collectors.toList());

        Collections.shuffle(playerWWList, game.getRandom());

        if (playerWWList.isEmpty()) {
            return;
        }

        WillOTheWispRecoverRoleEvent event1 = new WillOTheWispRecoverRoleEvent(this.getPlayerWW(),
                playerWWList.get(0),
                playerWWList.get(0).getRole().getDisplayRole());

        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.will_o_the_wisp.role_reveal",
                Formatter.number(game.getConfig().getValue(IntValueBase.WILL_O_THE_WISP_DISTANCE)),
                Formatter.role(game.translate(event1.getRoleKey())));

    }

    @EventHandler
    public void onInteraction(EntityDamageByEntityEvent event) {

        if (!event.getDamager().getUniqueId().equals(this.getPlayerUUID())) {
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if(this.getPlayerWW().getPotionModifiers().stream()
                .noneMatch(potionModifier -> potionModifier.getIdentifier().equals(INCENDIARY_MADNESS))){
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();

        if(this.onFirePlayers.containsKey(damagedPlayer.getUniqueId())){
            Bukkit.getScheduler().cancelTask(this.onFirePlayers.get(damagedPlayer.getUniqueId()));
        }

        this.onFirePlayers.put(damagedPlayer.getUniqueId(), BukkitUtils.scheduleSyncDelayedTask(game,
                () -> this.onFirePlayers.remove(damagedPlayer.getUniqueId()),
                8*20));
        damagedPlayer.setFireTicks(8* 20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if(!event.isCancelled()){
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)
            && this.onFirePlayers.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(false);
        }
    }


    @EventHandler
    public void onInteractWithFeather(PlayerInteractEvent playerInteractEvent){


        if(playerInteractEvent.getItem() == null || playerInteractEvent.getItem().getType() != Material.FEATHER){
            return;
        }

        if(!playerInteractEvent.getPlayer().getUniqueId().equals(this.getPlayerUUID())){
            return;
        }

        if(!this.canBeTeleported){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.will_o_the_wisp.wait_cooldown_tp");
            return;
        }

        if (!this.isInvisible()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.will_o_the_wisp.should_be_invisible");
            return;
        }

        this.canBeTeleported = false;
        BukkitUtils.scheduleSyncDelayedTask(game, () -> {
            this.canBeTeleported = true;
            if(this.getPlayerWW().isState(StatePlayer.ALIVE)){
                this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.will_o_the_wisp.colldown_end_tp");
            }
        }, game.getConfig().getTimerValue(TimerBase.WILL_O_THE_WISP_COOLDOWN_TP) * 20L);

        WillOTheWispTeleportEvent willOTheWispTeleportEvent = new WillOTheWispTeleportEvent(this.getPlayerWW(), this.getUse());
        Bukkit.getPluginManager().callEvent(willOTheWispTeleportEvent);
        this.setUse(this.getUse() + 1);

        if (willOTheWispTeleportEvent.isCancelled()) {
            this.getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }

        Vector vector = this.getPlayerWW().getEyeLocation().getDirection();
        vector
                .normalize()
                .multiply(game.getConfig().getValue(IntValueBase.WILL_O_THE_WISP_DISTANCE))
                .setY(Objects.requireNonNull(this.getPlayerWW().getLocation().getWorld()).getHighestBlockYAt(this.getPlayerWW().getLocation()) - this.getPlayerWW().getLocation().getBlockY() + 10);

        this.getPlayerWW().teleport(this.getPlayerWW().getLocation().add(vector));
        this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.WITHER,
                400,
                0,
                this.getPlayerWW().getRole().getKey()));
    }

    @EventHandler
    public void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!uuid.equals(this.getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!this.game.isState(StateGame.GAME)) return;

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (inventory.getItem(36) == null &&
                inventory.getItem(37) == null &&
                inventory.getItem(38) == null &&
                inventory.getItem(39) == null) {
            if (!this.isInvisible()) {
                if (!isAbilityEnabled()) {
                    getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.check.ability_disabled");
                    return;
                }

                player.sendMessage(game.translate(Prefix.GREEN,
                        "werewolf.roles.little_girl.remove_armor_perform"));

                this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.INVISIBILITY,
                        this.getKey()));
                this.getPlayerWW().addPotionModifier(PotionModifier.add(UniversalPotionEffectType.ABSORPTION,
                        Integer.MAX_VALUE, 1,
                        this.getKey()));

                this.timer = BukkitUtils.scheduleSyncDelayedTask(game, () -> {
                    if (this.isInvisible()) {
                        this.setInvisible(false);
                        this.getPlayerWW().addPotionModifier(PotionModifier
                                .remove(UniversalPotionEffectType.INVISIBILITY,
                                        this.getKey(),
                                        0));
                        this.getPlayerWW().addPotionModifier(PotionModifier
                                .remove(UniversalPotionEffectType.ABSORPTION,
                                        this.getKey(),
                                        1));
                        Bukkit.getPluginManager().callEvent(new InvisibleEvent(this.getPlayerWW(), false));
                        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
                        this.timer = -1;
                    }
                }, 6000);
                if (isInfected() && game.isDay(Day.NIGHT)) {
                    this.getPlayerWW().addPotionModifier(PotionModifier
                            .remove(UniversalPotionEffectType.STRENGTH,
                                    RoleBase.WEREWOLF,
                                    0));

                }
                this.setInvisible(true);
                Bukkit.getPluginManager().callEvent(new InvisibleEvent(this.getPlayerWW(), true));
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
            }
        } else if (this.isInvisible()) {
            player.sendMessage(game.translate(
                    Prefix.YELLOW, "werewolf.roles.little_girl.visible"));
            if (this.isInfected() && game.isDay(Day.NIGHT)) {
                this.getPlayerWW().addPotionModifier(PotionModifier
                        .add(UniversalPotionEffectType.STRENGTH,
                                RoleBase.WEREWOLF));
            }
            this.getPlayerWW().addPotionModifier(PotionModifier
                    .remove(UniversalPotionEffectType.INVISIBILITY,
                            this.getKey(),
                            0));
            this.getPlayerWW().addPotionModifier(PotionModifier
                    .remove(UniversalPotionEffectType.ABSORPTION,
                            this.getKey(),
                            1));
            if (this.timer != -1) {
                Bukkit.getScheduler().cancelTask(this.timer);
                this.timer = -1;
            }
            this.setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @Override
    public void disableAbilitiesRole() {

        if (isInvisible()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED, "werewolf.roles.little_girl.ability_disabled");
            this.getPlayerWW().addPotionModifier(PotionModifier
                    .remove(UniversalPotionEffectType.STRENGTH,
                            RoleBase.WEREWOLF, 0));
            this.getPlayerWW().addPotionModifier(PotionModifier
                    .remove(UniversalPotionEffectType.ABSORPTION,
                            this.getKey(), 1));
            this.getPlayerWW().addPotionModifier(PotionModifier
                    .remove(UniversalPotionEffectType.INVISIBILITY,
                            this.getKey(), 0));

            setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
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
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }
}
