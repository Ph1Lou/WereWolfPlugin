package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.buildings.IBuildingsManager;
import fr.ph1lou.werewolfapi.buildings.IVoteOffice;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.buildings.UrnVote1StartEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.guis.UrnChoiceGui;
import fr.ph1lou.werewolfplugin.roles.werewolfs.MesquinWereWolf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BuildingListener implements Listener {

    private final WereWolfAPI game;

    public BuildingListener(WereWolfAPI game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUrnVote1Start(UrnVote1StartEvent event) {
        game.getBuildingsManager().revealBuildings();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (game.getState() != StateGame.GAME) {
            return;
        }
        Block block = event.getBlock();
        if (game.getBuildingsManager().isProtected(block.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (game.getState() != StateGame.GAME) {
            return;
        }
        Block block = event.getBlock();
        if (game.getBuildingsManager().isProtected(block.getLocation())) {
            event.setCancelled(true);
        }
    }
}
