package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandJoin extends Commands {


    public CommandJoin(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) return;

        if(args.length==0) return;

        try {

            if (!main.listGames.containsKey(UUID.fromString(args[0]))) return;

            GameManager game = null;

            Player player = (Player) sender;
            String playerName = player.getName();

            for (GameManager gameManager : main.listGames.values()) {
                if (gameManager.getWorld().equals(player.getWorld()) && gameManager.playerLG.containsKey(playerName)) {
                    game = gameManager;
                    break;
                }
            }

            if (game != null) {
                player.sendMessage(game.text.getText(275));
                return;
            }

            game = main.listGames.get(UUID.fromString(args[0]));

            if (playerName.equals(args[0]) || game.getModerators().contains(((Player) sender).getUniqueId()) || game.getHosts().contains(((Player) sender).getUniqueId())) {
                game.join(player);
                return;
            }

            if (game.playerLG.containsKey(playerName) && game.playerLG.get(playerName).isState(State.MORT)) {
                if (game.getSpectatorMode() == 0) {
                    player.sendMessage(game.text.getText(276));
                    return;
                }
            }

            if (!game.isState(StateLG.LOBBY) && game.getSpectatorMode() < 2) {
                player.sendMessage(game.text.getText(277));
                return;
            }

            if (game.isWhiteList() && !game.getWhiteListedPlayers().contains(playerName)) {
                player.sendMessage(game.text.getText(278));
                return;
            }

            TextLG text = game.text;

            if (game.playerLG.size() >= game.getPlayerMax()) {
                if (!game.getQueue().contains(player.getUniqueId())) {
                    player.sendMessage(text.getText(66));
                    game.addPlayerInQueue(player.getUniqueId());
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getWorld().equals(game.getWorld())) {
                            p.sendMessage(String.format(game.text.getText(279), playerName));
                        }

                    }
                }
                return;
            }

            game.join(player);
        }catch(Exception ignored){
        }
    }
}
