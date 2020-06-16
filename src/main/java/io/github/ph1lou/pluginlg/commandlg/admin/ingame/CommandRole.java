package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.PlayerWW;
import io.github.ph1lou.pluginlgapi.enumlg.AngelForm;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.StateLG;
import io.github.ph1lou.pluginlgapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.pluginlgapi.rolesattributs.AngelRole;
import io.github.ph1lou.pluginlgapi.rolesattributs.Power;
import io.github.ph1lou.pluginlgapi.rolesattributs.Roles;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRole implements Commands {


    private final MainLG main;

    public CommandRole(MainLG main) {
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


        UUID playerUUID = null;

        for(PlayerWW playerWW:game.playerLG.values()){
            if(playerWW.getName().toLowerCase().equals(args[0].toLowerCase())){
                playerUUID=playerWW.getRole().getPlayerUUID();
            }
        }


        if(playerUUID==null){
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!game.playerLG.containsKey(playerUUID)) {
            sender.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }
        PlayerWW plg = game.playerLG.get(playerUUID);

        if (game.playerLG.containsKey(player.getUniqueId()) && game.playerLG.get(player.getUniqueId()).isState(State.ALIVE)) {
            sender.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
            return;
        }
        Roles role = plg.getRole();
        sender.sendMessage(game.translate("werewolf.commands.admin.role.role", args[0], game.translate(role.getDisplay())));

        if(role instanceof AngelRole){
            sender.sendMessage(game.translate("werewolf.commands.admin.role.angel", game.translate(((AngelRole) role).isChoice(AngelForm.ANGEL)?"werewolf.role.angel.display":((AngelRole) role).isChoice(AngelForm.FALLEN_ANGEL)?"werewolf.role.fallen_angel.display":"werewolf.role.guardian_angel.display")));
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
        
        if(role.isDisplay("werewolf.role.sister.display")){
            sb=new StringBuilder();

            for (UUID uuid : game.playerLG.keySet()) {
                if(game.playerLG.get(uuid).getRole().isDisplay("werewolf.role.sister.display") && !uuid.equals(playerUUID)){
                    sb.append(game.playerLG.get(uuid).getName()).append(" ");
                }
            }
            if(sb.length()!=0){
                sender.sendMessage(game.translate("werewolf.commands.admin.role.sister", sb.toString()));

            }
        }

        if(role.isDisplay("werewolf.role.siamese_twin.display")){
            sb=new StringBuilder();

            for (UUID uuid : game.playerLG.keySet()) {
                if(game.playerLG.get(uuid).getRole().isDisplay("werewolf.role.siamese_twin.display") && !uuid.equals(playerUUID)){
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
