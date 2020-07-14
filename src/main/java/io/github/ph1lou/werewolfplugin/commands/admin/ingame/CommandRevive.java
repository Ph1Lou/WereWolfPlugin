package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRevive implements Commands {


    private final Main main;

    public CommandRevive(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.revive.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        
        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if (!game.isState(StateLG.GAME)) {
            sender.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID uuid = Bukkit.getPlayer(args[0]).getUniqueId();

        if (!game.getPlayersWW().containsKey(uuid)) {
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);

        if (!plg.isState(State.DEATH)) {
            sender.sendMessage(game.translate("werewolf.commands.admin.revive.not_death"));
            return;
        }

        Roles role = plg.getRole();
        game.getConfig().getRoleCount().put(role.getDisplay(), game.getConfig().getRoleCount().get(role.getDisplay()) + 1);
        game.getScore().addPlayerSize();
        game.resurrection(uuid);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.commands.admin.revive.perform", args[0]));
            p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 20);
        }

    }
}
