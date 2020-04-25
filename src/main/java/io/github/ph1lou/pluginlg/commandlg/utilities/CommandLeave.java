package io.github.ph1lou.pluginlg.commandlg.utilities;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave extends Commands {


    public CommandLeave(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
            return;
        }

        GameManager game=null;
        Player player =(Player) sender;
        String playerName = player.getName();

        for(GameManager gameManager:main.listGames.values()){
            if(gameManager.getWorld().equals(player.getWorld())){
                game=gameManager;
                break;
            }
        }

        if(game==null){
            return;
        }

        if(!game.isState(StateLG.FIN) && !game.isState(StateLG.LOBBY) && game.playerLG.containsKey(player.getName()) && game.playerLG.get(player.getName()).isState(State.LIVING)){
            player.sendMessage(game.text.getText(280));
        }
        else{
            FastBoard fastboard = game.boards.remove(player.getUniqueId());
            if (fastboard != null) {
                fastboard.delete();
            }
            fastboard = new FastBoard(player);
            fastboard.updateTitle(main.defaultLanguage.getText(125));
            main.boards.put(player.getUniqueId(), fastboard);
            Title.sendTabTitle(player, main.defaultLanguage.getText(125), main.defaultLanguage.getText(184));

            if(game.isState(StateLG.LOBBY) && game.playerLG.containsKey(playerName)) {
                game.score.removePlayerSize();
                game.board.getTeam(playerName).unregister();
                game.playerLG.remove(playerName);
                game.checkQueue();
                for(Player p:Bukkit.getOnlinePlayers()){
                    if(game.playerLG.containsKey(p.getName())){
                        p.sendMessage(String.format(game.text.getText(195),game.score.getPlayerSize(),game.score.getRole(),player.getName()));
                    }
                }
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(game.text.getText(281));
            game.clearPlayer(player);
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

    }
}
