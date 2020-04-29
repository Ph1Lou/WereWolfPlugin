package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.enumlg.ToolLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLovers extends Commands {


    public CommandLovers(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){
            return;
        }

     GameManager game = main.currentGame;

        TextLG text = game.text;
        Player player = (Player) sender;
        String playername = player.getName();

        if(!game.playerLG.containsKey(playername)) {
            player.sendMessage(text.getText(67));
            return;
        }

        PlayerLG plg = game.playerLG.get(playername);

        if(!game.isState(StateLG.LG)) {
            player.sendMessage(text.getText(68));
            return;
        }
        if (!plg.isState(State.LIVING)) {
            player.sendMessage(text.getText(97));
            return;
        }
        if (!game.config.configValues.get(ToolLG.DON_LOVERS)) {
            player.sendMessage(text.getText(259));
            return;
        }
        if (plg.getLovers().isEmpty()) {
            player.sendMessage(text.getText(27));
            return;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(String.format(text.getText(190), 1));
            return;
        }
        int heart;
        double life = player.getHealth();
        try {
            heart = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            player.sendMessage(text.getText(254));
            return;
        }
        if (life<=heart) {
            player.sendMessage(text.getText(255));
            return;
        }

        if(args.length==1){

            if (plg.getLovers().size() > heart) {
                player.sendMessage(text.getText(256));
                return;
            }

            player.setHealth(life-heart);
            int temp=heart;
            for (String p : plg.getLovers()) {
                if (Bukkit.getPlayer(p) != null) {
                    Player playerCouple = Bukkit.getPlayer(p);
                    int don = heart / plg.getLovers().size();
                    if (playerCouple.getMaxHealth() - playerCouple.getHealth() >= don) {
                        playerCouple.setHealth(playerCouple.getHealth() + don);
                        playerCouple.sendMessage(String.format(text.getText(260), don, playername));
                        playerCouple.playSound(playerCouple.getLocation(), Sound.PORTAL, 1, 20);
                        heart -= don;
                    }
                }
            }
            if(temp==heart){
                player.sendMessage("Vous ne pouvez pas envoyer autant de coeurs");
            }
            player.setHealth(player.getHealth()+heart);
        }
        else {
            if (args[1].equals(playername)) {
                player.sendMessage(text.getText(105));
                return;
            }
            if (!plg.getLovers().contains(args[1])) {
                player.sendMessage(text.getText(257));
                return;
            }
            player.setHealth(life-heart);

            if(Bukkit.getPlayer(args[1])==null){
                player.sendMessage(text.getText(106));
                return;
            }
            Player playerCouple = Bukkit.getPlayer(args[1]);

            if(playerCouple.getMaxHealth()-playerCouple.getHealth()>=heart){
                playerCouple.setHealth(playerCouple.getHealth() + heart);
                playerCouple.sendMessage(String.format(text.getText(260), heart, playername));
            }
            else{
                player.sendMessage("Vous ne pouvez pas envoyer autant de coeurs");
                player.setHealth(player.getHealth()+heart);
            }
        }
    }
}
