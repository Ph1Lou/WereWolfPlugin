package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.trouble_maker.TroubleMakerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTroubleMaker implements ICommands {


    private final Main main;

    public CommandTroubleMaker(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole troublemaker = playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        TroubleMakerEvent troubleMakerEvent = new TroubleMakerEvent(playerWW, playerWW1);
        ((IPower) troublemaker).setPower(false);
        Bukkit.getPluginManager().callEvent(troubleMakerEvent);

        if (troubleMakerEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((IAffectedPlayers) troublemaker).addAffectedPlayer(playerWW1);

        playerWW1.sendMessageWithKey("werewolf.role.troublemaker.get_switch");
        game.getMapManager().transportation(playerWW1, Math.random() * 2 * Math.PI);
        player.sendMessage(game.translate("werewolf.role.troublemaker.troublemaker_perform", playerArg.getName()));
    }
}
