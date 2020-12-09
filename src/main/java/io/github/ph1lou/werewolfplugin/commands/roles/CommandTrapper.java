package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
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
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles trapper = playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (uuid.equals(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (((AffectedPlayers) trapper).getAffectedPlayers().contains(playerWW1)) {
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }

        TrackEvent trackEvent = new TrackEvent(playerWW, playerWW1);
        ((Power) trapper).setPower(false);
        Bukkit.getPluginManager().callEvent(trackEvent);

        if(trackEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) trapper).clearAffectedPlayer();
        ((AffectedPlayers) trapper).addAffectedPlayer(playerWW1);

        playerArg.sendMessage(game.translate("werewolf.role.trapper.get_track"));
        player.sendMessage(game.translate("werewolf.role.trapper.tracking_perform", playerArg.getName()));
    }
}
