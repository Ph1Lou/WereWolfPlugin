package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.ConfigsBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.events.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWitch implements Commands {


    private final Main main;

    public CommandWitch(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        Roles witch = playerWW.getRole();

        if (args.length != 1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!((Power) witch).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (Bukkit.getPlayer(UUID.fromString(args[0])) == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);
        PlayerWW playerWW1 = game.getPlayerWW(argUUID);

        if (playerWW1 == null) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if (!game.getConfig().getConfigValues().get(ConfigsBase.AUTO_REZ_WITCH.getKey()) && argUUID.equals(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.JUDGEMENT)) {
            player.sendMessage(game.translate("werewolf.check.not_in_judgement"));
            return;
        }

        if (game.getScore().getTimer() - playerWW1.getDeathTime() < 7) {
            return;
        }

        ((Power) witch).setPower(false);
        WitchResurrectionEvent witchResurrectionEvent = new WitchResurrectionEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

        if (witchResurrectionEvent.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) witch).addAffectedPlayer(playerWW1);
        game.resurrection(playerWW1);
        player.sendMessage(game.translate("werewolf.role.witch.resuscitation_perform",
                playerWW1.getName()));
    }
}
