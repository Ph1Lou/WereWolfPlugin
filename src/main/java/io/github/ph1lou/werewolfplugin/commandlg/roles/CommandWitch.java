package io.github.ph1lou.werewolfplugin.commandlg.roles;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.enumlg.ToolLG;
import io.github.ph1lou.werewolfapi.events.WitchResurrectionEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWitch implements Commands {


    private final Main main;

    public CommandWitch(Main main) {
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

        if (!(plg.getRole().isDisplay("werewolf.role.witch.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.witch.display")));
            return;
        }

        Roles witch = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)witch).hasPower()) {
            player.sendMessage(game.translate("werewolf.check.power"));
            return;
        }

        if(Bukkit.getPlayer(UUID.fromString(args[0]))==null){
            player.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }
        UUID argUUID = UUID.fromString(args[0]);

        if (!game.getConfig().getConfigValues().get(ToolLG.AUTO_REZ_WITCH) && argUUID.equals(uuid)) {
            player.sendMessage(game.translate("werewolf.check.not_yourself"));
            return;
        }

        if(!game.getPlayersWW().containsKey(argUUID)){
            player.sendMessage(game.translate("werewolf.check.player_not_found"));
            return;
        }

        PlayerWW plg1 = game.getPlayersWW().get(argUUID);

        if (!plg1.isState(State.JUDGEMENT)) {
            player.sendMessage(game.translate("werewolf.check.not_in_judgement"));
            return;
        }

        if (plg1.canBeInfect()) {
            return;
        }
        WitchResurrectionEvent witchResurrectionEvent=new WitchResurrectionEvent(uuid,argUUID);
        Bukkit.getPluginManager().callEvent(witchResurrectionEvent);

        if(witchResurrectionEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers)witch).addAffectedPlayer(argUUID);
        ((Power) witch).setPower(false);
        game.resurrection(argUUID);
        sender.sendMessage(game.translate("werewolf.role.witch.resuscitation_perform",plg1.getName()));
    }
}
