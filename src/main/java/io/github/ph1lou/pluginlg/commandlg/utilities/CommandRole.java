package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRole implements Commands {


    private final MainLG main;

    public CommandRole(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        player.sendMessage(plg.getRole().getDescription());

        if(plg.getRole().isDisplay("werewolf.role.sister.display")) {
            StringBuilder list =new StringBuilder();
            for(UUID uuid2:game.playerLG.keySet()) {
                PlayerWW pls =game.playerLG.get(uuid2);
                if(pls.isState(State.ALIVE) && pls.getRole().isDisplay("werewolf.role.sister.display")) {
                    list.append(pls.getName()).append(" ");
                }
            }
            player.sendMessage(game.translate("werewolf.role.sister.sisters_list",list.toString()));
        }
        else if(plg.getRole().isDisplay("werewolf.role.siamese_twin.display")) {
            StringBuilder list =new StringBuilder();
            for(UUID uuid3:game.playerLG.keySet()) {
                PlayerWW plb =game.playerLG.get(uuid3);
                if(plb.isState(State.ALIVE) && plb.getRole().isDisplay("werewolf.role.siamese_twin.display")) {
                    list.append(plb.getName()).append(" ");
                }
            }
            player.sendMessage(game.translate("werewolf.role.siamese_twin.siamese_twin_list",list.toString()));
        }
    }
}
