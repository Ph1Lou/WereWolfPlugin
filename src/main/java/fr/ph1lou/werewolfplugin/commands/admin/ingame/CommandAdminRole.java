package fr.ph1lou.werewolfplugin.commands.admin.ingame;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.AngelForm;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILover;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.interfaces.IPowers;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.role.interfaces.ITransformed;
import fr.ph1lou.werewolfapi.statistics.interfaces.IGameReview;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.roles.neutrals.Angel;
import fr.ph1lou.werewolfplugin.statistiks.StatsHoverText;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AdminCommand(key = "werewolf.commands.admin.role.command",
        descriptionKey = "werewolf.commands.admin.role.description",
        statesGame = { StateGame.GAME, StateGame.END },
        argNumbers = { 0, 1 },
        moderatorAccess = true)
public class CommandAdminRole implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW != null &&
            playerWW.isState(StatePlayer.ALIVE)) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.commands.admin.role.in_game"));
            return;
        }

        if (args.length == 0) {
            IGameReview review = getReview();
            Map<UUID, String> reviewNames = StatsHoverText.buildReviewNames(game);
            game.getPlayersWW()
                    .stream()
                    .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                    .forEach(playerWW1 -> {
                        String name = playerWW1.getName();
                        String message = game.translate("werewolf.commands.admin.role.role",
                                Formatter.player(name),
                                Formatter.role(game.translate(playerWW1.getRole().getKey())));
                        String hover = StatsHoverText.build(game, playerWW1, review, reviewNames);
                        if (hover.isEmpty()) {
                            player.sendMessage(message);
                        } else {
                            VersionUtils.getVersionUtils().sendEndMessage(player, message, name, hover);
                        }
                    });
            return;
        }

        IPlayerWW targetWW = game.getPlayersWW()
                .stream()
                .filter(playerWW1 -> playerWW1.getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .map(playerWW1 -> game.getPlayerWW(playerWW1.getUUID()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);

        if (targetWW == null) {
            player.sendMessage(game.translate(Prefix.RED, "werewolf.check.not_in_game_player"));
            return;
        }

        IRole role = targetWW.getRole();
        String roleMessage = game.translate("werewolf.commands.admin.role.role",
                Formatter.player(args[0]),
                Formatter.role(game.translate(role.getKey())));
        String hover = StatsHoverText.build(game, targetWW, getReview(), StatsHoverText.buildReviewNames(game));
        if (hover.isEmpty()) {
            player.sendMessage(roleMessage);
        } else {
            VersionUtils.getVersionUtils().sendEndMessage(player, roleMessage, args[0], hover);
        }

        if (role instanceof Angel && role.isKey(RoleBase.ANGEL) &&
            !((Angel) role).isChoice(AngelForm.ANGEL)) {

            player.sendMessage(game.translate("werewolf.roles.angel.choice_form",
                    Formatter.format("&form&", game.translate(((Angel) role).isChoice(AngelForm.FALLEN_ANGEL) ?
                            RoleBase.FALLEN_ANGEL :
                            RoleBase.GUARDIAN_ANGEL))));
        }
        if (role instanceof IPowers) {
            IPowers powers = (IPowers) role;
            StringBuilder list = new StringBuilder();
            for (String key : powers.getPowers().keys()) {
                if (list.length() > 0) list.append(", ");
                list.append(game.translate(key)).append(":").append(powers.getPowerUses(key));
            }
            if (list.length() > 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.powers",
                        Formatter.format("&list&", list.toString())));
            }
        } else if (role instanceof IPower) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.power",
                    Formatter.format("&on&", ((IPower) role).hasPower())));
        }
        if (role instanceof ITransformed) {
            player.sendMessage(game.translate("werewolf.commands.admin.role.transformed",
                    Formatter.format("&on&", game.translate(((ITransformed) role).isTransformed() ?
                            "werewolf.commands.admin.role.yes" :
                            "werewolf.commands.admin.role.no"))));
        }


        for (ILover lover : targetWW.getLovers()) {

            StringBuilder sb = new StringBuilder();
            lover.getLovers().stream()
                    .filter(playerWW1 -> !targetWW.equals(playerWW1))
                    .forEach(playerWW1 -> sb.append(playerWW1.getName()).append(" "));

            if (!lover.isKey(LoverBase.CURSED_LOVER)) {
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
                if (playerWW1 != null) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.commands.admin.role.affected",
                        Formatter.player(sb.toString())));
            }
        }

        if (role.isKey(RoleBase.SISTER)) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayersWW()) {
                if (playerWW1.getRole().isKey(RoleBase.SISTER) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.roles.sister.sisters",
                        Formatter.format("&list&", sb.toString())));

            }
        }

        if (role.isKey(RoleBase.SIAMESE_TWIN)) {
            sb = new StringBuilder();

            for (IPlayerWW playerWW1 : game.getPlayersWW()) {
                if (playerWW1.getRole().isKey(RoleBase.SIAMESE_TWIN) && !playerWW1.equals(targetWW)) {
                    sb.append(playerWW1.getName()).append(" ");
                }
            }
            if (sb.length() != 0) {
                player.sendMessage(game.translate("werewolf.roles.siamese_twin.siamese_twin",
                        Formatter.format("&list&", sb.toString())));

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

        player.sendMessage(game.translate("werewolf.commands.admin.role.honor", Formatter.number(targetWW.getHonor())));
    }

    /**
     * Récupère le {@link IGameReview} courant (actions enregistrées pendant la partie),
     * ou {@code null} si les statistiques sont indisponibles.
     */
    private IGameReview getReview() {
        return Main.getPlugin(Main.class).getStatisticsEvents() != null
                ? Main.getPlugin(Main.class).getStatisticsEvents().getGameReview()
                : null;
    }
}
