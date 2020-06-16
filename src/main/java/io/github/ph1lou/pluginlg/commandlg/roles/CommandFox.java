package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.BeginSniffEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFox implements Commands {


    private final MainLG main;

    public CommandFox(MainLG main) {
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

        if (!(plg.getRole().isDisplay("werewolf.role.fox.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.fox.display")));
            return;
        }

        Roles fox = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)fox).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
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
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if(!game.playerLG.containsKey(argUUID) || !game.playerLG.get(argUUID).isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = Bukkit.getPlayer(args[0]).getLocation();

        if (location.distance(locationTarget) > game.getConfig().getDistanceFox()) {
            player.sendMessage(game.translate("werewolf.role.fox.not_enough_near"));
            return;
        } else if (((LimitedUse)fox).getUse() >= game.getConfig().getUseOfFlair()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        BeginSniffEvent beginSniffEvent=new BeginSniffEvent(uuid,argUUID);
        Bukkit.getPluginManager().callEvent(beginSniffEvent);

        if(beginSniffEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers)fox).clearAffectedPlayer();
        ((Power) fox).setPower(false);
        ((LimitedUse) fox).setUse(((LimitedUse) fox).getUse()+1);
        ((AffectedPlayers) fox).addAffectedPlayer(argUUID);
        ((Progress)fox).setProgress(0f);

        player.sendMessage(game.translate("werewolf.role.fox.smell_beginning", args[0]));
    }
}
