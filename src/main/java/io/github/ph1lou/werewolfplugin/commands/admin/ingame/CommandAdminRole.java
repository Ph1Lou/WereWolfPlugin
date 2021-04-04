package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.ILover;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.AngelForm;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.RolesBase;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.ITransformed;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.roles.neutrals.Angel;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CommandAdminRole implements ICommands {


    private final Main main;

    public CommandAdminRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW != null &&
                playerWW.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            game.getPlayerWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .forEach(playerWW1 -> player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                            playerWW1.getName(),
                            game.translate(playerWW1.getRole().getKey()))));
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
        IPlayerWW targetWW = game.getPlayerWW(playerUUID);

        if (targetWW == null) {
            player.sendMessage(game.translate("werewolf.check.not_in_game_player"));
            return;
        }

        IRole role = targetWW.getRole();
        player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                args[0],
                game.translate(role.getKey())));

        if (role instanceof Angel && role.isKey(RolesBase.ANGEL.getKey()) &&
                !((Angel) role).isChoice(AngelForm.ANGEL)) {

            player.sendMessage(game.translate("werewolf.commands.admin.role.angel",
                    game.translate(((Angel) role).isChoice(AngelForm.FALLEN_ANGEL) ?
                            RolesBase.FALLEN_ANGEL.getKey() :
                            RolesBase.GUARDIAN_ANGEL.getKey())));
        }
        if (role instanceof IPower) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.power",
                    ((IPower) role).hasPower()));
        }
        if (role instanceof ITransformed) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.transformed",
                    game.translate(((ITransformed) role).getTransformed() ?
                            "werewolf.commands.admin.role.yes" :
                            "werewolf.commands.admin.role.no")));
        }


        for (ILover ILover : targetWW.getLovers()) {

            StringBuilder sb = new StringBuilder();
            ILover.getLovers().stream()
                    .filter(playerWW1 -> !targetWW.equals(playerWW1))
                    .forEach(playerWW1 -> sb.append(playerWW1.getName()).append(" "));

            if (!ILover.isKey(LoverType.CURSED_LOVER.getKey())) {
                if (sb.length() != 0) {
                    player.sendMessage(game.translate("werewolf.commands.admin.role.lover", sb.toString()));
                }
            } else {
                if (sb.length() != 0) {
                    player.sendMessage(game.translate("werewolf.commands.admin.role.cursed_lover", sb.toString()));
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        if (targetWW.getRole() instanceof IAffectedPlayers) {
            IAffectedPlayers affectedPlayers = (IAffectedPlayers) targetWW.getRole();

            for (IPlayerWW playerWW1 : affectedPlayers.getAffectedPlayers()) {
                sb.append(playerWW1.getName()).append(" ");
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.affected", sb.toString()));
            }
        }

        if (role.isKey(RolesBase.SISTER.getKey())) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayerWW()) {
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

            for (IPlayerWW playerWW1 : game.getPlayerWW()) {
                if (playerWW1.getRole().isKey(RolesBase.SIAMESE_TWIN.getKey()) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.siamese_twin", sb.toString()));

            }
        }

        sb = new StringBuilder();

        for (IPlayerWW playerWW1 : targetWW.getKillers()) {
            if (playerWW1 != null) {
                sb.append(playerWW1.getName()).append(" ");
            } else sb.append(game.translate("werewolf.utils.pve")).append(" ");
        }

        if (sb.length() != 0) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.kill_by", sb.toString()));
        }
    }
}
