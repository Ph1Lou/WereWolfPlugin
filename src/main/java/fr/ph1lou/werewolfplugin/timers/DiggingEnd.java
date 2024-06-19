package fr.ph1lou.werewolfplugin.timers;

import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.timers.DiggingEndEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.List;

@Timer(key = TimerBase.DIGGING,
        defaultValue = 70 * 60,
        meetUpValue = 0,
        decrement = true,
        onZero = DiggingEndEvent.class)
public class DiggingEnd extends ListenerWerewolf {

    public DiggingEnd(WereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onDiggingEnd(DiggingEndEvent event) {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    player.sendMessage(this.getGame().translate(Prefix.ORANGE, "werewolf.announcement.mining"));
                    Sound.ANVIL_BREAK.play(player);
                });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        if (this.getGame().getConfig().getTimerValue(TimerBase.DIGGING) >= 0) {
            return;
        }

        Block block = event.getBlock();


        Location loc = new Location(block.getWorld(),
                block.getLocation().getBlockX() + 0.5,
                block.getLocation().getBlockY() + 0.5,
                block.getLocation().getBlockZ() + 0.5);

        Material blockType = block.getType();

        if (UniversalMaterial.isDiamondOre(blockType) ||
            UniversalMaterial.isGoldOre(blockType) ||
            UniversalMaterial.isIronOre(blockType) ||
            UniversalMaterial.isRedstoneOre(blockType) ||
            UniversalMaterial.isEmeraldOre(blockType) ||
            UniversalMaterial.isLapisOre(blockType) ||
            UniversalMaterial.isCoalOre(blockType)) {

            block.getWorld().spawn(loc, ExperienceOrb.class).setExperience(event.getExpToDrop());
            block.setType(Material.AIR);
        }
    }
}
