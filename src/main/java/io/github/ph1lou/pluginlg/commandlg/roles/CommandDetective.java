package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Detective;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.FalsifierWereWolf;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandDetective extends Commands {


    public CommandDetective(MainLG main) {
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

        if (!(plg.getRole() instanceof Detective)){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.detective.display")));
            return;
        }

        Detective detective = (Detective) plg.getRole();

        if (args.length!=2) {
            player.sendMessage(game.translate("werewolf.check.parameters",2));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!detective.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(args[0].equals(args[1])) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        for(String p:args) {
            if(p.equals(plg.getName())) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }
        }

        for(String p:args) {
            if(Bukkit.getPlayer(p)==null){
                UUID playerUUID = Bukkit.getPlayer(p).getUniqueId();
                if(!game.playerLG.containsKey(playerUUID) || game.playerLG.get(playerUUID).isState(State.DEATH)){
                    player.sendMessage(game.translate("werewolf.check.player_not_found"));
                    return;
                }
            }
        }

        UUID uuid1 = Bukkit.getPlayer(args[0]).getUniqueId();
        UUID uuid2 = Bukkit.getPlayer(args[1]).getUniqueId();

        if(detective.getAffectedPlayers().contains(uuid1) || detective.getAffectedPlayers().contains(uuid2)){
            player.sendMessage(game.translate("werewolf.role.detective.already_inspect"));
            return;
        }

        PlayerLG plg1 = game.playerLG.get(uuid1);
        PlayerLG plg2 = game.playerLG.get(uuid2);

        detective.addAffectedPlayer(uuid1);
        detective.addAffectedPlayer(uuid2);
        detective.setPower(false);
        Camp isLG1=plg2.getRole().getCamp();
        Camp isLG2=plg1.getRole().getCamp();

        if(plg1.getRole() instanceof FalsifierWereWolf) {
            isLG2= ((FalsifierWereWolf) plg1.getRole()).getPosterCamp();
        }
        if(plg2.getRole() instanceof FalsifierWereWolf) {
            isLG1= ((FalsifierWereWolf) plg2.getRole()).getPosterCamp();
        }

        if(isLG1!=isLG2) {
            player.sendMessage(game.translate("werewolf.role.detective.opposing_camp",args[0],args[1]));
        }
        else player.sendMessage(game.translate("werewolf.role.detective.same_camp",args[0],args[1]));
    }
}
