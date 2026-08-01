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
import org.bukkit.event.player.PlayerInteractEvent;

public class UrnVoteListener implements Listener {

    private final WereWolfAPI game;
    private final IBuildingsManager manager;

    public UrnVoteListener(WereWolfAPI game) {
        this.game = game;
        this.manager = game.getBuildingsManager();
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
        IVoteOffice office = manager.getVoteOffice(block.getLocation());
        if (office == null) {
            return;
        }

        Player player = event.getPlayer();
        IPlayerWW playerWW = game.getPlayerWW(player.getUniqueId()).orElse(null);
        if (playerWW == null || !playerWW.isState(StatePlayer.ALIVE)) {
            return;
        }

        if (manager.isDisabled(office)) {
            event.setCancelled(true);
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.burned_unusable");
            return;
        }

        if (!game.getConfig().isConfigActive(ConfigBase.URN_VOTE)) {
            return;
        }

        event.setCancelled(true);

        if (playerWW.getRole() instanceof MesquinWereWolf) {
            MesquinWereWolf mesquin = (MesquinWereWolf) playerWW.getRole();
            if (mesquin.getUse() < MesquinWereWolf.MAX_USES) {
                if (mesquin.rigUrn(office.getUrnLocation())) {
                    playerWW.sendMessageWithKey(Prefix.GREEN, "werewolf.roles.mesquin_werewolf.rig_perform");
                } else {
                    playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.mesquin_werewolf.rig_already");
                }
                return;
            }
        }

        if (!manager.getUrnVoteManager().isActive()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.configurations.buildings.urn_vote_not_active");
            return;
        }

        UrnChoiceGui.getInventory(office).open(player);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onUrnVote1Start(UrnVote1StartEvent event) {
        manager.revealBuildings();
    }
}
