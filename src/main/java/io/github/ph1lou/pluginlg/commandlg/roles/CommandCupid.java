package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Cupid;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCupid extends Commands {


    public CommandCupid(MainLG main) {
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

        if (!(plg.getRole() instanceof Cupid)){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.cupid.display")));
            return;
        }

        Cupid cupid = (Cupid) plg.getRole();

        if (args.length!=2) {
            player.sendMessage(game.translate("werewolf.check.parameters",2));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!cupid.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(args[0].equals(args[1])) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        for(String p:args) {
            if(p.equals(playername)) {
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

        for(String p:args) {
            cupid.addAffectedPlayer(Bukkit.getPlayer(p).getUniqueId());
        }
        cupid.setPower(false);

        sender.sendMessage(game.translate("werewolf.role.cupid.designation_perform",args[0],args[1]));
    }
}
