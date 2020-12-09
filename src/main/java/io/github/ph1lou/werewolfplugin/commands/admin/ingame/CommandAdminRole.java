package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.Commands;
import io.github.ph1lou.werewolfapi.LoverAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
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
        PlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW != null &&
                playerWW.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            game.getPlayerWW()
                    .stream()
                    .map(PlayerWW::getName)
                    .forEach(s -> Bukkit.dispatchCommand(player,
                            String.format("a role %s", s)));
            return;
        }

        AtomicReference<UUID> playerAtomicUUID = new AtomicReference<>();

        game.getPlayerWW()
                .stream()
                .filter(playerWW1 -> playerWW1.getName().equalsIgnoreCase(args[0]))
                .forEach(playerWW1 -> playerAtomicUUID.set(playerWW1.getUUID()));

        if (playerAtomicUUID.get() == null) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        UUID playerUUID = playerAtomicUUID.get();
        PlayerWW targetWW = game.getPlayerWW(playerUUID);

        if (targetWW == null) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        Roles role = targetWW.getRole();
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


        for (LoverAPI loverAPI : targetWW.getLovers()) {

            StringBuilder sb = new StringBuilder();

            if (!loverAPI.isKey(RolesBase.CURSED_LOVER.getKey())) {
                for (PlayerWW playerWW1 : loverAPI.getLovers()) {
                    sb.append(playerWW1.getName()).append(" ");
                }
                if (sb.length() != 0) {
                    player.sendMessage(game.translate("werewolf.commands.admin.role.lover", sb.toString()));
                }
            } else {
                for (PlayerWW playerWW1 : loverAPI.getLovers()) {
                    sb.append(playerWW1.getName()).append(" ");
                }
                if (sb.length() != 0) {
                    player.sendMessage(game.translate("werewolf.commands.admin.role.cursed_lover", sb.toString()));
                }
            }
        }


        StringBuilder sb = new StringBuilder();

        if (targetWW.getRole() instanceof AffectedPlayers) {
            AffectedPlayers affectedPlayers = (AffectedPlayers) targetWW.getRole();

            for (PlayerWW playerWW1 : affectedPlayers.getAffectedPlayers()) {
                sb.append(playerWW1.getName()).append(" ");
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.affected", sb.toString()));
            }
        }

        if (role.isKey(RolesBase.SISTER.getKey())) {
            sb = new StringBuilder();

            for (PlayerWW playerWW1 : game.getPlayerWW()) {
                if (playerWW1.isKey(RolesBase.SISTER.getKey()) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.sister", sb.toString()));

            }
        }

        if (role.isKey(RolesBase.SIAMESE_TWIN.getKey())) {
            sb = new StringBuilder();

            for (PlayerWW playerWW1 : game.getPlayerWW()) {
                if (playerWW1.getRole().isKey(RolesBase.SIAMESE_TWIN.getKey()) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.siamese_twin", sb.toString()));

            }
        }

        sb = new StringBuilder();

        for (PlayerWW playerWW1 : targetWW.getKillers()) {
            if (playerWW1 != null) {
                sb.append(playerWW1.getName()).append(" ");
            } else sb.append(game.translate("werewolf.utils.pve")).append(" ");
        }

        if (sb.length() != 0) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.kill_by", sb.toString()));
        }
    }
}
