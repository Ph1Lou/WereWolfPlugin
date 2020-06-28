package io.github.ph1lou.werewolfplugin.commandlg.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.Camp;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.SeerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Display;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSeer implements Commands {

    private final Main main;

    public CommandSeer(Main main) {
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

        if (!plg.getRole().isDisplay("werewolf.role.seer.display") && !plg.getRole().isDisplay("werewolf.role.chatty_seer.display")){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.seer.display")));
            return;
        }

        Roles seer = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)seer).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if(!game.getPlayersWW().containsKey(argUUID) || !game.getPlayersWW().get(argUUID).isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        double life =player.getMaxHealth();

        if (life<7) {
            player.sendMessage(game.translate("werewolf.role.seer.not_enough_life"));
        }
        else {
            PlayerWW plg1 = game.getPlayersWW().get(argUUID);
            Roles role1 = plg1.getRole();

            SeerEvent seerEvent=new SeerEvent(uuid,argUUID);

            Bukkit.getPluginManager().callEvent(seerEvent);

            if(seerEvent.isCancelled()){
                player.sendMessage(game.translate("werewolf.check.cancel"));
                return;
            }

            ((Power) seer).setPower(false);
            ((AffectedPlayers)seer).addAffectedPlayer(argUUID);

            if((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.VILLAGER)) || role1.isCamp(Camp.VILLAGER)) {
                player.setMaxHealth(life-6);
                if(player.getHealth()>life-6) {
                    player.setHealth(life-6);
                }
                player.sendMessage(game.translate("werewolf.role.seer.see_villager"));
                if(seer.isDisplay("werewolf.role.chatty_seer.display")){
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.villager")));
                }
                plg.addKLostHeart(6);
            }
            else if((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.WEREWOLF)) || (!(role1 instanceof Display) && role1.isCamp(Camp.WEREWOLF))) {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform",game.translate("werewolf.role.werewolf.display")));
                if(seer.isDisplay("werewolf.role.chatty_seer.display")){
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.categories.werewolf")));
                }
            }
            else if((role1 instanceof Display && ((Display) role1).isDisplayCamp(Camp.NEUTRAL)) || (!(role1 instanceof Display) && role1.isCamp(Camp.NEUTRAL))) {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform",game.translate("werewolf.role.seer.neutral")));
                if(seer.isDisplay("werewolf.role.chatty_seer.display")){
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.role.seer.neutral")));
                }
            }
        }
    }
}
