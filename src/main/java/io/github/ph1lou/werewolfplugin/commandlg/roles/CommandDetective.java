package io.github.ph1lou.werewolfplugin.commandlg.roles;

import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.InvestigateEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Display;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class CommandDetective implements Commands {


    private final Main main;

    public CommandDetective(Main main) {
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

        if(!game.getPlayersWW().containsKey(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game"));
            return;
        }

        PlayerWW plg = game.getPlayersWW().get(uuid);


        if (!game.isState(StateLG.GAME)) {
            player.sendMessage(game.translate("werewolf.check.game_not_in_progress"));
            return;
        }

        if (!(plg.getRole().isDisplay("werewolf.role.detective.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.detective.display")));
            return;
        }

        Roles detective = plg.getRole();

        if (args.length!=2) {
            player.sendMessage(game.translate("werewolf.check.parameters",2));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)detective).hasPower()) {
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

            if(!game.getPlayersWW().containsKey(uuid1) || game.getPlayersWW().get(uuid).isState(State.DEATH)) {
                player.sendMessage(game.translate("werewolf.check.player_not_found"));
                return;
            }

            if(uuid.equals(uuid1)) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }

            if(((AffectedPlayers)detective).getAffectedPlayers().contains(uuid1)){
                player.sendMessage(game.translate("werewolf.role.detective.already_inspect"));
                return;
            }
        }

        UUID uuid1=Bukkit.getPlayer(args[0]).getUniqueId();
        UUID uuid2=Bukkit.getPlayer(args[1]).getUniqueId();

        PlayerWW plg1 = game.getPlayersWW().get(uuid1);
        PlayerWW plg2 = game.getPlayersWW().get(uuid2);

        Camp isLG1=plg2.getRole().getCamp();
        Camp isLG2=plg1.getRole().getCamp();

        if(plg1.getRole() instanceof Display) {
            isLG2= ((Display) plg1.getRole()).getDisplayCamp();
        }
        if(plg2.getRole() instanceof Display) {
            isLG1= ((Display) plg2.getRole()).getDisplayCamp();
        }

        InvestigateEvent event=new InvestigateEvent(uuid, Arrays.asList(uuid1,uuid2),isLG1==isLG2);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((Power) detective).setPower(false);
        ((AffectedPlayers) detective).addAffectedPlayer(uuid1);
        ((AffectedPlayers) detective).addAffectedPlayer(uuid2);

        if(isLG1!=isLG2) {
            player.sendMessage(game.translate("werewolf.role.detective.opposing_camp",args[0],args[1]));
        }
        else player.sendMessage(game.translate("werewolf.role.detective.same_camp",args[0],args[1]));
    }
}
