package fr.ph1lou.werewolfplugin.scenarios;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ScenarioBase;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

@Scenario(key = ScenarioBase.AUTO_BREAK, meetUpValue = true)
public class AutoBreak extends ListenerWerewolf {

    public AutoBreak(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (getGame().isState(StateGame.LOBBY)) {
            return;
        }

        Block block = event.getBlock(); // Récupère le bloc placé
        Material blockType = block.getType();

        BukkitUtils.scheduleSyncDelayedTask(getGame(), () -> {
            if (block.getType() == blockType) {
                block.setType(Material.AIR);
            }

        }, 30 * 20);
    }
}
