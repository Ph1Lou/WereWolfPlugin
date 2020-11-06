package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.TrackEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTrapper implements Commands {


    private final Main main;

    public CommandTrapper(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW plg = game.getPlayersWW().get(uuid);
        Roles trapper = plg.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!((Power) trapper).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID argUUID = playerArg.getUniqueId();

        if (uuid.equals(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if (!game.getPlayersWW().containsKey(argUUID) || !game.getPlayersWW().get(argUUID).isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (((AffectedPlayers) trapper).getAffectedPlayers().contains(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }

        TrackEvent trackEvent=new TrackEvent(uuid,argUUID);
        ((Power) trapper).setPower(false);
        Bukkit.getPluginManager().callEvent(trackEvent);

        if(trackEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) trapper).clearAffectedPlayer();
        ((AffectedPlayers) trapper).addAffectedPlayer(argUUID);

        playerArg.sendMessage(game.translate("werewolf.role.trapper.get_track"));
        player.sendMessage(game.translate("werewolf.role.trapper.tracking_perform", playerArg.getName()));
    }
}
