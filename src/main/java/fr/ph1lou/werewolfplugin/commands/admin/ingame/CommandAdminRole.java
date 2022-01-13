package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.RolesBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CommandAdminRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW != null &&
                playerWW.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            game.getPlayersWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .forEach(playerWW1 -> player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                            Formatter.player(playerWW1.getName()),
                            Formatter.role(game.translate(playerWW1.getRole().getKey())))));
            return;
        }

        AtomicReference<UUID> playerAtomicUUID = new AtomicReference<>();

        game.getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.getName().equalsIgnoreCase(args[0]))
                .forEach(playerWW1 -> playerAtomicUUID.set(playerWW1.getUUID()));

        if (playerAtomicUUID.get() == null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.not_in_game_player"));
            return;
        }

        UUID playerUUID = playerAtomicUUID.get();
        IPlayerWW targetWW = game.getPlayerWW(playerUUID).orElse(null);

        if (targetWW == null) {
            player.sendMessage(game.translate(Prefix.RED.getKey() , "werewolf.check.not_in_game_player"));
            return;
        }

        IRole role = targetWW.getRole();
        player.sendMessage(game.translate("werewolf.commands.admin.role.role",
                Formatter.player(args[0]),
                Formatter.role(game.translate(role.getKey()))));

        if (role instanceof Angel && role.isKey(RolesBase.ANGEL.getKey()) &&
                !((Angel) role).isChoice(AngelForm.ANGEL)) {

            player.sendMessage(game.translate("werewolf.commands.admin.role.angel",
                    Formatter.format("&form&",game.translate(((Angel) role).isChoice(AngelForm.FALLEN_ANGEL) ?
                            RolesBase.FALLEN_ANGEL.getKey() :
                            RolesBase.GUARDIAN_ANGEL.getKey()))));
        }
        if (role instanceof IPower) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.power",
                    Formatter.format("&on&",((IPower) role).hasPower())));
        }
        if (role instanceof ITransformed) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.transformed",
                    Formatter.format("&on&",game.translate(((ITransformed) role).isTransformed() ?
                            "werewolf.commands.admin.role.yes" :
                            "werewolf.commands.admin.role.no"))));
        }


        for (ILover lover : targetWW.getLovers()) {

            StringBuilder sb = new StringBuilder();
            lover.getLovers().stream()
                    .filter(playerWW1 -> !targetWW.equals(playerWW1))
                    .forEach(playerWW1 -> sb.append(playerWW1.getName()).append(" "));

            if (!lover.isKey(LoverType.CURSED_LOVER.getKey())) {
                if (sb.length() != 0) {
                    player.sendMessage(game.translate("werewolf.commands.admin.role.lover",
                            Formatter.player(sb.toString())));
                }
            } else {
                if (sb.length() != 0) {
                    player.sendMessage(game.translate("werewolf.commands.admin.role.cursed_lover",
                            Formatter.player(sb.toString())));
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
                player.sendMessage(game.translate("werewolf.commands.admin.role.affected",
                        Formatter.player(sb.toString())));
            }
        }

        if (role.isKey(RolesBase.SISTER.getKey())) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayersWW()) {
                if (playerWW1.getRole().isKey(RolesBase.SISTER.getKey()) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.sister",
                        Formatter.format("&list&",sb.toString())));

            }
        }

        if (role.isKey(RolesBase.SIAMESE_TWIN.getKey())) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayersWW()) {
                if (playerWW1.getRole().isKey(RolesBase.SIAMESE_TWIN.getKey()) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.siamese_twin",
                        Formatter.format("&list&",sb.toString())));

            }
        }

        sb = new StringBuilder();

        for (IPlayerWW playerWW1 : targetWW.getKillers()) {
            if (playerWW1 != null) {
                sb.append(playerWW1.getName()).append(" ");
            } else sb.append(game.translate("werewolf.utils.pve")).append(" ");
        }

        if (sb.length() != 0) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.kill_by",
                    Formatter.player(sb.toString())));
        }
    }
}
