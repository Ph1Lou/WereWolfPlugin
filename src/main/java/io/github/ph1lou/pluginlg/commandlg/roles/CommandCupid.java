package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.CupidLoversEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCupid implements Commands {


    private final MainLG main;

    public CommandCupid(MainLG main) {
        this.main = main;
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

        PlayerWW plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole().isDisplay("werewolf.role.cupid.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.cupid.display")));
            return;
        }

        Roles cupid = plg.getRole();

        if (args.length!=2) {
            player.sendMessage(game.translate("werewolf.check.parameters",2));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)cupid).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(args[0].toLowerCase().equals(args[1].toLowerCase())) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        for(String p:args) {

            if(Bukkit.getPlayer(p)==null){
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }
            UUID uuid1=Bukkit.getPlayer(p).getUniqueId();

            if(!game.playerLG.containsKey(uuid1) || game.playerLG.get(uuid).isState(State.DEATH)) {
                player.sendMessage(game.translate("werewolf.check.player_not_found"));
                return;
            }

            if(uuid.equals(uuid1)) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }
        }


        for(String p:args) {
            ((AffectedPlayers)cupid).addAffectedPlayer(Bukkit.getPlayer(p).getUniqueId());
        }
        ((Power) cupid).setPower(false);
        Bukkit.getPluginManager().callEvent(new CupidLoversEvent(uuid, ((AffectedPlayers) cupid).getAffectedPlayers()));
        sender.sendMessage(game.translate("werewolf.role.cupid.designation_perform",args[0],args[1]));
    }
}
