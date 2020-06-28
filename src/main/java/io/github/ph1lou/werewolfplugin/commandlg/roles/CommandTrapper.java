package io.github.ph1lou.werewolfplugin.commandlg.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.TrackEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTrapper implements Commands {


    private final Main main;

    public CommandTrapper(Main main) {
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

        if(!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole().isDisplay("werewolf.role.trapper.display"))) {
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.trapper.display")));
            return;
        }

        Roles trapper = plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (!((Power)trapper).hasPower()) {
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

        if (!game.getPlayersWW().containsKey(argUUID) || !game.getPlayersWW().get(argUUID).isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (((AffectedPlayers)trapper).getAffectedPlayers().contains(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }

        TrackEvent trackEvent=new TrackEvent(uuid,argUUID);

        Bukkit.getPluginManager().callEvent(trackEvent);

        if(trackEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) trapper).clearAffectedPlayer();
        ((AffectedPlayers) trapper).addAffectedPlayer(argUUID);
        ((Power) trapper).setPower(false);

        Bukkit.getPlayer(args[0]).sendMessage(game.translate("werewolf.role.trapper.get_track"));
        player.sendMessage(game.translate("werewolf.role.trapper.tracking_perform", args[0]));
    }
}
