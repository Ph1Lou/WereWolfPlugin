package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
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

        if (!sender.hasPermission("a.host.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
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

        if(game.getHosts().contains(uuid)){
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.remove", args[0]));
            game.getHosts().remove(host.getUniqueId());
        }
        else{
            game.getHosts().add(uuid);
            Bukkit.broadcastMessage(game.translate("werewolf.commands.admin.host.add", args[0]));
        }
        game.updateNameTag();
    }
}
