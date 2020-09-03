package io.github.ph1lou.werewolfplugin.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.ScoreAPI;
import io.github.ph1lou.werewolfapi.enumlg.State;
import io.github.ph1lou.werewolfapi.enumlg.StateLG;
import io.github.ph1lou.werewolfapi.events.ActionBarEvent;
import io.github.ph1lou.werewolfapi.events.UpdateEvent;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.InvisibleState;
import io.github.ph1lou.werewolfapi.rolesattributs.Roles;
import io.github.ph1lou.werewolfplugin.utils.VersionUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ScoreBoard implements ScoreAPI, Listener {

	private final GameManager game;
	private int group_size = 5;
	private int player = 0;
	private int timer = 0;
	private int role = 0;
	private final List<String> scoreboard1 = new ArrayList<>();
	private final List<String> scoreboard2 = new ArrayList<>();
	private final List<String> scoreboard3 = new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private final List<UUID> kill_score = new ArrayList<>();
	
	public ScoreBoard(GameManager game) {
		this.game=game;
	}
	
	public void updateScoreBoard1() {

		scoreboard1.clear();

		int i =0;

		while(game.getLanguage().containsKey("werewolf.score_board.scoreboard_1."+i)) {
			String line = game.translate("werewolf.score_board.scoreboard_1." + i);
			line = line.replace("&players&", String.valueOf(player));
			line = line.replace("&roles&", String.valueOf(role));
			line = line.replace("&max&", String.valueOf(game.getConfig().getPlayerMax()));
			line = line.substring(0, Math.min(30, line.length()));
			scoreboard1.add(line);
			i++;
		}
		String line = game.translate("werewolf.score_board.game_name");
		scoreboard1.add(line.substring(0, Math.min(30, line.length())));
		line = game.translate("werewolf.score_board.name", game.getConfig().getGameName());
		scoreboard1.add(line.substring(0, Math.min(30, line.length())));
	}
	
	public void updateScoreBoard2(FastBoard board) {

		UUID playerUUID = board.getPlayer().getUniqueId();
		List<String> score = new ArrayList<>(scoreboard2);
		String role;
		if(game.getPlayersWW().containsKey(playerUUID)) {

			PlayerWW plg = game.getPlayersWW().get(playerUUID);

			if(!plg.isState(State.DEATH)) {
				
				if(!game.isState(StateLG.GAME)) {
					role = conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.role_duration"));
				}
				else role=game.translate(plg.getRole().getDisplay());
			}
			else role=game.translate("werewolf.score_board.death");
		}
		else role=game.translate("werewolf.score_board.spectator");

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

		if (game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin") > 0) {
			border = conversion(game.getConfig().getTimerValues().get("werewolf.menu.timers.border_begin"));
		} else {
			border = game.translate("werewolf.utils.on");
			if (wb.getSize() > game.getConfig().getBorderMin()) {
				border_size = border_size + " > " + game.getConfig().getBorderMin();
			}
		}

		scoreboard2.clear();

		int i=0;
		while(game.getLanguage().containsKey("werewolf.score_board.scoreboard_2."+i)){
			String line=game.translate("werewolf.score_board.scoreboard_2."+i);
			line = line.replace("&timer&", conversion(timer));
			line = line.replace("&day&", String.valueOf(timer / game.getConfig().getTimerValues().get("werewolf.menu.timers.day_duration") / 2 + 1));
			line = line.replace("&players&", String.valueOf(player));
			line = line.replace("&group&", String.valueOf(group_size));
			line = line.replace("&border&", border);
			line = line.replace("&border_size&", border_size);
			scoreboard2.add(line);
			i++;
		}
		scoreboard2.add(game.translate("werewolf.score_board.game_name"));
		scoreboard2.add(game.translate("werewolf.score_board.name", game.getConfig().getGameName()));
	}

	private void updateScoreBoardRole(){


		if(game.getConfig().getLoverSize()>0){
			StringBuilder sb = new StringBuilder();
			sb.append("§3").append(game.getConfig().getLoverSize()).append("§r ").append(game.translate("werewolf.role.lover.display"));
			roles.add(sb.toString().substring(0, Math.min(30, sb.length())));
		}
		if(game.getConfig().getAmnesiacLoverSize()>0){
			StringBuilder sb = new StringBuilder();
			sb.append("§3").append(game.getConfig().getAmnesiacLoverSize()).append("§r ").append(game.translate("werewolf.role.amnesiac_lover.display"));
			roles.add(sb.toString().substring(0, Math.min(30, sb.length())));
		}
		if(game.getConfig().getCursedLoverSize()>0){
			StringBuilder sb = new StringBuilder();
			sb.append("§3").append(game.getConfig().getCursedLoverSize()).append("§r ").append(game.translate("werewolf.role.cursed_lover.display"));
			roles.add(sb.toString().substring(0, Math.min(30, sb.length())));
		}
		for (RoleRegister roleRegister:game.getRolesRegister()) {
			String key = roleRegister.getKey();
			if (game.getConfig().getRoleCount().get(key) > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("§3").append(game.getConfig().getRoleCount().get(key)).append("§r ").append(roleRegister.getName());
				roles.add(sb.toString().substring(0, Math.min(30, sb.length())));
			}
		}

		if(!roles.isEmpty()){

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

		for (UUID uuid : game.getPlayersWW().keySet()) {
			int i = 0;
			while (i < kill_score.size() && game.getPlayersWW().get(uuid).getNbKill() < game.getPlayersWW().get(kill_score.get(i)).getNbKill()) {
				i++;
			}
			kill_score.add(i, uuid);
		}

		scoreboard3.add(game.translate("werewolf.score_board.score").substring(0, Math.min(30, game.translate("werewolf.score_board.score").length())));
		for (int i = 0; i < Math.min(game.getPlayersWW().size(), 10); i++) {
			scoreboard3.add(game.getPlayersWW().get(kill_score.get(i)).getName() + "§3 " + game.getPlayersWW().get(kill_score.get(i)).getNbKill());
		}
		String line = game.translate("werewolf.score_board.game_name");
		scoreboard3.add(line.substring(0, Math.min(30, line.length())));
		line = game.translate("werewolf.score_board.name", game.getConfig().getGameName());
		scoreboard3.add(line.substring(0, Math.min(30, line.length())));

		Bukkit.getPluginManager().callEvent(new UpdateEvent());
	}

	public int midDistance(Player player) {

		World world = player.getWorld();
		Location location = player.getLocation();
		location.setY(world.getSpawnLocation().getY());
		int distance = (int) location.distance(world.getSpawnLocation());

		return distance / 300 * 300;
	}

	@Override
	public void actionBar(Player player) {

		UUID playerUUID = player.getUniqueId();

		if (!game.getPlayersWW().containsKey(playerUUID) || !game.getPlayersWW().get(playerUUID).isState(State.ALIVE))
			return;

		StringBuilder stringbuilder = new StringBuilder();
		int d = midDistance(player);
		stringbuilder.append(game.translate("werewolf.action_bar.in_game", d, d + 300, (int) Math.floor(player.getLocation().getY())));
		PlayerWW plg = game.getPlayersWW().get(playerUUID);

		if (plg.isState(State.ALIVE)) {
			for (UUID uuid : plg.getLovers()) {
				Player player1 = Bukkit.getPlayer(uuid);
				if (player1 != null && game.getPlayersWW().get(uuid).isState(State.ALIVE)) {
					stringbuilder.append("§d ").append(game.getPlayersWW().get(uuid).getName()).append(" ").append(updateArrow(player, player1.getLocation()));
				}
			}
			if(plg.getAmnesiacLoverUUID()!=null && plg.getRevealAmnesiacLover()) {
				UUID uuid = plg.getAmnesiacLoverUUID();
				Player player1 = Bukkit.getPlayer(uuid);
				if (player1 != null && game.getPlayersWW().get(uuid).isState(State.ALIVE)) {
					stringbuilder.append("§d ").append(game.getPlayersWW().get(uuid).getName()).append(" ").append(updateArrow(player, player1.getLocation()));
				}
			}
		}
		ActionBarEvent actionBarEvent = new ActionBarEvent(playerUUID, stringbuilder.toString());
		Bukkit.getPluginManager().callEvent(actionBarEvent);

		VersionUtils.getVersionUtils().sendActionBar(player, actionBarEvent.getActionBar());
	}

	@EventHandler
	public void updateBoard(UpdateEvent event) {

		if (Bukkit.getOnlinePlayers().size() == 0) return;

		roles.clear();

		if (!game.getConfig().getConfigValues().get("werewolf.menu.global.hide_composition") && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60 >= 30) {
			updateScoreBoardRole();
		}

		if (roles.isEmpty()) {
			if (game.isState(StateLG.LOBBY)) {
				updateScoreBoard1();
			} else updateGlobalScoreBoard2();
		}

		for (FastBoard board : game.getBoards().values()) {

			if (game.isState(StateLG.END)) {
				board.updateLines(scoreboard3);
			} else if (!roles.isEmpty()) {
				board.updateLines(roles);
			} else if (game.isState(StateLG.LOBBY)) {
				board.updateLines(scoreboard1);
			} else updateScoreBoard2(board);
		}

	}

	@Override
	public String updateArrow(Player player, Location target) {

		Location location = player.getLocation();
		String arrow;
		location.setY(target.getY());
		Vector dirToMiddle = target.toVector().subtract(player.getEyeLocation().toVector()).normalize();
		Integer distance = (int) Math.round(target.distance(location));
		Vector playerDirection = player.getEyeLocation().getDirection();
		double angle = dirToMiddle.angle(playerDirection);
		double det = dirToMiddle.getX() * playerDirection.getZ() - dirToMiddle.getZ() * playerDirection.getX();

		angle=angle*Math.signum(det);

		if (angle>-Math.PI/8 && angle<Math.PI/8) {
			arrow="⬆";
		} else if (angle>-3*Math.PI/8 && angle<-Math.PI/8) {
			arrow="⬈";
		} else if (angle<3*Math.PI/8 && angle>Math.PI/8) {
			arrow="⬉";
		} else if (angle>3*Math.PI/8 && angle<5*Math.PI/8) {
			arrow="←";
		} else if (angle<-3*Math.PI/8 && angle>-5*Math.PI/8) {
			arrow="➡";
		} else if (angle<-5*Math.PI/8 && angle>-7*Math.PI/8) {
			arrow = "⬊";
		} else if (angle > 5 * Math.PI / 8 && angle < 7 * Math.PI / 8) {
			arrow = "⬋";
		} else arrow = "⬇";

		return distance + " §l" + arrow;
	}

	@Override
	public String conversion(int timer) {

		String value;
		float sign = Math.signum(timer);
		timer = Math.abs(timer);

		if (timer % 60 > 9) {
			value = timer % 60 + "s";
		} else value = "0" + timer % 60 + "s";

		if(timer/3600>0) {

			if(timer%3600/60>9) {
				value = timer / 3600 + "h" + timer % 3600 / 60 + "m" + value;
			} else value = timer / 3600 + "h0" + timer % 3600 / 60 + "m" + value;
		} else if (timer / 60 > 0) {
			value = timer / 60 + "m" + value;
		}
		if (sign < 0) value = "-" + value;

		return value;
	}

	@EventHandler
	public void onNameTagUpdate(UpdateNameTagEvent event) {
		ModerationManager moderationManager = game.getModerationManager();

		for (PlayerWW playerWW : game.getPlayersWW().values()) {

			Scoreboard scoreBoard = playerWW.getScoreBoard();

			for (PlayerWW playerWW2 : game.getPlayersWW().values()) {

				String name = playerWW2.getName();

				if (scoreBoard.getTeam(name) == null) {
					scoreBoard.registerNewTeam(name).addEntry(name);
				}

				Team team = scoreBoard.getTeam(name);
				try {
					if (game.getConfig().getScenarioValues().get("werewolf.menu.scenarios.no_name_tag")) {
						VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, false);
					} else
						VersionUtils.getVersionUtils().setTeamNameTagVisibility(team, (!(playerWW2.getRole() instanceof InvisibleState)) || !((InvisibleState) playerWW2.getRole()).isInvisible());
				} catch (Exception ignored) {

				}

			}

			for (UUID uuid : moderationManager.getModerators()) {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) {
					String name3 = player.getName();
					if (scoreBoard.getTeam(name3) == null) {
						scoreBoard.registerNewTeam(name3).addEntry(name3);
					}
				}
			}

			for (UUID uuid : moderationManager.getHosts()) {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) {
					String name3 = player.getName();
					if (scoreBoard.getTeam(name3) == null) {
						scoreBoard.registerNewTeam(name3).addEntry(name3);
					}
				}
			}

			for (Team t : scoreBoard.getTeams()) {

				for (String e : t.getEntries()) {
					Player player = Bukkit.getPlayer(e);
					if (player != null) {
						UUID uuid = player.getUniqueId();
						StringBuilder sb = new StringBuilder();
						if (moderationManager.getHosts().contains(uuid)) {
							sb.append(game.translate("werewolf.commands.admin.host.tag"));
						} else if (moderationManager.getModerators().contains(uuid)) {
							sb.append(game.translate("werewolf.commands.admin.moderator.tag"));
						}
						if (game.getPlayersWW().containsKey(uuid)) {
							Roles roles = game.getPlayersWW().get(uuid).getRole();
							if (roles.isWereWolf() && playerWW.getRole().isWereWolf()) {
								if (game.getConfig().getConfigValues().get("werewolf.menu.global.red_name_tag")) {
									if (game.getConfig().getTimerValues().get("werewolf.menu.timers.werewolf_list") <= 0) {
										sb.append(ChatColor.DARK_RED);
									}
								}
							}
						}
						t.setPrefix(sb.toString());
					}
				}
			}
		}
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

		if (scoreboardManager == null) return;

		Scoreboard scoreboardModerator = scoreboardManager.getNewScoreboard();
		Team hosts = scoreboardModerator.registerNewTeam("hostsSpectator");
		hosts.setPrefix(game.translate("werewolf.commands.admin.host.tag"));
		Team hostsWW = scoreboardModerator.registerNewTeam("hostsWW");
		hostsWW.setPrefix(game.translate("werewolf.commands.admin.host.tag") + ChatColor.DARK_RED);
		Team moderators = scoreboardModerator.registerNewTeam("moderators");
		moderators.setPrefix(game.translate("werewolf.commands.admin.moderator.tag"));
		Team ww = scoreboardModerator.registerNewTeam("ww");
		ww.setPrefix(String.valueOf(ChatColor.DARK_RED));

		Scoreboard scoreboardSpectator = Bukkit.getScoreboardManager().getNewScoreboard();
		Team hostsSpectator = scoreboardSpectator.registerNewTeam("hostsSpectator");
		hostsSpectator.setPrefix(game.translate("werewolf.commands.admin.host.tag"));
		Team moderatorsSpectator = scoreboardSpectator.registerNewTeam("moderators");
		moderatorsSpectator.setPrefix(game.translate("werewolf.commands.admin.moderator.tag"));


		for (Player player : Bukkit.getOnlinePlayers()) {

			UUID uuid = player.getUniqueId();
			String playerName = player.getName();

			if (moderationManager.getHosts().contains(uuid)) {
				if (game.getPlayersWW().containsKey(uuid) && game.getPlayersWW().get(uuid).getRole().isWereWolf()) {
					hostsWW.addEntry(playerName);
				} else hosts.addEntry(playerName);
				hostsSpectator.addEntry(playerName);
			} else if (moderationManager.getModerators().contains(uuid)) {
				moderators.addEntry(playerName);
				moderatorsSpectator.addEntry(playerName);
			} else if (game.getPlayersWW().containsKey(uuid) && game.getPlayersWW().get(uuid).getRole().isWereWolf()) {
				ww.addEntry(playerName);
			}
		}


		for (Player player : Bukkit.getOnlinePlayers()) {
			if (moderationManager.getModerators().contains(player.getUniqueId())) {
				player.setScoreboard(scoreboardModerator);
			} else if (!game.getPlayersWW().containsKey(player.getUniqueId())) {
				player.setScoreboard(scoreboardSpectator);
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
	public int getGroup() {
		return this.group_size;
	}

	@Override
	public void setGroup(int group) {
		this.group_size = group;
	}

}