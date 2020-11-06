package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enumlg.AngelForm;
import io.github.ph1lou.werewolfapi.enumlg.RolesBase;
import io.github.ph1lou.werewolfapi.enumlg.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CommandAdminRole implements Commands {


    private final Main main;

    public CommandAdminRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();

        if (game.getPlayersWW().containsKey(uuid) &&
                game.getPlayersWW().get(uuid).isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            game.getPlayersWW().values()
                    .stream()
                    .map(PlayerWW::getName)
                    .forEach(s -> Bukkit.dispatchCommand(player,
                            String.format("a role %s", s)));
            return;
        }

        AtomicReference<UUID> playerAtomicUUID = new AtomicReference<>();

        game.getPlayersWW().values()
                .stream()
                .filter(playerWW -> playerWW.getName().equalsIgnoreCase(args[0]))
                .forEach(playerWW -> playerAtomicUUID.set(playerWW.getRole().getPlayerUUID()));

        if (playerAtomicUUID.get() == null) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        UUID playerUUID = playerAtomicUUID.get();

        if (!game.getPlayersWW().containsKey(playerUUID)) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }
        PlayerWW plg = game.getPlayersWW().get(playerUUID);


        Roles role = plg.getRole();
        player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                args[0],
                game.translate(role.getKey())));

        if (role instanceof AngelRole && role.isKey(RolesBase.ANGEL.getKey()) &&
                !((AngelRole) role).isChoice(AngelForm.ANGEL)) {

            player.sendMessage(game.translate("werewolf.commands.admin.role.angel",
                    game.translate(((AngelRole) role).isChoice(AngelForm.FALLEN_ANGEL) ?
                            RolesBase.FALLEN_ANGEL.getKey() :
                            RolesBase.GUARDIAN_ANGEL.getKey())));
        }
        if (role instanceof Power) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.power",
                    ((Power) role).hasPower()));
        }
        if (role instanceof Transformed) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.transformed",
                    game.translate(((Transformed) role).getTransformed() ?
                            "werewolf.commands.admin.role.yes" :
                            "werewolf.commands.admin.role.no")));
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

        if (role.isKey(RolesBase.SISTER.getKey())) {
            sb = new StringBuilder();

            for (UUID uuid1 : game.getPlayersWW().keySet()) {
                if (game.getPlayersWW().get(uuid1).getRole().isKey(RolesBase.SISTER.getKey()) && !uuid1.equals(playerUUID)) {
                    sb.append(game.getPlayersWW().get(uuid1).getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.sister", sb.toString()));

            }
        }

        if (role.isKey(RolesBase.SIAMESE_TWIN.getKey())) {
            sb = new StringBuilder();

            for (UUID uuid1 : game.getPlayersWW().keySet()) {
                if (game.getPlayersWW().get(uuid1).getRole().isKey(RolesBase.SIAMESE_TWIN.getKey()) && !uuid1.equals(playerUUID)) {
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
