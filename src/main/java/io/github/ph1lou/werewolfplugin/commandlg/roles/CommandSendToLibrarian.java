package io.github.ph1lou.werewolfplugin.commandlg.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfapi.rolesattributs.Storage;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSendToLibrarian implements Commands {

    private final Main main;

    public CommandSendToLibrarian(Main main) {
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

        if(!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }


        if (args.length==0) {
            player.sendMessage(game.translate("werewolf.check.parameters",1));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        boolean find =false;

        StringBuilder sb2 = new StringBuilder();

        for (String w : args) {
            sb2.append(w).append(" ");
        }

        for(PlayerWW playerWW:game.getPlayersWW().values()){

            if(playerWW.isState(State.ALIVE)){
                if(playerWW.getRole().isDisplay("werewolf.role.librarian.display")){

                    Roles roles = playerWW.getRole();
                    if(((AffectedPlayers)roles).getAffectedPlayers().contains(uuid)){
                        ((Storage)roles).getStorage().add(sb2.toString());
                        ((AffectedPlayers) roles).removeAffectedPlayer(uuid);
                        player.sendMessage(game.translate("werewolf.role.librarian.contribute"));
                        find=true;
                        if(Bukkit.getPlayer(roles.getPlayerUUID())!=null){
                            Bukkit.getPlayer(roles.getPlayerUUID()).sendMessage(game.translate("werewolf.role.librarian.contribution",player.getName(),sb2.toString()));
                        }
                    }
                }
            }
        }

        if(!find){
            player.sendMessage(game.translate("werewolf.role.librarian.prohibit"));
        }


    }
}
