package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRevive implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        Player player1 = Bukkit.getPlayer(args[0]);

        if (player1 == null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.offline_player"));
            return;
        }

        UUID uuid = player1.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(uuid).orElse(null);

        if (playerWW1 == null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.not_in_game_player"));
            return;
        }

        if (!playerWW1.isState(StatePlayer.DEATH)) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.revive.not_death"));
            return;
        }

        if (game.getModerationManager().getModerators().contains(uuid)) {
            Bukkit.dispatchCommand(player, "a moderator " + player1.getName());
        }

        IRole role = playerWW1.getRole();
        game.getConfig().addOneRole(role.getKey());
        ((GameManager) game).setPlayerSize(game.getPlayersCount()+1);
        game.resurrection(playerWW1);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate(Prefix.YELLOW.getKey() , "werewolf.commands.admin.revive.perform",
                    Formatter.player(player1.getName()),
                    Formatter.format("&admin&",player.getName())));
            Sound.AMBIENCE_THUNDER.play(p);
        }

    }
}
