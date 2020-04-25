package io.github.ph1lou.pluginlg.commandlg.admin;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStop extends Commands {


    public CommandStop(MainLG main) {
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

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.stop.use") && !game.getHosts().contains(player.getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        if(game.isState(StateLG.FIN)){
            return;
        }

        for(Player p: Bukkit.getOnlinePlayers()){
            if(p.getWorld().equals(game.getWorld())){
                p.sendMessage(game.text.getText(291));
                TextComponent msg = new TextComponent(game.text.getText(292));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg leave"));
                p.spigot().sendMessage(msg);
            }
        }
        game.setState(StateLG.FIN);
    }
}
