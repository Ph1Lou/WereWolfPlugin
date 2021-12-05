package io.github.ph1lou.werewolfplugin.roles.neutrals;

import fr.minuskube.inv.ClickableItem;
import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IConfiguration;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.GoldenAppleParticleEvent;
import io.github.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.roles.will_o_the_wisp.WillOTheWispRecoverRoleEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IInvisible;
import io.github.ph1lou.werewolfapi.rolesattributs.ILimitedUse;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import io.github.ph1lou.werewolfapi.utils.BukkitUtils;
import io.github.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class WillOTheWisp extends RoleNeutral implements IInvisible, ILimitedUse {

    private boolean invisible = false;
    private int use=0;
    private int timer=-1;

    public WillOTheWisp(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.will_o_the_wisp.description",
                        Formatter.number(game.getConfig().getDistanceWillOTheWisp())))
                .setEffects(game.translate("werewolf.role.will_o_the_wisp.effects"))
                .setCommand(game.translate("werewolf.role.will_o_the_wisp.command_info",
                        Formatter.number(2-this.use),
                        Formatter.format("&number2&", game.getConfig().getDistanceWillOTheWisp())))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void recoverPotionEffect() {
        if(!this.isAbilityEnabled()){
            return;
        }

        if(game.isDay(Day.DAY)){
            this.getPlayerWW().addPotionModifier(PotionModifier
                    .add(PotionEffectType.SPEED,"wild_o_the_wisp"));
        }
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
    public void onNight(NightEvent event){


        this.getPlayerWW().addPotionModifier(PotionModifier
                .remove(PotionEffectType.SPEED,"wild_o_the_wisp",0));

        if(!this.isAbilityEnabled()){
            return;
        }

        List<IPlayerWW> playerWWList = Bukkit.getOnlinePlayers()
                .stream()
                .map(Entity::getUniqueId)
                .filter(uuid -> !this.getPlayerUUID().equals(uuid))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(playerWW -> {
                    Location wildLocation = this.getPlayerWW().getLocation();
                    Location playerLocation = playerWW.getLocation();
                    return wildLocation.getWorld() == playerLocation.getWorld() &&
                            wildLocation.distance(playerLocation) < game.getConfig().getDistanceWillOTheWisp();
                })
                .collect(Collectors.toList());

        Collections.shuffle(playerWWList, game.getRandom());

        if(playerWWList.isEmpty()){
            return;
        }

        WillOTheWispRecoverRoleEvent event1 = new WillOTheWispRecoverRoleEvent(this.getPlayerWW(),
                playerWWList.get(0),
                playerWWList.get(0).getRole().getDisplayRole());

        Bukkit.getPluginManager().callEvent(event1);

        if(event1.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        this.getPlayerWW().sendMessageWithKey(Prefix.YELLOW.getKey(),"werewolf.role.will_o_the_wisp.role_reveal",
                Formatter.number(game.getConfig().getDistanceWillOTheWisp()),
                Formatter.role(game.translate(event1.getRoleKey())));

    }

    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        this.setInvisible(false);
    }

    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

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
                    getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.ability_disabled");
                    return;
                }

                player.sendMessage(game.translate(Prefix.GREEN.getKey() ,
                        "werewolf.role.little_girl.remove_armor_perform"));
                this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE.getKey(),
                        "werewolf.role.will_o_the_wisp.use_tp",Formatter.number(2-this.use));
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INVISIBILITY,
                        "will_o_the_wisp"));
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.ABSORPTION,
                        Integer.MAX_VALUE,1,
                        "will_o_the_wisp"));

                this.timer = BukkitUtils.scheduleSyncDelayedTask(() -> {
                    if(this.isInvisible()){
                        this.setInvisible(false);
                        Bukkit.getPluginManager().callEvent(new InvisibleEvent(this.getPlayerWW(), false));
                        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
                        this.timer=-1;
                    }
                },6000);
                if (isInfected()) {
                    this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"werewolf",0));

                }
                this.setInvisible(true);
                Bukkit.getPluginManager().callEvent(new InvisibleEvent(this.getPlayerWW(), true));
                Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
            }
        } else if (this.isInvisible()) {
            player.sendMessage(game.translate(
                    Prefix.YELLOW.getKey() , "werewolf.role.little_girl.visible"));
            if (this.isInfected()) {
                this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE,"werewolf"));
            }
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INVISIBILITY,"will_o_the_wisp",0));
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.ABSORPTION,"will_o_the_wisp",1));
            if(this.timer != -1){
                Bukkit.getScheduler().cancelTask(this.timer);
                this.timer=-1;
            }
            this.setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        if (!event.getPlayerWW().equals(this.getPlayerWW())) return;

        if (!this.isInvisible()) return;

        if (this.game.isDay(Day.DAY)) return;

        event.setCancelled(true);
    }

    @Override
    public void disableAbilities() {
        super.disableAbilities();

        if (isInvisible()) {
            getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.role.little_girl.ability_disabled");
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE,"werewolf",0));
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.ABSORPTION,"will_o_the_wisp",1));
            this.getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INVISIBILITY,"will_o_the_wisp",0));

            setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(this.getPlayerWW(), false));
            Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(this.getPlayerWW()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onResurrection(ResurrectionEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        this.setInvisible(false);
    }

    @EventHandler
    public void onDay(DayEvent event){

        if(!this.isAbilityEnabled()){
            return;
        }

        this.getPlayerWW().addPotionModifier(PotionModifier
                .add(PotionEffectType.SPEED,"wild_o_the_wisp"));
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
        this.use=use;
    }

    public static ClickableItem config(WereWolfAPI game) {

        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((new ItemBuilder(
                UniversalMaterial.YELLOW_WOOL.getStack())
                .setDisplayName(game.translate("werewolf.menu.advanced_tool.will_o_the_wisp",
                        Formatter.number(config.getDistanceWillOTheWisp())))
                .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceWillOTheWisp((config.getDistanceWillOTheWisp() + 5));
            } else if (config.getDistanceWillOTheWisp() - 5 > 0) {
                config.setDistanceWillOTheWisp(config.getDistanceWillOTheWisp() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.will_o_the_wisp",
                            Formatter.number(config.getDistanceWillOTheWisp())))
                    .build());

        });
    }
}
