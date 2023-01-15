package fr.ph1lou.werewolfplugin.commands.elections;

import fr.ph1lou.werewolfapi.annotations.PlayerCommand;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.ElectionState;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfplugin.configs.Elections;
import fr.ph1lou.werewolfplugin.guis.elections.ElectionGUI;
import org.bukkit.entity.Player;

@PlayerCommand(key = "werewolf.elections.election.command", descriptionKey = "",
        statesGame = StateGame.GAME,
        statesPlayer = StatePlayer.ALIVE)
public class ElectionCommand implements ICommand {

    @Override
    public void execute(WereWolfAPI wereWolfAPI, Player player, String[] args) {

        wereWolfAPI.getListenersManager().getConfiguration(ConfigBase.ELECTIONS)
                .ifPresent(electionManager1 -> {

                    Elections electionManager = (Elections) electionManager1;
                    IPlayerWW playerWW = wereWolfAPI.getPlayerWW(player.getUniqueId())
                            .orElse(null);

                    if (playerWW == null) return;

                    if (!wereWolfAPI.getConfig().isConfigActive(ConfigBase.ELECTIONS)) {
                        playerWW.sendMessageWithKey("werewolf.elections.election.disable");
                        return;
                    }

                    if (electionManager.isState(ElectionState.NOT_BEGIN)) {
                        playerWW.sendMessageWithKey("werewolf.elections.election.not_begin");
                        return;
                    }

                    if (electionManager.isState(ElectionState.MESSAGE)) {

                        if (args.length == 0) {
                            playerWW.sendMessageWithKey("werewolf.elections.election.empty");
                            return;
                        }
                        StringBuilder message = new StringBuilder();
                        for (String part : args) {
                            message.append(part).append(" ");
                        }
                        if (electionManager.getPlayerMessage(playerWW).isPresent()) {
                            playerWW.sendMessageWithKey("werewolf.elections.election.change");
                        } else {
                            playerWW.sendMessageWithKey("werewolf.elections.election.register");
                        }

                        electionManager.addMessage(playerWW, message.toString());
                        return;
                    }

                    if (electionManager.isState(ElectionState.ELECTION)) {
                        ElectionGUI.getInventory(player).open(player);
                        return;
                    }

                    if (electionManager.isState(ElectionState.FINISH)) {
                        playerWW.sendMessageWithKey("werewolf.elections.election.finish");
                    }
                });

    }
}
