package io.github.ph1lou.werewolfplugin.scoreboards;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.IScoreBoard;
import io.github.ph1lou.werewolfapi.enums.ConfigBase;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimerBase;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ScoreBoard implements IScoreBoard {

	private final GameManager game;

	private int day;
	private String dayState;
	private final List<String> scoreboard1 = new ArrayList<>();
	private final List<String> scoreboard2 = new ArrayList<>();
	private final List<String> scoreboard3 = new ArrayList<>();
	private List<String> roles = new ArrayList<>();

	public ScoreBoard(GameManager game) {
		this.game = game;
	}

	public void updateScoreBoard1() {

		scoreboard1.clear();

		scoreboard1.addAll(game.translateArray("werewolf.score_board.scoreboard_1",
				Formatter.format("&players&", game.getPlayerSize()),
				Formatter.format("&roles&", game.getRoleInitialSize()),
				Formatter.format("&max&",game.getConfig().getPlayerMax())).stream()
				.map(s -> s.substring(0, Math.min(30, s.length())))
				.collect(Collectors.toList()));

		String line = game.translate("werewolf.score_board.game_name");
		scoreboard1.add(line.substring(0, Math.min(30, line.length())));
		line = game.translate("werewolf.score_board.name", game.getGameName());
		scoreboard1.add(line.substring(0, Math.min(30, line.length())));
	}
	
	public void updateScoreBoard2(FastBoard board) {

		UUID playerUUID = board.getPlayer().getUniqueId();
		List<String> score = new ArrayList<>(scoreboard2);
		IPlayerWW playerWW = game.getPlayerWW(playerUUID).orElse(null);
		IModerationManager moderationManager = game.getModerationManager();
		String role;
		if (playerWW != null) {

			if (!playerWW.isState(StatePlayer.DEATH)) {

				if (!game.isState(StateGame.GAME)) {
					role = Utils.conversion(game.getConfig().getTimerValue(TimerBase.ROLE_DURATION.getKey()));
				} else role = game.translate(playerWW.getRole().getKey());
			} else role = game.translate("werewolf.score_board.death");
		} else if (moderationManager.getModerators().contains(playerUUID)) {
			role = game.translate("werewolf.commands.admin.moderator.name");
		} else if (moderationManager.getHosts().contains(playerUUID)) {
			role = game.translate("werewolf.commands.admin.host.name");
		} else role = game.translate("werewolf.score_board.spectator");

		for(int i=0;i<score.size();i++){
			score.set(i,score.get(i).replace("&role&",role));
			score.set(i,score.get(i).substring(0,Math.min(30,score.get(i).length())));
		}
		board.updateLines(score);
	}

	private void updateGlobalScoreBoard2() {

		WorldBorder wb = game.getMapManager().getWorld().getWorldBorder();
		String borderSize = String.valueOf(Math.round(wb.getSize()));
		String border;

		if (game.getConfig().getTimerValue(TimerBase.BORDER_BEGIN.getKey()) > 0) {
			border = Utils.conversion(game.getConfig().getTimerValue(TimerBase.BORDER_BEGIN.getKey()));
		} else {
			border = game.translate("werewolf.utils.on");
			if (wb.getSize() > game.getConfig().getBorderMin()) {
				borderSize = borderSize + " > " + game.getConfig().getBorderMin();
			}
		}

		scoreboard2.clear();

		this.day = game.getTimer() / game.getConfig().getTimerValue(TimerBase.DAY_DURATION.getKey()) / 2 + 1;
		this.dayState = game.translate(game.isDay(Day.DAY) ? "werewolf.score_board.day" : "werewolf.score_board.night");


		scoreboard2.addAll(game.translateArray("werewolf.score_board.scoreboard_2",
				Formatter.format("&timer&",Utils.conversion(game.getTimer())),
				Formatter.format("&day&", this.day),
				Formatter.format("&players&", game.getPlayerSize()),
				Formatter.format("&group&", game.getGroup()),
				Formatter.format("&border&",border),
				Formatter.format("&daystate&",this.dayState),
				Formatter.format("&borderSize&",borderSize)));

		scoreboard2.add(game.translate("werewolf.score_board.game_name"));
		scoreboard2.add(game.translate("werewolf.score_board.name", game.getGameName()));
	}

	private void updateScoreBoardRole(){


		if (game.getConfig().getLoverCount(LoverType.LOVER.getKey()) > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("§3").append(game.getConfig().getLoverCount(LoverType.LOVER.getKey())).append("§f ").append(game.translate(LoverType.LOVER.getKey()));
			roles.add(sb.substring(0, Math.min(30, sb.length())));
		}
		if (game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey()) > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("§3").append(game.getConfig().getLoverCount(LoverType.AMNESIAC_LOVER.getKey())).append("§f ").append(game.translate(LoverType.AMNESIAC_LOVER.getKey()));
			roles.add(sb.substring(0, Math.min(30, sb.length())));
		}
		if (game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey()) > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("§3").append(game.getConfig().getLoverCount(LoverType.CURSED_LOVER.getKey())).append("§f ").append(game.translate(LoverType.CURSED_LOVER.getKey()));
			roles.add(sb.substring(0, Math.min(30, sb.length())));
		}
		for (RoleRegister roleRegister : RegisterManager.get().getRolesRegister()) {
			String key = roleRegister.getKey();
			if (game.getConfig().getRoleCount(key) > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("§3").append(game.getConfig().getRoleCount(key)).append("§f ").append(game.translate(roleRegister.getKey()));
				roles.add(sb.substring(0, Math.min(30, sb.length())));
			}
		}

		if (!roles.isEmpty()) {

			int inf= 6*((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())%30/5);
			int total = (int) Math.ceil(roles.size()/6f);

			if(inf<roles.size()){

				roles=roles.subList(inf,Math.min(roles.size(),inf+6));
				String up = game.translate("werewolf.score_board.composition");
				roles.add(0,up.substring(0,Math.min(30,up.length())));


				String down = game.translate("werewolf.score_board.page", inf / 6 + 1, total);

				roles.add(roles.size(), down.substring(0, Math.min(30, down.length())));

			} else roles.clear();
		}
	}

	public void getKillCounter() {

		List<IPlayerWW> topKillers = game.getPlayersWW().stream()
				.sorted(Comparator.comparingInt(value -> value.getPlayersKills().size()))
				.collect(Collectors.toList());

		scoreboard3.add(game.translate("werewolf.score_board.score")
				.substring(0, Math.min(30, game.translate("werewolf.score_board.score").length())));

		for (int i = 0; i < Math.min(game.getPlayersWW().size(), 10); i++) {

			scoreboard3.add(topKillers.get(i).getName() + "§3 " +topKillers.get(i).getPlayersKills().size());
		}
		String line = game.translate("werewolf.score_board.game_name");
		scoreboard3.add(line.substring(0, Math.min(30, line.length())));
		line = game.translate("werewolf.score_board.name", game.getGameName());
		scoreboard3.add(line.substring(0, Math.min(30, line.length())));
	}






	@Override
	public void updateBoard() {

		if(game.isState(StateGame.END) && this.scoreboard3.isEmpty()){
			this.getKillCounter();
		}

		if (Bukkit.getOnlinePlayers().size() == 0) return;

		roles.clear();

		if (!game.getConfig().isConfigActive(ConfigBase.HIDE_COMPOSITION.getKey())
				&& TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60 >= 30) {
			updateScoreBoardRole();
		}

		if (roles.isEmpty()) {
			if (game.isState(StateGame.LOBBY)) {
				updateScoreBoard1();
			} else {
				updateGlobalScoreBoard2();
			}
		}

		String bot = "";

		if (game.isState(StateGame.START) || game.isState(StateGame.GAME)) {
			bot = game.translate("werewolf.tab.timer", Utils.conversion(game.getTimer()), day, dayState);
		}

		bot += game.translate("werewolf.tab.bot");

		for (FastBoard board : game.getBoards().values()) {

			VersionUtils.getVersionUtils().sendTabTitle(board.getPlayer(),
					game.translate("werewolf.tab.top"),
					bot);

			if (game.isState(StateGame.END)) {
				board.updateLines(scoreboard3);
			} else if (!roles.isEmpty()) {
				board.updateLines(roles);
			} else if (game.isState(StateGame.LOBBY)) {
				board.updateLines(scoreboard1);
			} else {
				updateScoreBoard2(board);
			}
		}

	}
}