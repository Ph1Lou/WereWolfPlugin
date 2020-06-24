package io.github.ph1lou.werewolfplugin.commandlg.roles;


import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.AngelForm;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.AngelChoiceEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AngelRole;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandFallenAngel implements Commands {


    private final Main main;

    public CommandFallenAngel(Main main) {
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

        if (!(plg.getRole() instanceof AngelRole)){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.angel.display")));
            return;
        }

        AngelRole role = (AngelRole) plg.getRole();

        if(!(role.isChoice(AngelForm.ANGEL))) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        role.setChoice(AngelForm.FALLEN_ANGEL);
        Bukkit.getPluginManager().callEvent(new AngelChoiceEvent(uuid,AngelForm.FALLEN_ANGEL));
        sender.sendMessage(game.translate("werewolf.role.angel.angel_choice_perform",game.translate("werewolf.role.fallen_angel.display")));
    }
}
