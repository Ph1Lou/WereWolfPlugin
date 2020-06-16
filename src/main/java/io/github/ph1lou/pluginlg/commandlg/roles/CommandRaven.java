package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.CurseEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandRaven implements Commands {


    private final MainLG main;

    public CommandRaven(MainLG main) {
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

        if (!(plg.getRole().isDisplay("werewolf.role.raven.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.raven.display")));
            return;
        }

        Roles raven = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)raven).hasPower()) {
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

        if(((AffectedPlayers)raven).getAffectedPlayers().contains(argUUID)){
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }

        CurseEvent curseEvent=new CurseEvent(uuid,argUUID);

        Bukkit.getPluginManager().callEvent(curseEvent);

        if(curseEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) raven).clearAffectedPlayer();
        ((Power) raven).setPower(false);
        ((AffectedPlayers) raven).addAffectedPlayer(argUUID);
        game.playerLG.get(argUUID).setDamn(true);
        Player playerDamned=Bukkit.getPlayer(args[0]);
        playerDamned.removePotionEffect(PotionEffectType.JUMP);
        playerDamned.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE,1,false,false));
        playerDamned.sendMessage(game.translate("werewolf.role.raven.get_curse"));
        player.sendMessage(game.translate("werewolf.role.raven.curse_perform",args[0]));
    }
}
