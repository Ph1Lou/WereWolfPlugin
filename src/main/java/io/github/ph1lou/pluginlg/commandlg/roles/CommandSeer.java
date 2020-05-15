package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.villageroles.ChattySeer;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Seer;
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

public class CommandSeer extends Commands {


    public CommandSeer(MainLG main) {
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

        if (!(plg.getRole() instanceof Seer)){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.seer.display")));
            return;
        }

        Seer seer = (Seer) plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!seer.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(Bukkit.getPlayer(args[0])==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if(!game.playerLG.containsKey(argUUID) || !game.playerLG.get(argUUID).isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        double life =player.getMaxHealth();

        if (life<7) {
            player.sendMessage(game.translate("werewolf.role.seer.not_enough_life"));
        }
        else {
            PlayerLG plg1 = game.playerLG.get(argUUID);
            RolesImpl role1 = plg1.getRole();
            seer.setPower(false);
            seer.addAffectedPlayer(argUUID);

            if((role1 instanceof FalsifierWereWolf && ((FalsifierWereWolf) role1).isPosterCamp(Camp.VILLAGER)) || role1.isCamp(Camp.VILLAGER)) {
                player.setMaxHealth(life-6);
                if(player.getHealth()>life-6) {
                    player.setHealth(life-6);
                }
                player.sendMessage(game.translate("werewolf.role.seer.see_villager"));
                if(seer instanceof ChattySeer){
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.role.villager.display")));
                }
                plg.addKLostHeart(6);
            }
            else if((role1 instanceof FalsifierWereWolf && ((FalsifierWereWolf) role1).isPosterCamp(Camp.WEREWOLF)) || (!(role1 instanceof FalsifierWereWolf) && role1.isCamp(Camp.WEREWOLF))) {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform",game.translate("werewolf.role.werewolf.display")));
                if(seer instanceof ChattySeer){
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.role.werewolf.display")));
                }
            }
            else if((role1 instanceof FalsifierWereWolf && ((FalsifierWereWolf) role1).isPosterCamp(Camp.NEUTRAL)) || role1.isCamp(Camp.NEUTRAL)) {
                player.sendMessage(game.translate("werewolf.role.seer.see_perform",game.translate("werewolf.role.seer.neutral")));
                if(seer instanceof ChattySeer){
                    Bukkit.broadcastMessage(game.translate("werewolf.role.chatty_seer.see_perform", game.translate("werewolf.role.seer.neutral")));
                }
            }
        }
    }
}
