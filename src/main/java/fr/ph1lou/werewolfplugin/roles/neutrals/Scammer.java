package fr.ph1lou.werewolfplugin.roles.neutrals;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.roles.scammer.ScamEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfplugin.roles.villagers.Villager;
import fr.ph1lou.werewolfplugin.roles.werewolfs.WereWolf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Héphaïsto
 */
public class Scammer extends RoleNeutral implements IAffectedPlayers, IPower {
    private final Map<IPlayerWW, Integer> affectedPlayer = new HashMap<>();
    private boolean power = true;
    private int count = 0;

    public Scammer(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.scammer.description",
                        Formatter.timer(Utils.conversion(game.getConfig().getScamDelay())),
                        Formatter.number(game.getConfig().getDistanceScammer())))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void second() {

        if(!this.isAbilityEnabled()){
            return;
        }

        this.count = this.count++ % game.getConfig().getScamDelay();

        if(count != 0){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (!hasPower()) return;

        Location location = getPlayerWW().getLocation();

        Bukkit.getOnlinePlayers().stream()
                .map(Entity::getUniqueId)
                .filter(uniqueId -> !getPlayerUUID().equals(uniqueId))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE) && checkDistance(iPlayerWW, location))
                .forEach(iPlayerWW -> {
                    int value = 0;
                    if (this.affectedPlayer.containsKey(iPlayerWW)) {
                        value = this.affectedPlayer.get(iPlayerWW);
                        if (value == 99) {
                            Bukkit.getPluginManager().callEvent(new ScamEvent(getPlayerWW(), iPlayerWW));
                            return;
                        }
                    }
                    this.affectedPlayer.put(iPlayerWW, value + 1);
                });
    }


    @EventHandler
    public void onScam(ScamEvent event) {

        if (event.isCancelled()){
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            affectedPlayer.put(event.getTargetWW(),0);
            return;
        }

        if(!event.getPlayerWW().equals(this.getPlayerWW())){
            return;
        }

        setPower(false);
        affectedPlayer.clear();
        IPlayerWW target = event.getTargetWW();
        IRole targetRole = target.getRole();
        HandlerList.unregisterAll(this);
        getPlayerWW().setRole(targetRole);
        BukkitUtils.registerEvents(targetRole);
        Bukkit.getPluginManager().callEvent(new SwapEvent(this.getPlayerWW(),target));
        IRole newRole;
        if (targetRole.isWereWolf()) {
            newRole = new WereWolf(game, target, RolesBase.WEREWOLF.getKey());
            if(targetRole.isNeutral()){
                if(targetRole.isSolitary()){
                    newRole.setSolitary(true);
                }
                else{
                    newRole.setTransformedToNeutral(true);
                }
            }
            target.sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.scammer.message_villager");
        } else {
            newRole = new Villager(game, target, RolesBase.VILLAGER.getKey());
            target.sendMessageWithKey(Prefix.ORANGE.getKey(),"werewolf.role.scammer.message_werewolf");
        }
        if (this.isInfected()) {
            targetRole.setInfected();
        } else if (targetRole.isWereWolf()) {
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
        }
        if(this.isSolitary()){
            targetRole.setSolitary(true);
        }
        targetRole.setDeathRole(this.getKey());

        newRole.disableAbilities();
        target.setRole(newRole);
        BukkitUtils.registerEvents(target.getRole());
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.scammer.message",
                Formatter.player(target.getName()));
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.put(iPlayerWW, 0);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayer.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return new ArrayList<>(affectedPlayer.keySet());
    }

    /**
     * Check that the given PlayerWW is within 20 blocks of the Location
     *
     * @param player   the PlayerWW
     * @param location the location to compare
     * @return true if the player is within 20 blocks of the location, false otherwise
     */
    private boolean checkDistance(IPlayerWW player, Location location) {
        return player.getLocation().getWorld() == location.getWorld() &&
                player.getLocation().distance(location) < game.getConfig().getDistanceScammer();
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BROWN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.scammer",
                                Formatter.number(config.getDistanceScammer())))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceScammer((config.getDistanceScammer() + 5));
            } else if (config.getDistanceScammer() - 5 > 0) {
                config.setDistanceScammer(config.getDistanceScammer() - 5);
            }

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.scammer",
                            Formatter.number(config.getDistanceScammer())))
                    .build());

        });
    }

    public static ClickableItem configDelay(WereWolfAPI game) {

        IConfiguration config = game.getConfig();

        return ClickableItem.of(
                new ItemBuilder(Material.STICK)
                        .setLore(game.translate("werewolf.role.scammer.config.lore", Formatter.timer(String.valueOf(config.getScamDelay()))))
                        .setDisplayName(game.translate("werewolf.role.scammer.config.name"))
                        .build(), e -> {
                    if (e.isLeftClick()) {
                        config.setScamDelay(config.getScamDelay() + 1);
                    } else if (e.isRightClick()) {
                        config.setScamDelay(config.getScamDelay() - 1);
                    }

                    e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                            .setLore(game.translate("werewolf.role.scammer.config.lore", Formatter.timer(String.valueOf(config.getScamDelay()))))
                            .build());
                });
    }

    @Override
    public void disableAbilities() {
        super.disableAbilities();
        setPower(false);
    }

    @Override
    public void enableAbilities() {
        super.enableAbilities();
        setPower(true);
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
