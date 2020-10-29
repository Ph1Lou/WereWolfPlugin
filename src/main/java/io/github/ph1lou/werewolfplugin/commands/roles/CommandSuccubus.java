package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.events.BeginCharmEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSuccubus implements Commands {


    private final Main main;

    public CommandSuccubus(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {


        GameManager game = main.getCurrentGame();
        UUID uuid = player.getUniqueId();
        String playername = player.getName();
        PlayerWW plg = game.getPlayersWW().get(uuid);
        Roles succubus = plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!((AffectedPlayers)succubus).getAffectedPlayers().isEmpty()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (args[0].toLowerCase().equals(playername.toLowerCase())) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if (!(((Power) succubus).hasPower())) {
            player.sendMessage(game.translate("werewolf.check.power"));
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
        player.sendMessage(game.translate("werewolf.role.succubus.charming_beginning", playerArg.getName()));
    }
}
