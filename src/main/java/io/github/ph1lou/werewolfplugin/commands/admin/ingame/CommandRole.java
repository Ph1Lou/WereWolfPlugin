package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRole implements Commands {


    private final Main main;

    public CommandRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameManager game = main.getCurrentGame();
        UUID uuid = player.getUniqueId();

        if (!game.isState(StateLG.GAME) && !game.isState(StateLG.END)) {
            player.sendMessage(game.translate("werewolf.check.role_not_set"));
            return;
        }

        if (game.getPlayersWW().containsKey(uuid) && game.getPlayersWW().get(uuid).isState(State.ALIVE)) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            for (PlayerWW playerWW : game.getPlayersWW().values()) {
                Bukkit.dispatchCommand(player, "a role " + playerWW.getName());
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
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        if (!game.getPlayersWW().containsKey(playerUUID)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }
        PlayerWW plg = game.getPlayersWW().get(playerUUID);


        Roles role = plg.getRole();
        player.sendMessage(game.translate("werewolf.commands.admin.role.role", args[0], game.translate(role.getDisplay())));

        if (role instanceof AngelRole && role.isDisplay("werewolf.role.angel.display") && !((AngelRole) role).isChoice(AngelForm.ANGEL)) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.angel", game.translate(((AngelRole) role).isChoice(AngelForm.FALLEN_ANGEL) ? "werewolf.role.fallen_angel.display" : "werewolf.role.guardian_angel.display")));
        }
        if (role instanceof Power) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.power", ((Power) role).hasPower()));
        }
        if (role instanceof Transformed) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.transformed", game.translate(((Transformed) role).getTransformed() ? "werewolf.commands.admin.role.yes" : "werewolf.commands.admin.role.no")));
        }
        StringBuilder sb = new StringBuilder();
        for (UUID uuid1 : plg.getLovers()) {
            sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
        }
        if (sb.length() != 0) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.lover", sb.toString()));
        }

        if (plg.getCursedLovers() != null) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.cursed_lover", game.getPlayersWW().get(plg.getCursedLovers()).getName()));
        }

        if (plg.getAmnesiacLoverUUID() != null) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.lover", game.getPlayersWW().get(plg.getAmnesiacLoverUUID()).getName()));
        }

        sb= new StringBuilder();

        if(plg.getRole() instanceof AffectedPlayers) {
            AffectedPlayers affectedPlayers = (AffectedPlayers) plg.getRole();

            for (UUID uuid1 : affectedPlayers.getAffectedPlayers()) {
                sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.affected", sb.toString()));
            }
        }
        
        if(role.isDisplay("werewolf.role.sister.display")) {
            sb = new StringBuilder();

            for (UUID uuid1 : game.getPlayersWW().keySet()) {
                if (game.getPlayersWW().get(uuid1).getRole().isDisplay("werewolf.role.sister.display") && !uuid1.equals(playerUUID)) {
                    sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.sister", sb.toString()));

            }
        }

        if(role.isDisplay("werewolf.role.siamese_twin.display")) {
            sb = new StringBuilder();

            for (UUID uuid1 : game.getPlayersWW().keySet()) {
                if (game.getPlayersWW().get(uuid1).getRole().isDisplay("werewolf.role.siamese_twin.display") && !uuid1.equals(playerUUID)) {
                    sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.siamese_twin", sb.toString()));

            }
        }

        sb = new StringBuilder();

        for (UUID uuid1 : plg.getKillers()) {
            if (uuid1 != null) {
                sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
            } else sb.append(game.translate("werewolf.utils.pve")).append(" ");
        }

        if (sb.length() != 0) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.kill_by", sb.toString()));
        }
    }
}
