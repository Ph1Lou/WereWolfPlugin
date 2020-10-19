package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.HostEvent;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.game.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHost implements Commands {


    private final Main main;

    public CommandHost(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();
        ModerationManager moderationManager = game.getModerationManager();

        if (!sender.hasPermission("a.host.use") && !moderationManager.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        Player host = Bukkit.getPlayer(args[0]);
        if (host == null) return;
        UUID uuid = host.getUniqueId();

        if (moderationManager.getHosts().contains(uuid)) {
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.remove", args[0]));
            moderationManager.getHosts().remove(uuid);
            if (moderationManager.getModerators().contains(uuid)) {
                if (game.getPlayersWW().containsKey(uuid)) {
                    game.getScore().removePlayerSize();
                    game.getPlayersWW().remove(uuid);
                }
            }
        } else {
            if (game.isState(StateLG.LOBBY) && !game.getPlayersWW().containsKey(uuid)) {
                game.finalJoin(host);
            }
            moderationManager.getHosts().add(uuid);
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.add", args[0]));
        }
        Bukkit.getPluginManager().callEvent(new HostEvent(uuid, moderationManager.getHosts().contains(uuid)));
    }
}
