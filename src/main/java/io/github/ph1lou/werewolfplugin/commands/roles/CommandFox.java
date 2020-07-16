package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.BeginSniffEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFox implements Commands {


    private final Main main;

    public CommandFox(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;
        String playername = player.getName();
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

        if (!((Power) fox).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (args[0].toLowerCase().equals(playername.toLowerCase())) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();

        if (!game.getPlayersWW().containsKey(argUUID) || !game.getPlayersWW().get(argUUID).isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = playerArg.getLocation();

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
