package fr.ph1lou.werewolfplugin.roles.neutrals;

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
import fr.ph1lou.werewolfapi.events.random_events.SwapEvent;
import fr.ph1lou.werewolfapi.events.roles.scammer.ScamEvent;
import fr.ph1lou.werewolfapi.events.werewolf.NewWereWolfEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleNeutral;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfplugin.roles.villagers.Villager;
import fr.ph1lou.werewolfplugin.roles.werewolfs.WereWolf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Héphaïsto
 */

@Role(key = RoleBase.SCAMMER,
        defaultAura = Aura.NEUTRAL,
        category = Category.NEUTRAL,
        attributes = RoleAttribute.HYBRID,
        incompatibleRoles = {RoleBase.CHARMER},
        timers = {@Timer(key = TimerBase.SCAMMER_DELAY, defaultValue = 9, meetUpValue = 3)},
        configValues = {@IntValue(key = IntValueBase.SCAMMER_DISTANCE,
                defaultValue = 20, meetUpValue = 20, step = 2, item = UniversalMaterial.BROWN_WOOL)})
public class Scammer extends RoleNeutral implements IAffectedPlayers, IPower {

    private final Map<IPlayerWW, Integer> affectedPlayer = new HashMap<>();
    private boolean power = true;
    private int count = 0;

    public Scammer(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.scammer.description",
                        Formatter.timer(game, TimerBase.SCAMMER_DELAY),
                        Formatter.number(game.getConfig().getValue(IntValueBase.SCAMMER_DISTANCE))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public void second() {

        if (!this.isAbilityEnabled()) {
            return;
        }

        this.count = this.count++ % game.getConfig().getTimerValue(TimerBase.SCAMMER_DELAY);

        if (count != 0) {
            return;
        }

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) return;

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
                    int value = this.affectedPlayer.getOrDefault(iPlayerWW, 0);
                    if (value == 100) {
                        Bukkit.getPluginManager().callEvent(new ScamEvent(getPlayerWW(), iPlayerWW));
                        return;
                    }
                    this.affectedPlayer.put(iPlayerWW, value + 1);
                });
    }


    @EventHandler
    public void onScam(ScamEvent event) {

        if (event.isCancelled()) {
            getPlayerWW().sendMessageWithKey("werewolf.check.cancel");
            affectedPlayer.put(event.getTargetWW(), 0);
            return;
        }

        if (!event.getPlayerWW().equals(this.getPlayerWW())) {
            return;
        }

        this.setPower(false);
        this.affectedPlayer.clear();
        IPlayerWW target = event.getTargetWW();
        IRole targetRole = target.getRole();
        targetRole.setPlayerWW(this.getPlayerWW());
        HandlerList.unregisterAll(this);
        getPlayerWW().setRole(targetRole);
        Bukkit.getPluginManager().callEvent(new SwapEvent(this.getPlayerWW(), target));
        IRole newRole;
        if (targetRole.isWereWolf()) {
            newRole = new WereWolf(game, target);
            if (targetRole.isNeutral()) {
                if (targetRole.isSolitary()) {
                    newRole.setSolitary(true);
                } else {
                    newRole.setTransformedToNeutral(true);
                }
            }
            target.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.scammer.message_werewolf");

        } else {
            newRole = new Villager(game, target);
            target.sendMessageWithKey(Prefix.ORANGE, "werewolf.roles.scammer.message_villager");
        }
        if (this.isInfected()) {
            targetRole.setInfected();
        } else if (targetRole.isWereWolf()) {
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(getPlayerWW()));
        }
        if (this.isSolitary()) {
            targetRole.setSolitary(true);
        }
        target.addDeathRole(targetRole.getKey());
        this.getPlayerWW().addDeathRole(this.getKey());
        targetRole.removeTemporaryAuras();
        targetRole.recoverPower();
        targetRole.recoverPotionEffects();
        target.addPlayerMaxHealth(20 - target.getMaxHealth());
        target.clearPotionEffects(targetRole.getKey());
        newRole.disableAbilities();
        target.setRole(newRole);
        BukkitUtils.registerListener(target.getRole());
        this.getPlayerWW().sendMessageWithKey(Prefix.GREEN, "werewolf.roles.scammer.message",
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
                player.getLocation().distance(location) < game.getConfig().getValue(IntValueBase.SCAMMER_DISTANCE);
    }

    @Override
    public void disableAbilitiesRole() {
        setPower(false);
    }

    @Override
    public void enableAbilitiesRole() {
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
