package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandKill implements Commands {


    private final Main main;

    public CommandKill(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();

        boolean find = false;

        UUID argUUID=null;

        for(UUID uuid:game.getPlayersWW().keySet()){
            if (game.getPlayersWW().get(uuid).getName().equals(args[0])) {
                find=true;
                argUUID=uuid;
            }
        }
        if(!find){
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!game.getPlayersWW().get(argUUID).isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.commands.kill.not_living"));
            return;
        }
        if (game.isState(StateGame.START)) {
            game.getScore().removePlayerSize();
            game.getPlayersWW().remove(argUUID);
            player.sendMessage(game.translate("werewolf.commands.kill.remove_role"));
            return;
        }
        if (Bukkit.getPlayer(args[0]) != null) {
            player.sendMessage(game.translate("werewolf.commands.kill.on_line"));
            return;
        }

        game.death(argUUID);
    }
}
