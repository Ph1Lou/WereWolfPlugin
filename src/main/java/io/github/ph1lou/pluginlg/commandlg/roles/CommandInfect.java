package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.werewolfroles.InfectFatherOfTheWolves;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.events.NewWereWolfEvent;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.Camp;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandInfect extends Commands {


    public CommandInfect(MainLG main) {
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

        if (!(plg.getRole() instanceof InfectFatherOfTheWolves)){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.infect_father_of_the_wolves")));
            return;
        }

        InfectFatherOfTheWolves infectFatherOfTheWolves = (InfectFatherOfTheWolves) plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!infectFatherOfTheWolves.hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if (!game.config.getConfigValues().get(ToolLG.AUTO_REZ_INFECT) && args[0].equals(playername)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);

        if(!game.playerLG.containsKey(argUUID)) {
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }
        PlayerLG plg1 = game.playerLG.get(argUUID);

        if (!plg1.isState(State.JUDGEMENT)) {
            player.sendMessage(game.translate("werewolf.check.not_in_judgement"));
            return;
        }

        if (!plg1.canBeInfect()) {
            player.sendMessage(game.translate("werewolf.role.infect_father_of_the_wolves.player_cannot_be_infected"));
            return;
        }

        infectFatherOfTheWolves.addAffectedPlayer(argUUID);
        infectFatherOfTheWolves.setPower(false);
        game.playerLG.get(argUUID).setInfected(true);
        if(!plg1.getRole().isCamp(Camp.WEREWOLF)) {
            Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(argUUID));
        }
        plg1.setCanBeInfect(false);
        player.sendMessage(game.translate("werewolf.role.infect_father_of_the_wolves.infection_perform",plg1.getName()));
        game.death_manage.resurrection(argUUID);
    }
}
