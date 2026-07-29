package fr.ph1lou.werewolfplugin.listeners;

import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.buildings.BuildingsManager;
import fr.ph1lou.werewolfplugin.buildings.VoteOffice;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.guis.UrnChoiceGui;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BuildingListener implements Listener {

    private final GameManager game;

    public BuildingListener(WereWolfAPI game) {
        this.game = (GameManager) game;
    }

    @EventHandler
    public void onJukeboxInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.JUKEBOX) {
            return;
        }
        if (game.getState() != StateGame.GAME) {
            return;
        }
        BuildingsManager manager = game.getBuildingsManager();
        VoteOffice office = manager.getVoteOffice(block.getLocation());
        if (office == null) {
            return;
        }

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);
        if (playerWW == null || !playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) {
            return;
        }

        event.setCancelled(true);

        if (!manager.getUrnVoteManager().isActive()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.urn_vote_not_active");
            return;
        }

        UrnChoiceGui.getInventory(office).open(player);
    }
}
