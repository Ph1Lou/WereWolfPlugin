package io.github.ph1lou.werewolfplugin.commands.utilities;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StateGame;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRole implements Commands {


    private final Main main;

    public CommandRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();

        if (!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateGame.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        player.sendMessage(plg.getRole().getDescription());

        if(plg.getRole().isKey("werewolf.role.sister.display")) {
            StringBuilder list =new StringBuilder();
            for(UUID uuid2:game.getPlayersWW().keySet()) {
                PlayerWW pls =game.getPlayersWW().get(uuid2);
                if(pls.isState(StatePlayer.ALIVE) && pls.getRole().isKey("werewolf.role.sister.display")) {
                    list.append(pls.getName()).append(" ");
                }
            }
            player.sendMessage(game.translate("werewolf.role.sister.sisters_list",list.toString()));
        }
        else if(plg.getRole().isKey("werewolf.role.siamese_twin.display")) {
            StringBuilder list =new StringBuilder();
            for(UUID uuid3:game.getPlayersWW().keySet()) {
                PlayerWW plb =game.getPlayersWW().get(uuid3);
                if(plb.isState(StatePlayer.ALIVE) && plb.getRole().isKey("werewolf.role.siamese_twin.display")) {
                    list.append(plb.getName()).append(" ");
                }
            }
            player.sendMessage(game.translate("werewolf.role.siamese_twin.siamese_twin_list",list.toString()));
        }
    }
}
