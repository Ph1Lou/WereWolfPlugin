package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.ThirdDeathEvent;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.roles.servitor.DefinitiveMasterEvent;
import fr.ph1lou.werewolfapi.roles.servitor.MasterChosenEvent;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Héphaïsto
 */
public class Servitor extends RoleVillage implements IPower {
    private boolean power = true;
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
            MasterChosenEvent event1 = new MasterChosenEvent(getPlayerWW(),game.autoSelect(getPlayerWW()));
            Bukkit.getPluginManager().callEvent(event1);

            if (!event1.isCancelled()){
                master = event1.getTargetWW();
            }
        }
    }

    @EventHandler
    public void onThirdDeathEvent(ThirdDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        if (!isAbilityEnabled()) return;

        if (master == null) return;

        IPlayerWW playerWW = event.getPlayerWW();

        Optional<IPlayerWW> lastKiller = event.getPlayerWW().getLastKiller();

        if (!lastKiller.isPresent()) return;

        if (playerWW.equals(getPlayerWW()) && lastKiller.get().equals(master)) {
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
        if (master == null) return;

        if (!master.isState(StatePlayer.ALIVE)) return;

        if(!getPlayerWW().isState(StatePlayer.ALIVE)) return;

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
                player.getLocation().distance(location) < game.getConfig().getDistanceServitor();
    }

    public static ClickableItem config(WereWolfAPI game) {
        List<String> lore = Arrays.asList(game.translate("werewolf.menu.left"),
                game.translate("werewolf.menu.right"));
        IConfiguration config = game.getConfig();

        return ClickableItem.of((
                new ItemBuilder(UniversalMaterial.BROWN_WOOL.getStack())
                        .setDisplayName(game.translate("werewolf.menu.advanced_tool.servitor",
                                Formatter.number(config.getDistanceServitor())))
                        .setLore(lore).build()), e -> {
            if (e.isLeftClick()) {
                config.setDistanceServitor((config.getDistanceServitor() + 5));
            } else if (config.getDistanceServitor() - 5 > 0) {
                config.setDistanceServitor(config.getDistanceServitor() - 5);
            }


            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(lore)
                    .setDisplayName(game.translate("werewolf.menu.advanced_tool.servitor",
                            Formatter.number(config.getDistanceServitor())))
                    .build());

        });
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
