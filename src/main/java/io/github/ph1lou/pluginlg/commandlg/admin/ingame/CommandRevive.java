package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRevive extends Commands {


    public CommandRevive(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game=null;
        Player player =(Player) sender;

        for(GameManager gameManager:main.listGames.values()){
            if(gameManager.getWorld().equals(player.getWorld())){
                game=gameManager;
                break;
            }
        }

        if(game==null){
            return;
        }

        TextLG text = game.text;

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.revive.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }
        
        if (args.length != 1) {
            sender.sendMessage(text.getText(54));
            return;
        }

        if (!game.isState(StateLG.LG)) {
            sender.sendMessage(text.getText(68));
            return;
        }

        if (!game.playerLG.containsKey(args[0])) {
            sender.sendMessage(text.getText(132));
            return;
        }

        if (!game.playerLG.get(args[0]).isState(State.MORT)) {
            sender.sendMessage(text.getText(149));
            return;
        }

        RoleLG role = game.playerLG.get(args[0]).getRole();
        game.config.roleCount.put(role, game.config.roleCount.get(role) + 1);
        game.death_manage.resurrection(args[0]);
        game.score.addPlayerSize();
        if (role.equals(RoleLG.PETITE_FILLE) || role.equals(RoleLG.LOUP_PERFIDE)) {
            game.playerLG.get(args[0]).setPower(true);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.getWorld().equals(p.getWorld())) {
                p.sendMessage(String.format(text.getText(154), args[0]));
                p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 20);
            }
        }

    }
}
