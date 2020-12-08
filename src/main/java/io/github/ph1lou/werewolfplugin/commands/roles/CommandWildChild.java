package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.ModelEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWildChild implements Commands {


    private final Main main;

    public CommandWildChild(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles wildChild = playerWW.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!((Power) wildChild).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (argUUID.equals(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        ((AffectedPlayers) wildChild).addAffectedPlayer(playerWW1);
        ((Power) wildChild).setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(playerWW, playerWW1));
        player.sendMessage(game.translate("werewolf.role.wild_child.reveal_model", playerArg.getName()));
    }
}
