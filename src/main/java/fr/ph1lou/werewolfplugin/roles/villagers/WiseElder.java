package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.roles.wise_elder.WiseElderRevealAuraAmountEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Hephaisto
 */
public class WiseElder extends RoleVillage {

    private int neutralCounter;
    private int darkCounter;
    private int lightCounter;
    private boolean active;

    public WiseElder(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.wise_elder.description",
                        Formatter.number(game.getConfig().getDistanceWiseElder())))
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

        if (event.getNumber() == 6) {
            this.active = true;
        }

        if (this.active) {

            WiseElderRevealAuraAmountEvent wiseElderRevealAuraAmountEvent = new WiseElderRevealAuraAmountEvent(getPlayerWW(),neutralCounter,darkCounter,lightCounter);

            Bukkit.getPluginManager().callEvent(wiseElderRevealAuraAmountEvent);

            if(wiseElderRevealAuraAmountEvent.isCancelled()){
                this.getPlayerWW().sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
                resetCounters();
                return;
            }

            getPlayerWW().sendMessageWithKey(Prefix.GREEN.getKey(),"werewolf.role.wise_elder.end_of_cycle",
                    Formatter.format("&neutral&",neutralCounter),
                    Formatter.format("&dark&",darkCounter),
                    Formatter.format("&light&",lightCounter));

            resetCounters();
        }
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BROWN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.wise_elder",
                                Formatter.number(config.getDistanceWiseElder())))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceWiseElder((config.getDistanceWiseElder() + 5));
            } else if (config.getDistanceBearTrainer() - 5 > 0) {
                config.setDistanceWiseElder(config.getDistanceWiseElder() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.wise_elder",
                            Formatter.number(config.getDistanceWiseElder())))
                    .build());

        });
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
                player.getLocation().distance(location) < game.getConfig().getDistanceWiseElder();
    }
}
