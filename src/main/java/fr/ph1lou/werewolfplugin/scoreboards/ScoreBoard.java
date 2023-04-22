package fr.ph1lou.werewolfplugin.scoreboards;

import fr.mrmicky.fastboard.FastBoard;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.LoverType;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.IModerationManager;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.utils.Utils;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ScoreBoard {

    private final GameManager game;
    private final List<String> scoreboardLobby = new ArrayList<>();
    private final List<String> scoreboardGame = new ArrayList<>();
    private final List<String> scoreboardScore = new ArrayList<>();
    private final List<String> roles = new ArrayList<>();
    private int day;
    private String dayState;

    public ScoreBoard(GameManager game) {
        this.game = game;
    }

    public void updateScoreBoard1() {

        scoreboardLobby.clear();

        scoreboardLobby.addAll(game.translateArray("werewolf.score_board.scoreboard_lobby",
                        Formatter.format("&players&", game.getPlayersCount()),
                        Formatter.format("&roles&", game.getRoleInitialSize()),
                        Formatter.format("&max&", game.getConfig().getPlayerMax()),
                        Formatter.format("&name&", game.getGameName()))
                .stream()
                .map(s -> s.substring(0, Math.min(30, s.length())))
                .collect(Collectors.toList()));
    }

    public void updateScoreBoard2(FastBoard board) {

        UUID playerUUID = board.getPlayer().getUniqueId();
        List<String> score = new ArrayList<>(scoreboardGame);
        IPlayerWW playerWW = game.getPlayerWW(playerUUID).orElse(null);
        IModerationManager moderationManager = game.getModerationManager();
        String role;
        if (playerWW != null) {

            if (!playerWW.isState(StatePlayer.DEATH)) {

                if (!game.isState(StateGame.GAME)) {
                    role = Utils.conversion(game.getConfig().getTimerValue(TimerBase.ROLE_DURATION));
                } else {
                    role = game.translate(playerWW.getRole().getKey());
                }
            } else {
                role = game.translate("werewolf.score_board.death");
            }
        } else if (moderationManager.getModerators().contains(playerUUID)) {
            role = game.translate("werewolf.commands.admin.moderator.name");
        } else if (moderationManager.getHosts().contains(playerUUID)) {
            role = game.translate("werewolf.commands.admin.host.name");
        } else {
            role = game.translate("werewolf.score_board.spectator");
        }

        for (int i = 0; i < score.size(); i++) {
            score.set(i, score.get(i).replace("&role&", role));
            score.set(i, score.get(i).substring(0, Math.min(30, score.get(i).length())));
        }
        board.updateLines(score);
    }

    private void updateGlobalScoreBoard2() {

        WorldBorder wb = game.getMapManager().getWorld().getWorldBorder();
        String borderSize = String.valueOf(Math.round(wb.getSize()));
        String border;

        if (game.getConfig().getTimerValue(TimerBase.BORDER_BEGIN) > 0) {
            border = Utils.conversion(game.getConfig().getTimerValue(TimerBase.BORDER_BEGIN));
        } else {
            border = game.translate("werewolf.utils.on");
            if (wb.getSize() != game.getConfig().getBorderMin()) {
                borderSize = borderSize + " > " + game.getConfig().getBorderMin();
            }
        }

        scoreboardGame.clear();

        this.day = game.getTimer() / game.getConfig()
                .getTimerValue(TimerBase.DAY_DURATION) / 2 + 1;
        this.dayState = game.translate(game.isDay(Day.DAY) ?
                "werewolf.score_board.day" : "werewolf.score_board.night");


        scoreboardGame.addAll(game.translateArray("werewolf.score_board.scoreboard_game",
                        Formatter.timer(Utils.conversion(game.getTimer())),
                        Formatter.format("&day&", this.day),
                        Formatter.format("&players&", game.getPlayersCount()),
                        Formatter.format("&group&", game.getGroup()),
                        Formatter.format("&border&", border),
                        Formatter.format("&daystate&", this.dayState),
                        Formatter.format("&border_size&", borderSize),
                        Formatter.format("&name&", game.getGameName()))
                .stream()
                .map(s -> s.substring(0, Math.min(30, s.length())))
                .collect(Collectors.toList()));

    }

    private void updateScoreBoardRole() {


        roles.clear();

        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60 < 30) {
            return;
        }

        List<String> composition = new ArrayList<>();

        if (game.getConfig().getLoverCount(LoverBase.LOVER) > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(LoverType.LOVER.getChatColor())
                    .append(game.getConfig().getLoverCount(LoverBase.LOVER))
                    .append("§f ")
                    .append(game.translate(LoverBase.LOVER));
            composition.add(sb.substring(0, Math.min(30, sb.length())));
        }
        if (game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER) > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(LoverType.AMNESIAC_LOVER.getChatColor()).append(game.getConfig().getLoverCount(LoverBase.AMNESIAC_LOVER))
                    .append("§f ")
                    .append(game.translate(LoverBase.AMNESIAC_LOVER));
            composition.add(sb.substring(0, Math.min(30, sb.length())));
        }
        if (game.getConfig().getLoverCount(LoverBase.CURSED_LOVER) > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(LoverType.CURSED_LOVER.getChatColor()).append(game.getConfig().getLoverCount(LoverBase.CURSED_LOVER))
                    .append("§f ")
                    .append(game.translate(LoverBase.CURSED_LOVER));
            composition.add(sb.substring(0, Math.min(30, sb.length())));
        }

        composition.addAll(Register.get().getRolesRegister()
                .stream()
                .sorted((o1, o2) -> game.translate(o1.getMetaDatas().key())
                        .compareToIgnoreCase(game.translate(o2.getMetaDatas().key())))
                .filter(iRoleRoleWrapper -> game.getConfig().getRoleCount(iRoleRoleWrapper.getMetaDatas().key()) > 0)
                .sorted(Comparator.comparingInt(o -> o.getMetaDatas().category().ordinal()))
                .map(iRoleRoleWrapper -> {
                    String key = iRoleRoleWrapper.getMetaDatas().key();

                    StringBuilder sb = new StringBuilder();
                    sb
                            .append(iRoleRoleWrapper.getMetaDatas().category().getChatColor())
                            .append(game.getConfig().getRoleCount(key))
                            .append("§f ")
                            .append(game.translate(key));
                    return sb.substring(0, Math.min(30, sb.length()));
                })
                .collect(Collectors.toList()));

        if (!composition.isEmpty()) {

            int inf = 6 * ((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 30 / 5);
            int total = (int) Math.ceil(composition.size() / 6f);

            if (inf < composition.size()) {

                composition = composition.subList(inf, Math.min(composition.size(), inf + 6));

                this.roles.addAll(game.translateArray("werewolf.score_board.scoreboard_role",
                                Formatter.format("&current&", inf / 6 + 1),
                                Formatter.format("&sum&", total),
                                Formatter.format("&name&", game.getGameName()))
                        .stream()
                        .map(s -> s.substring(0, Math.min(30, s.length())))
                        .collect(Collectors.toList()));

                AtomicInteger index = new AtomicInteger(this.roles.indexOf("&roles&"));

                if (index.get() != -1) {
                    this.roles.remove(index.get());
                    composition.forEach(s -> this.roles.add(index.getAndIncrement(), s.substring(0, Math.min(30, s.length()))));
                }
            }
        }
    }

    public void getFinalScore() {

        scoreboardScore.clear();

        List<IPlayerWW> topKillers = game.getPlayersWW().stream()
                .sorted(Comparator.comparingInt(value -> value.getPlayersKills().size()))
                .collect(Collectors.toList());

        scoreboardScore.addAll(game.translateArray("werewolf.score_board.scoreboard_score",
                        Formatter.format("&name&", game.getGameName()))
                .stream()
                .map(s -> s.substring(0, Math.min(30, s.length())))
                .collect(Collectors.toList()));

        int index = scoreboardScore.indexOf("&scores&");

        if (index != -1) {
            scoreboardScore.remove(index);
            for (int i = 0; i < Math.min(game.getPlayersWW().size(), 10); i++) {
                scoreboardScore.add(index++, topKillers.get(topKillers.size() - 1).getName() +
                        "§3 " + topKillers.remove(topKillers.size() - 1).getPlayersKills().size());
            }
        }
    }

    public void updateBoard() {

        if (game.isState(StateGame.END) && this.scoreboardScore.isEmpty()) {
            this.getFinalScore();
        }

        if (Bukkit.getOnlinePlayers().size() == 0) return;

        if (!game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION)) {
            updateScoreBoardRole();
        }

        if (roles.isEmpty()) {
            if (game.isState(StateGame.LOBBY)) {
                updateScoreBoard1();
            } else if (!game.isState(StateGame.END)) {
                updateGlobalScoreBoard2();
            }
        }

        String bot = "";

        if (game.isState(StateGame.START) || game.isState(StateGame.GAME)) {
            bot = game.translate("werewolf.tab.timer",
                    Formatter.timer(Utils.conversion(game.getTimer())),
                    Formatter.format("&day&", day),
                    Formatter.format("&day_state&", dayState));
        }

        bot += game.translate("werewolf.tab.bot");

        for (FastBoard board : game.getBoards().values()) {

            VersionUtils.getVersionUtils().sendTabTitle(board.getPlayer(),
                    game.translate("werewolf.tab.top"),
                    bot);

            if (game.isState(StateGame.END)) {
                board.updateLines(scoreboardScore);
            } else if (!roles.isEmpty()) {
                board.updateLines(roles);
            } else if (game.isState(StateGame.LOBBY)) {
                board.updateLines(scoreboardLobby);
            } else {
                updateScoreBoard2(board);
            }
        }

    }
}