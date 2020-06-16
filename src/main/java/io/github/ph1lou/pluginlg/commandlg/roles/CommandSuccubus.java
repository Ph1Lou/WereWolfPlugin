package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.BeginCharmEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSuccubus implements Commands {


    private final MainLG main;

    public CommandSuccubus(MainLG main) {
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
        String playername = player.getName();

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole().isDisplay("werewolf.role.succubus.display"))) {
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.succubus.display")));
            return;
        }

        Roles succubus = plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (!((AffectedPlayers)succubus).getAffectedPlayers().isEmpty()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(args[0].toLowerCase().equals(playername.toLowerCase())) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if (!(((Power)succubus).hasPower())) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if (!game.playerLG.containsKey(argUUID) || !game.playerLG.get(argUUID).isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        Location location = player.getLocation();
        Location locationTarget = Bukkit.getPlayer(args[0]).getLocation();

        if (location.distance(locationTarget) > game.getConfig().getDistanceSuccubus()) {
            player.sendMessage(game.translate("werewolf.role.succubus.not_enough_near"));
            return;
        }

        BeginCharmEvent beginCharmEvent = new BeginCharmEvent(uuid,argUUID);

        Bukkit.getPluginManager().callEvent(beginCharmEvent);

        if(beginCharmEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) succubus).addAffectedPlayer(argUUID);
        player.sendMessage(game.translate("werewolf.role.succubus.charming_beginning", args[0]));
    }
}
