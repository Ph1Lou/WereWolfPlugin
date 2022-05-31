package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.wise_elder.WiseElderRevealAuraAmountEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Hephaisto
 */

@Role(key = RoleBase.WISE_ELDER, 
        category = Category.VILLAGER, 
        attributes = {RoleAttribute.VILLAGER, RoleAttribute.MINOR_INFORMATION},
        configValues = {@IntValue(key = IntValueBase.WISE_ELDER_DISTANCE,
                defaultValue = 15,
                meetUpValue = 15,
                step = 3,
                item = UniversalMaterial.BROWN_WOOL),
                @IntValue(key = IntValueBase.WISE_ELDER_BEGIN_DAY,
                        defaultValue = 6,
                        meetUpValue = 3,
                        step = 1,
                        item = UniversalMaterial.BED)})
public class WiseElder extends RoleVillage {

    private int neutralCounter;
    private int darkCounter;
    private int lightCounter;
    private boolean active;

    public WiseElder(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.roles.wise_elder.description",
                        Formatter.number(game.getConfig().getValue(IntValueBase.WISE_ELDER_DISTANCE)),
                        Formatter.format("&day&",
                                game.getConfig().getValue(IntValueBase.WISE_ELDER_DISTANCE))))
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

        if(!this.getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if (event.getNumber() == game.getConfig().getValue(IntValueBase.WISE_ELDER_BEGIN_DAY)) {
            this.active = true;
        }

        if (this.active) {

            WiseElderRevealAuraAmountEvent wiseElderRevealAuraAmountEvent = new WiseElderRevealAuraAmountEvent(getPlayerWW(),neutralCounter,darkCounter,lightCounter);

            Bukkit.getPluginManager().callEvent(wiseElderRevealAuraAmountEvent);

            if(wiseElderRevealAuraAmountEvent.isCancelled()){
                this.getPlayerWW().sendMessageWithKey(Prefix.RED , "werewolf.check.cancel");
                resetCounters();
                return;
            }

            getPlayerWW().sendMessageWithKey(Prefix.GREEN,"werewolf.roles.wise_elder.end_of_cycle",
                    Formatter.format("&neutral&",neutralCounter),
                    Formatter.format("&dark&",darkCounter),
                    Formatter.format("&light&",lightCounter));

            resetCounters();
        }
    }

    @Override
    public void second() {

        if(!this.isAbilityEnabled()){
            return;
        }

        if (!active) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        Location location = getPlayerWW().getLocation();
        Bukkit.getOnlinePlayers().stream()
                .map(Entity::getUniqueId)
                .filter(uniqueId -> !getPlayerUUID().equals(uniqueId))
                .map(game::getPlayerWW)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(iPlayerWW -> iPlayerWW.isState(StatePlayer.ALIVE) && checkDistance(iPlayerWW,location))
                .map(IPlayerWW::getRole)
                .map(IRole::getAura)
                .forEach(aura -> {
                    switch (aura) {
                        case NEUTRAL:
                            neutralCounter++;
                            break;
                        case DARK:
                            darkCounter++;
                            break;
                        case LIGHT:
                            lightCounter++;
                            break;
                    }
                });
    }

    /**
     * Reset all the aura counters
     */
    private void resetCounters() {
        neutralCounter = 0;
        darkCounter = 0;
        lightCounter = 0;
    }

    /**
     * Check that the given PlayerWW is within 15 blocks of the Location
     * @param player the PlayerWW
     * @param location the location to compare
     * @return true if the player is within 15 blocks of the location, false otherwise
     */
    private boolean checkDistance(IPlayerWW player, Location location) {
        return player.getLocation().getWorld() == location.getWorld() &&
                player.getLocation().distance(location) < game.getConfig().getValue(IntValueBase.WISE_ELDER_DISTANCE);
    }
}
