package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.AngelRole;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRole implements Commands {


    private final Main main;

    public CommandRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        GameManager game = main.getCurrentGame();


        if (!sender.hasPermission("a.use") && !sender.hasPermission("a.role.use") && !game.getModerators().contains(((Player) sender).getUniqueId()) && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(game.translate("werewolf.check.permission_denied"));
            return;
        }
        
        if (!game.isState(StateLG.GAME) && !game.isState(StateLG.END)) {
            sender.sendMessage(game.translate("werewolf.check.role_not_set"));
            return;
        }
        if (args.length == 0) {
            for(PlayerWW playerWW:game.getPlayersWW().values()){
                Bukkit.dispatchCommand(sender,"a role "+playerWW.getName());
            }
            return;
        }


        UUID playerUUID = null;

        for(PlayerWW playerWW:game.getPlayersWW().values()){
            if(playerWW.getName().toLowerCase().equals(args[0].toLowerCase())){
                playerUUID=playerWW.getRole().getPlayerUUID();
            }
        }


        if(playerUUID==null){
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!game.getPlayersWW().containsKey(playerUUID)) {
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }
        PlayerWW plg = game.getPlayersWW().get(playerUUID);

        if(sender instanceof Player){
            Player player = (Player) sender;
            if (game.getPlayersWW().containsKey(player.getUniqueId()) && game.getPlayersWW().get(player.getUniqueId()).isState(State.ALIVE)) {
                sender.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
                return;
            }
        }

        Roles role = plg.getRole();
        sender.sendMessage(game.translate("werewolf.commands.admin.role.role", args[0], game.translate(role.getDisplay())));

        if(role instanceof AngelRole && role.isDisplay("werewolf.role.angel.display") && !((AngelRole) role).isChoice(AngelForm.ANGEL)){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.angel", game.translate(((AngelRole) role).isChoice(AngelForm.FALLEN_ANGEL)?"werewolf.role.fallen_angel.display":"werewolf.role.guardian_angel.display")));
        }
        if(role instanceof Power){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.power", ((Power) role).hasPower()));

        }
        StringBuilder sb = new StringBuilder();
        for (UUID uuid : plg.getLovers()) {
            sb.append(game.getPlayersWW().get(uuid).getName()).append(" ");
        }
        if(sb.length()!=0){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.lover", sb.toString()));
        }

        if (plg.getCursedLovers()!=null) {
            sender.sendMessage(game.translate("werewolf.commands.admin.role.cursed_lover", game.getPlayersWW().get(plg.getCursedLovers()).getName()));
        }

        if (plg.getAmnesiacLoverUUID()!=null) {
            sender.sendMessage(game.translate("werewolf.commands.admin.role.lover", game.getPlayersWW().get(plg.getAmnesiacLoverUUID()).getName()));
        }

        sb= new StringBuilder();

        if(plg.getRole() instanceof AffectedPlayers){
            AffectedPlayers affectedPlayers= (AffectedPlayers) plg.getRole();

            for (UUID uuid : affectedPlayers.getAffectedPlayers()) {
                sb.append(game.getPlayersWW().get(uuid).getName()).append(" ");
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.affected", sb.toString()));
            }
        }
        
        if(role.isDisplay("werewolf.role.sister.display")){
            sb=new StringBuilder();

            for (UUID uuid : game.getPlayersWW().keySet()) {
                if(game.getPlayersWW().get(uuid).getRole().isDisplay("werewolf.role.sister.display") && !uuid.equals(playerUUID)){
                    sb.append(game.getPlayersWW().get(uuid).getName()).append(" ");
                }
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.sister", sb.toString()));

            }
        }

        if(role.isDisplay("werewolf.role.siamese_twin.display")){
            sb=new StringBuilder();

            for (UUID uuid : game.getPlayersWW().keySet()) {
                if(game.getPlayersWW().get(uuid).getRole().isDisplay("werewolf.role.siamese_twin.display") && !uuid.equals(playerUUID)){
                    sb.append(game.getPlayersWW().get(uuid).getName()).append(" ");
                }
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.siamese_twin", sb.toString()));

            }
        }

        sb=new StringBuilder();

        for (UUID uuid : plg.getKillers()) {
            if(uuid != null){
                sb.append(game.getPlayersWW().get(uuid).getName()).append(" ");
            }
            else sb.append(game.translate("werewolf.utils.pve")).append(" ");
        }

        if(sb.length()!=0){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.kill_by",sb.toString()));
        }
    }
}
