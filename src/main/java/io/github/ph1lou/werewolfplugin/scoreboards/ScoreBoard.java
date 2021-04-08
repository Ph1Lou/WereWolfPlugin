package io.github.ph1lou.werewolfplugin.scoreboards;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.IModerationManager;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.IScoreBoard;
import io.github.ph1lou.werewolfapi.enums.ConfigsBase;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.LoverType;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.enums.TimersBase;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfapi.utils.Utils;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.RegisterManager;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ScoreBoard implements IScoreBoard, Listener {

	private final GameManager game;
	private int group_size = 5;
	private int player = 0;
	private int timer = 0;
	private int role = 0;
	private int day;
	private String dayState;
	private final List<String> scoreboard1 = new ArrayList<>();
	private final List<String> scoreboard2 = new ArrayList<>();
	private final List<String> scoreboard3 = new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private final List<UUID> kill_score = new ArrayList<>();

	public ScoreBoard(GameManager game) {
		this.game = game;
	}

	public void updateScoreBoard1() {

		scoreboard1.clear();

		for (String line : game.translateArray("werewolf.score_board.scoreboard_1")) {

			line = line.replace("&players&", String.valueOf(player));
			line = line.replace("&roles&", String.valueOf(role));
			line = line.replace("&max&", String.valueOf(game.getConfig().getPlayerMax()));
			line = line.substring(0, Math.min(30, line.length()));
			scoreboard1.add(line);
		}
		String line = game.translate("werewolf.score_board.game_name");
		scoreboard1.add(line.substring(0, Math.min(30, line.length())));
		line = game.translate("werewolf.score_board.name", game.getGameName());
		scoreboard1.add(line.substring(0, Math.min(30, line.length())));
	}
	
	public void updateScoreBoard2(FastBoard board) {

		UUID playerUUID = board.getPlayer().getUniqueId();
		List<String> score = new ArrayList<>(scoreboard2);
		IPlayerWW playerWW = game.getPlayerWW(playerUUID);
		IModerationManager moderationManager = game.getModerationManager();
		String role;
		if (playerWW != null) {

			if (!playerWW.isState(StatePlayer.DEATH)) {

				if (!game.isState(StateGame.GAME)) {
					role = Utils.conversion(game.getConfig().getTimerValue(TimersBase.ROLE_DURATION.getKey()));
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
		String border_size = String.valueOf(Math.round(wb.getSize()));
		String border;

		if (game.getConfig().getTimerValue(TimersBase.BORDER_BEGIN.getKey()) > 0) {
			border = Utils.conversion(game.getConfig().getTimerValue(TimersBase.BORDER_BEGIN.getKey()));
		} else {
			border = game.translate("werewolf.utils.on");
			if (wb.getSize() > game.getConfig().getBorderMin()) {
				border_size = border_size + " > " + game.getConfig().getBorderMin();
			}
		}

		scoreboard2.clear();

		this.day = timer / game.getConfig().getTimerValue(TimersBase.DAY_DURATION.getKey()) / 2 + 1;
		this.dayState = game.translate(game.isDay(Day.DAY) ? "werewolf.score_board.day" : "werewolf.score_board.night");

		for (String line : game.translateArray("werewolf.score_board.scoreboard_2")) {

			line = line.replace("&timer&", Utils.conversion(timer));
			line = line.replace("&day&", String.valueOf(this.day));
			line = line.replace("&players&", String.valueOf(player));
			line = line.replace("&group&", String.valueOf(group_size));
			line = line.replace("&border&", border);
			line = line.replace("&daystate&", this.dayState);
			line = line.replace("&border_size&", border_size);
			scoreboard2.add(line);
		}

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

	@Override
	public void getKillCounter() {

		for (IPlayerWW playerWW1 : game.getPlayerWW()) {
			int i = 0;
			while (i < kill_score.size() && playerWW1.getNbKill() <
					Objects.requireNonNull(game.getPlayerWW(kill_score.get(i))).getNbKill()) {
				i++;
			}
			kill_score.add(i, playerWW1.getUUID());
		}

		scoreboard3.add(game.translate("werewolf.score_board.score")
				.substring(0, Math.min(30, game.translate("werewolf.score_board.score").length())));

		for (int i = 0; i < Math.min(game.getPlayerWW().size(), 10); i++) {

			IPlayerWW playerWW1 = game.getPlayerWW(kill_score.get(i));

			if (playerWW1 != null) {
				scoreboard3.add(playerWW1.getName() + "§3 " +
						playerWW1.getNbKill());
			}

		}
		String line = game.translate("werewolf.score_board.game_name");
		scoreboard3.add(line.substring(0, Math.min(30, line.length())));
		line = game.translate("werewolf.score_board.name", game.getGameName());
		scoreboard3.add(line.substring(0, Math.min(30, line.length())));

		this.updateBoard();
	}






	@Override
	public void updateBoard() {

		if (Bukkit.getOnlinePlayers().size() == 0) return;

		roles.clear();

		if (!game.getConfig().isConfigActive(ConfigsBase.HIDE_COMPOSITION.getKey())
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
			bot = game.translate("werewolf.tab.timer", Utils.conversion(timer), day, dayState);
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

	@Override
	public int getRole() {
		return role;
	}

	@Override
	public void setRole(int role) {
		this.role = role;
	}

	@Override
	public void addTimer() {
		this.timer++;
	}

	@Override
	public int getPlayerSize() {
		return player;
	}

	@Override
	public void removePlayerSize() {
		this.player = this.player - 1;
	}

	@Override
	public void addPlayerSize() {
		this.player = this.player + 1;
	}

	@Override
	public int getTimer() {
		return timer;
	}

	@Override
	public int getGroup() {
		return this.group_size;
	}

	@Override
	public void setGroup(int group) {
		this.group_size = group;
	}
}