package io.github.ph1lou.werewolfplugin.roles.villagers;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.PotionModifier;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import io.github.ph1lou.werewolfapi.events.roles.servitor.DefinitiveMasterEvent;
import io.github.ph1lou.werewolfapi.events.roles.servitor.MasterChosenEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Héphaïsto
 */
public class Servitor extends RoleVillage implements IPower {
    private boolean power;
    private IPlayerWW master;

    public Servitor(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.servitor.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (power) {
            master = game.autoSelect(getPlayerWW());
            Bukkit.getPluginManager().callEvent(new MasterChosenEvent(getPlayerWW(),master));
        }
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.equals(getPlayerWW())) {
            event.setCancelled(true);
            autoResurrection();
        }
    }

    private void autoResurrection() {
        setPower(false);
        Bukkit.getPluginManager().callEvent(new DefinitiveMasterEvent(getPlayerWW(), master));
        game.resurrection(getPlayerWW());
    }

    @Override
    public void second() {
        Location location = getPlayerWW().getLocation();

        if (checkDistance(master, location)) {
            if (power) {
                getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, "servitor")); //TODO patch potions
            } else {
                getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS, "servitor"));
            }
        } else {
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS, "servitor",0));
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE, "servitor",0));
        }
    }

    /**
     * Check that the given PlayerWW is within 25 blocks of the Location
     *
     * @param player   the PlayerWW
     * @param location the location to compare
     * @return true if the player is within 25 blocks of the location, false otherwise
     */
    private boolean checkDistance(IPlayerWW player, Location location) {
        return player.getLocation().getWorld() == location.getWorld() &&
                player.getLocation().distance(location) < 25;
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
