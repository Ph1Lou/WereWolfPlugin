package io.github.ph1lou.pluginlg.commandlg.roles;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.events.ProtectionEvent;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CommandProtector implements Commands {


    private final MainLG main;

    public CommandProtector(MainLG main) {
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

        if (!(plg.getRole().isDisplay("werewolf.role.protector.display"))){
            player.sendMessage(game.translate("werewolf.check.role", game.translate("werewolf.role.protector.display")));
            return;
        }

        Roles protector = plg.getRole();

        if (args.length!=1) {
            player.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }

        if(!plg.isState(State.ALIVE)){
            player.sendMessage(game.translate("werewolf.check.death"));
            return;
        }

        if(!((Power)protector).hasPower()) {
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

        if(((AffectedPlayers)protector).getAffectedPlayers().contains(argUUID)){
            player.sendMessage(game.translate("werewolf.check.already_get_power"));
            return;
        }

        ProtectionEvent protectionEvent=new ProtectionEvent(uuid,argUUID);

        Bukkit.getPluginManager().callEvent(protectionEvent);

        if(protectionEvent.isCancelled()){
            player.sendMessage(game.translate("werewolf.check.cancel"));
            return;
        }

        ((AffectedPlayers) protector).clearAffectedPlayer();
        ((Power) protector).setPower(false);
        ((AffectedPlayers) protector).addAffectedPlayer(argUUID);

        Player playerProtected = Bukkit.getPlayer(args[0]);
        playerProtected.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        playerProtected.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,0,false,false));
        game.playerLG.get(argUUID).setSalvation(true);
        playerProtected.sendMessage(game.translate("werewolf.role.protector.get_protection"));
        player.sendMessage(game.translate("werewolf.role.protector.protection_perform",args[0]));
    }
}
