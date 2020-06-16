package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.LibrarianRequestEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.LimitedUse;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandLibrarian implements Commands {


    private final MainLG main;

    public CommandLibrarian(MainLG main) {
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
        String playername = player.getName();
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

        if (!(plg.getRole().isDisplay("werewolf.role.librarian.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.fox.display")));
            return;
        }

        Roles librarian = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(args[0].toLowerCase().equals(playername.toLowerCase())) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        Player selectionPlayer =Bukkit.getPlayer(args[0]);
        UUID argUUID = selectionPlayer.getUniqueId();

        if(!game.playerLG.containsKey(argUUID) || !game.playerLG.get(argUUID).isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if(((AffectedPlayers)librarian).getAffectedPlayers().contains(argUUID)){
            player.sendMessage(game.translate("werewolf.role.librarian.waiting"));
            return;
        }


       if (((LimitedUse)librarian).getUse() >= 3) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        LibrarianRequestEvent librarianRequestEvent=new LibrarianRequestEvent(uuid,argUUID);
        Bukkit.getPluginManager().callEvent(librarianRequestEvent);

        if(librarianRequestEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((LimitedUse) librarian).setUse(((LimitedUse) librarian).getUse()+1);
        ((AffectedPlayers) librarian).addAffectedPlayer(argUUID);

        selectionPlayer.sendMessage(game.translate("werewolf.role.librarian.message"));
        player.sendMessage(game.translate("werewolf.role.librarian.perform",selectionPlayer.getName()));
    }
}
