package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.InvisibleState;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.villageroles.LittleGirl;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.MischievousWereWolf;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRevive extends Commands {


    public CommandRevive(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

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

        if (!game.playerLG.containsKey(uuid)) {
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        PlayerLG plg = game.playerLG.get(uuid);

        if (!plg.isState(State.DEATH)) {
            sender.sendMessage(game.translate("werewolf.commands.admin.revive.not_death"));
            return;
        }

        RolesImpl role = plg.getRole();
        game.config.getRoleCount().put(role.getRoleEnum(), game.config.getRoleCount().get(role.getRoleEnum()) + 1);
        game.score.addPlayerSize();
        game.death_manage.resurrection(uuid);

        if (role instanceof LittleGirl || role instanceof MischievousWereWolf) {
            ((InvisibleState) role).setInvisible(false);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(game.translate("werewolf.commands.admin.revive.perform", args[0]));
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 20);
        }

    }
}
