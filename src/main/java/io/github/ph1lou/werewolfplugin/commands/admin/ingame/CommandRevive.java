package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Sounds;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRevive implements Commands {


    private final Main main;

    public CommandRevive(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        Player player1 = Bukkit.getPlayer(args[0]);

        if (player1 == null) {
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = player1.getUniqueId();
        PlayerWW playerWW1 = game.getPlayerWW(uuid);

        if (playerWW1 == null) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.DEATH)) {
            player.sendMessage(game.translate("werewolf.commands.admin.revive.not_death"));
            return;
        }

        if (game.getModerationManager().getModerators().contains(uuid)) {
            Bukkit.dispatchCommand(player, "a moderator " + player1.getName());
        }

        Roles role = playerWW1.getRole();
        game.getConfig().getRoleCount().put(role.getKey(), game.getConfig().getRoleCount().get(role.getKey()) + 1);
        game.getScore().addPlayerSize();
        game.resurrection(playerWW1);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.commands.admin.revive.perform", player1.getName(), player.getName()));
            Sounds.AMBIENCE_THUNDER.play(p);
        }

    }
}
