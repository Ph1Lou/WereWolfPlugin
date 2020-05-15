package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
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

public class CommandLovers extends Commands {


    public CommandLovers(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.currentGame;

        if (!(sender instanceof Player)) {
            sender.sendMessage(game.translate("werewolf.check.console"));
            return;
        }

        Player player = (Player) sender;
        String playername = player.getName();
        UUID uuid = player.getUniqueId();

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerLG plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!plg.isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if (plg.getLovers().isEmpty() && (plg.getAmnesiacLoverUUID()==null || !plg.getRevealAmnesiacLover())) {
            player.sendMessage(game.translate("werewolf.role.lover.not_in_pairs"));
            return;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(game.translate("werewolf.check.parameters",1));
            return;
        }
        int heart;
        double life = player.getHealth();
        try {
            heart = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            player.sendMessage(game.translate("werewolf.check.number_required"));
            return;
        }
        if (life<=heart) {
            player.sendMessage(game.translate("werewolf.role.lover.not_enough_heart"));
            return;
        }

        if(args.length==1){

            if (plg.getLovers().size() > heart) {
                player.sendMessage(game.translate("werewolf.role.lover.not_enough_heart_send"));
                return;
            }

            player.setHealth(life-heart);
            int temp=heart;
            for (UUID uuid1 : plg.getLovers()) {
                if (Bukkit.getPlayer(uuid1) != null) {
                    Player playerCouple = Bukkit.getPlayer(uuid1);
                    int don = heart / plg.getLovers().size();
                    if (playerCouple.getMaxHealth() - playerCouple.getHealth() >= don) {
                        playerCouple.setHealth(playerCouple.getHealth() + don);
                        playerCouple.sendMessage(game.translate("werewolf.role.lover.received", don, playername));
                        playerCouple.playSound(playerCouple.getLocation(), Sound.PORTAL, 1, 20);
                        heart -= don;
                    }
                }
            }
            if (plg.getAmnesiacLoverUUID()!=null && Bukkit.getPlayer(plg.getAmnesiacLoverUUID()) != null) {
                Player playerCouple = Bukkit.getPlayer(plg.getAmnesiacLoverUUID());
                int don = heart ;
                if (playerCouple.getMaxHealth() - playerCouple.getHealth() >= don) {
                    playerCouple.setHealth(playerCouple.getHealth() + don);
                    playerCouple.sendMessage(game.translate("werewolf.role.lover.received", don, playername));
                    playerCouple.playSound(playerCouple.getLocation(), Sound.PORTAL, 1, 20);
                    heart -= don;
                }
            }
            if(temp==heart){
                player.sendMessage("Vous ne pouvez pas envoyer autant de coeurs");
            }
            player.setHealth(player.getHealth()+heart);
        }
        else {
            if (args[1].equals(playername)) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }
            if(Bukkit.getPlayer(args[1])==null){
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

            if (!plg.getLovers().contains(argUUID)) {
                player.sendMessage(game.translate("werewolf.role.lover.not_lover"));
                return;
            }
            player.setHealth(life-heart);

            Player playerCouple = Bukkit.getPlayer(args[1]);

            if(playerCouple.getMaxHealth()-playerCouple.getHealth()>=heart){
                playerCouple.setHealth(playerCouple.getHealth() + heart);
                playerCouple.sendMessage(game.translate("werewolf.role.lover.received", heart, playername));
            }
            else{
                player.sendMessage("Vous ne pouvez pas envoyer autant de coeurs");
                player.setHealth(player.getHealth()+heart);
            }
        }
    }
}
