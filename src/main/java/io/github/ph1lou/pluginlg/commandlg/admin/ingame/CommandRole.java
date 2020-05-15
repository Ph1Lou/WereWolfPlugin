package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.Power;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Angel;
import io.github.ph1lou.pluginlg.classesroles.villageroles.SiameseTwin;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Sister;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.PlayerLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRole extends Commands {


    public CommandRole(MainLG main) {
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

        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.role.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        
        if (!game.isState(StateLG.GAME) && !game.isState(StateLG.END)) {
            sender.sendMessage(game.translate("werewolf.check.role_not_set"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(game.translate("werewolf.check.player_input"));
            return;
        }
        if(Bukkit.getPlayer(args[0])==null){
            sender.sendMessage(game.translate("werewolf.check.offline_player"));
            return;
        }

        UUID playerUUID = Bukkit.getPlayer(args[0]).getUniqueId();

        if (!game.playerLG.containsKey(playerUUID)) {
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }
        PlayerLG plg = game.playerLG.get(playerUUID);

        if (game.playerLG.containsKey(player.getUniqueId()) && game.playerLG.get(player.getUniqueId()).isState(State.ALIVE)) {
            sender.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
            return;
        }
        RolesImpl role = plg.getRole();
        sender.sendMessage(game.translate("werewolf.commands.admin.role.role", args[0], role.getDisplay()));
        if(role instanceof Angel){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.angel", game.translate(((Angel) role).getChoice().getKey())));

        }
        if(role instanceof Power){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.power", ((Power) role).hasPower()));

        }
        StringBuilder sb = new StringBuilder();
        for (UUID uuid : plg.getLovers()) {
            sb.append(game.playerLG.get(uuid).getName()).append(" ");
        }
        if(sb.length()!=0){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.lover", sb.toString()));
        }

        if (plg.getCursedLovers()!=null) {
            sender.sendMessage(game.translate("werewolf.commands.admin.role.cursed_lover", game.playerLG.get(plg.getCursedLovers()).getName()));
        }

        if (plg.getAmnesiacLoverUUID()!=null) {
            sender.sendMessage(game.translate("werewolf.commands.admin.role.lover", game.playerLG.get(plg.getAmnesiacLoverUUID()).getName()));
        }

        sb= new StringBuilder();

        if(plg.getRole() instanceof AffectedPlayers){
            AffectedPlayers affectedPlayers= (AffectedPlayers) plg.getRole();

            for (UUID uuid : affectedPlayers.getAffectedPlayers()) {
                sb.append(game.playerLG.get(uuid).getName()).append(" ");
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.affected", sb.toString()));
            }
        }




        if(role instanceof Sister){
            sb=new StringBuilder();

            for (UUID uuid : game.playerLG.keySet()) {
                if(game.playerLG.get(uuid).getRole() instanceof Sister && !uuid.equals(playerUUID)){
                    sb.append(game.playerLG.get(uuid).getName()).append(" ");
                }
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.sister", sb.toString()));

            }
        }

        if(role instanceof SiameseTwin){
            sb=new StringBuilder();

            for (UUID uuid : game.playerLG.keySet()) {
                if(game.playerLG.get(uuid).getRole() instanceof SiameseTwin && !uuid.equals(playerUUID)){
                    sb.append(game.playerLG.get(uuid).getName()).append(" ");
                }
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.siamese_twin", sb.toString()));

            }
        }

        sb=new StringBuilder();

        for (UUID uuid : plg.getKillers()) {
            sb.append(game.playerLG.get(uuid).getName()).append(" ");
        }

        if(sb.length()!=0){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.kill_by",sb.toString()));
        }
    }
}
