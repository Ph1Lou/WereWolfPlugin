package io.github.ph1lou.pluginlg.commandlg.utilities;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStuff extends Commands {


    public CommandStuff(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
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

        sender.sendMessage(text.getText(211 + game.config.getLimitKnockBack()));
        sender.sendMessage(text.getText(214 + game.config.getLimitPunch()));
        sender.sendMessage(text.getText(217));
        sender.sendMessage(String.format(text.getText(206), game.config.getLimitProtectionIron()));
        sender.sendMessage(String.format(text.getText(207), game.config.getLimitProtectionDiamond()));
        sender.sendMessage(String.format(text.getText(208), game.config.getLimitPowerBow()));
        sender.sendMessage(String.format(text.getText(209), game.config.getLimitSharpnessIron()));
        sender.sendMessage(String.format(text.getText(210), game.config.getLimitSharpnessDiamond()));
        sender.sendMessage(text.getText(218));
        sender.sendMessage(text.getText(219));
        sender.sendMessage(String.format(text.getText(220), String.format(text.getText(208), game.config.getLimitPowerBow() + 1), String.format(text.getText(210), game.config.getLimitSharpnessDiamond() + 1)));
        sender.sendMessage(String.format(text.getText(221), String.format(text.getText(208), game.config.getLimitPowerBow() + 1)));
    }
}
