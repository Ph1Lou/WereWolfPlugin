package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.ModelEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWildChild implements Commands {


    private final MainLG main;

    public CommandWildChild(MainLG main) {
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
        String playername = player.getName();

        if(!game.playerLG.containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.playerLG.get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole().isDisplay("werewolf.role.wild_child.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.wild_child.display")));
            return;
        }

        Roles wildChild = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)wildChild).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if(!game.playerLG.containsKey(argUUID) || !game.playerLG.get(argUUID).isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        if(args[0].toLowerCase().equals(playername.toLowerCase())) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        ((AffectedPlayers)wildChild).addAffectedPlayer(argUUID);
        ((Power) wildChild).setPower(false);
        Bukkit.getPluginManager().callEvent(new ModelEvent(uuid,argUUID));
        player.sendMessage(game.translate("werewolf.role.wild_child.reveal_model", args[0]));
    }
}
