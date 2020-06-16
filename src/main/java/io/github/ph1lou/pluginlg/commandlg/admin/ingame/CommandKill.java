package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandKill implements Commands {


    private final MainLG main;

    public CommandKill(MainLG main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        GameManager game = main.currentGame;

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.kill.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        
        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        boolean find=false;

        UUID argUUID=null;

        for(UUID uuid:game.playerLG.keySet()){
            if (game.playerLG.get(uuid).getName().equals(args[0])) {
                find=true;
                argUUID=uuid;
            }
        }
        if(!find){
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!game.playerLG.get(argUUID).isState(State.ALIVE)) {
            sender.sendMessage(game.translate("werewolf.commands.kill.not_living"));
            return;
        }
        if (game.isState(StateLG.START)) {
            game.score.removePlayerSize();
            game.playerLG.remove(argUUID);
            sender.sendMessage(game.translate("werewolf.commands.kill.remove_role"));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            sender.sendMessage(game.translate("werewolf.commands.kill.on_line"));
            return;
        }
        if (!game.isState(StateLG.GAME)) {
            sender.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        game.death_manage.death(argUUID);
    }
}
