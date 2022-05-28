package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerManager;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Scenario(key = ScenarioBase.DIAMOND_LIMIT, defaultValue = true,
        configValues = @IntValue(key = DiamondLimit.LIMIT, defaultValue = 17,
                meetUpValue = 0,
                step = 1,
        item = UniversalMaterial.DIAMOND))
public class DiamondLimit extends ListenerManager {

    public static final String LIMIT = "werewolf.menu.scenarios.diamond_limit_config.diamond";

    final Map<String, Integer> diamondPerPlayer = new HashMap<>();

    public DiamondLimit(GetWereWolfAPI main) {
        super(main);
    }


    @EventHandler
    public void onGameStop(StopEvent event) {
        diamondPerPlayer.clear();
    }


    @EventHandler(ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {

        WereWolfAPI game = this.getGame();
        if (game.isState(StateGame.LOBBY)) return;

        String playerName = event.getPlayer().getName();
        Block block = event.getBlock();

        if (!block.getType().equals(Material.DIAMOND_ORE)) return;

        Location loc = new Location(block.getWorld(),
                block.getLocation().getBlockX() + 0.5,
                block.getLocation().getBlockY() + 0.5,
                block.getLocation().getBlockZ() + 0.5);

        if (!VersionUtils.getVersionUtils()
                .getItemInHand(event.getPlayer())
                .getType().equals(Material.DIAMOND_PICKAXE) &&
                !VersionUtils.getVersionUtils()
                        .getItemInHand(event.getPlayer())
                        .getType().equals(Material.IRON_PICKAXE)) {
            return;
        }
        if (diamondPerPlayer.getOrDefault(playerName, 0) >=
                game.getConfig().getValue(LIMIT)) {
            block.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
            block.setType(Material.AIR);
        }
        diamondPerPlayer.put(playerName, diamondPerPlayer.getOrDefault(playerName, 0) + 1);
    }
}
