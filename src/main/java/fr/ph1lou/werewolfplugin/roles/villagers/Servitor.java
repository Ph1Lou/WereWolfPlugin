package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.events.roles.servitor.ServitorDefinitiveMasterEvent;
import fr.ph1lou.werewolfapi.events.roles.servitor.ServitorMasterChosenEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Héphaïsto
 */

@Role(key = RoleBase.SERVITOR,
        category = Category.VILLAGER,
        attributes = RoleAttribute.VILLAGER,
configValues = {@IntValue(key = Servitor.DISTANCE,
        defaultValue = 25, 
        meetUpValue = 25, 
        step = 5, 
        item = UniversalMaterial.BROWN_WOOL)})
public class Servitor extends RoleVillage implements IPower {

    public static final String DISTANCE = "werewolf.role.servitor.distance";
    private boolean power = true;
    private IPlayerWW master;

    public Servitor(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game,this)
                .setDescription(game.translate("werewolf.role.servitor.description"))
                .setEffects(game.translate(this.hasPower()?"werewolf.role.servitor.effects":"werewolf.role.servitor.effects_death",
                        Formatter.number(game.getConfig().getValue(DISTANCE))))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onDay(DayEvent event) {

        if(!this.isAbilityEnabled()){
            return;
        }

        if(!this.hasPower()){
            return;
        }

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        ServitorMasterChosenEvent event1 = new ServitorMasterChosenEvent(getPlayerWW(), Utils.autoSelect(game, getPlayerWW()));
        Bukkit.getPluginManager().callEvent(event1);

        if (event1.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }

        this.master = event1.getTargetWW();

        this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,"werewolf.role.servitor.message",
                Formatter.player(master.getName()));
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if(!this.isAbilityEnabled()){
            return;
        }

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        if (!isAbilityEnabled()) return;

        if (master == null) return;

        IPlayerWW playerWW = event.getPlayerWW();

        Optional<IPlayerWW> lastKiller = event.getPlayerWW().getLastKiller();

        if (!lastKiller.isPresent()) return;

        if (playerWW.equals(getPlayerWW()) && lastKiller.get().equals(master)) {
            event.setCancelled(true);
            this.getPlayerWW().sendMessageWithKey(Prefix.ORANGE,"werewolf.role.servitor.resurrection",
                    Formatter.player(lastKiller.get().getName()),
                    Formatter.number(game.getConfig().getValue(DISTANCE)));
            autoResurrection();
        }
    }

    private void autoResurrection() {
        setPower(false);

        ServitorDefinitiveMasterEvent servitorDefinitiveMasterEvent = new ServitorDefinitiveMasterEvent(getPlayerWW(), master);


        Bukkit.getPluginManager().callEvent(servitorDefinitiveMasterEvent);

        if(servitorDefinitiveMasterEvent.isCancelled()){
            this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
            return;
        }
        game.resurrection(getPlayerWW());
    }

    @Override
    public void second() {

        if(!this.isAbilityEnabled()){
            return;
        }

        if (master == null) return;

        if (!master.isState(StatePlayer.ALIVE)) {
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS, this.getKey(),0));
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE, this.getKey(),0));
            return;
        }

        if(!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        Location location = getPlayerWW().getLocation();

        if (checkDistance(master, location)) {
            if (power) {
                getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.INCREASE_DAMAGE, this.getKey())); //TODO patch potions
            } else {
                getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.WEAKNESS, this.getKey()));
            }
        } else {
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.WEAKNESS, this.getKey(),0));
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.INCREASE_DAMAGE, this.getKey(),0));
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
                player.getLocation().distance(location) < game.getConfig().getValue(DISTANCE);
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
