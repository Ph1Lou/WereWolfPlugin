package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.InvestigateEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CommandDetective implements Commands {


    private final Main main;

    public CommandDetective(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();

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

        if (args[0].equalsIgnoreCase(args[1])) {
            player.sendMessage(game.translate("werewolf.check.two_distinct_player"));
            return;
        }

        for(String p:args) {

            Player playerArg = Bukkit.getPlayer(p);

            if (playerArg == null) {
                player.sendMessage(game.translate("werewolf.check.offline_player"));
                return;
            }

            UUID uuid1 = playerArg.getUniqueId();


            if (!game.getPlayersWW().containsKey(uuid1) || game.getPlayersWW().get(uuid).isState(State.DEATH)) {
                player.sendMessage(game.translate("werewolf.check.player_not_found"));
                return;
            }

            if (uuid.equals(uuid1)) {
                player.sendMessage(game.translate("werewolf.check.not_yourself"));
                return;
            }

            if (((AffectedPlayers) detective).getAffectedPlayers().contains(uuid1)) {
                player.sendMessage(game.translate("werewolf.role.detective.already_inspect"));
                return;
            }
        }

        Player player1 = Bukkit.getPlayer(args[0]);
        Player player2 = Bukkit.getPlayer(args[1]);

        if (player1 == null || player2 == null) return;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();

        PlayerWW plg1 = game.getPlayersWW().get(uuid1);
        PlayerWW plg2 = game.getPlayersWW().get(uuid2);

        Camp isLG1 = plg1.getRole().getCamp();
        Camp isLG2 = plg2.getRole().getCamp();

        if (plg1.getRole() instanceof Display) {
            isLG1 = ((Display) plg1.getRole()).getDisplayCamp();
        }
        if (plg2.getRole() instanceof Display) {
            isLG2 = ((Display) plg2.getRole()).getDisplayCamp();
        }

        ((Power) detective).setPower(false);

        InvestigateEvent event = new InvestigateEvent(uuid, new ArrayList<>(Arrays.asList(uuid1, uuid2)), isLG1 == isLG2);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) detective).addAffectedPlayer(uuid1);
        ((AffectedPlayers) detective).addAffectedPlayer(uuid2);

        if (event.isSameCamp()) {
            player.sendMessage(game.translate("werewolf.role.detective.same_camp", player1.getName(), player2.getName()));
        } else
            player.sendMessage(game.translate("werewolf.role.detective.opposing_camp", player1.getName(), player2.getName()));
    }
}
