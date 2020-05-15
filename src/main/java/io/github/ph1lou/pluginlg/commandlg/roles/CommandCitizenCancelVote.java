package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Citizen;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.TimerLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandCitizenCancelVote extends Commands {


    public CommandCitizenCancelVote(MainLG main) {
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

        if (!(plg.getRole() instanceof Citizen)){
            player.sendMessage(game.translate("werewolf.check.role",game.translate("werewolf.role.citizen.display")));
            return;
        }

        Citizen citizen = (Citizen) plg.getRole();


        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!citizen.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (game.score.getTimer() % (game.config.getTimerValues().get(TimerLG.DAY_DURATION) * 2) < game.config.getTimerValues().get(TimerLG.VOTE_DURATION)) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }
        if (!game.config.getConfigValues().get(ToolLG.VOTE) || game.config.getTimerValues().get(TimerLG.VOTE_DURATION) + game.config.getTimerValues().get(TimerLG.VOTE_BEGIN) > 0) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }
        if (game.score.getTimer() % (game.config.getTimerValues().get(TimerLG.DAY_DURATION) * 2) > game.config.getTimerValues().get(TimerLG.VOTE_DURATION) + game.config.getTimerValues().get(TimerLG.CITIZEN_DURATION)) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        citizen.setPower(false);
        UUID vote=game.vote.getResult();
        sender.sendMessage(game.translate("werewolf.role.citizen.cancelling_vote_perform",game.playerLG.get(vote).getName()));
        citizen.addAffectedPlayer(vote);
        Bukkit.broadcastMessage(game.translate("werewolf.role.citizen.cancelling_broadcast"));
        game.vote.resetVote();
    }
}
