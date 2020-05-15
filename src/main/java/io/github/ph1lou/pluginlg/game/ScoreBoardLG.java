package io.github.ph1lou.pluginlg.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.classesroles.AffectedPlayers;
import io.github.ph1lou.pluginlg.classesroles.neutralroles.Angel;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Trapper;
import io.github.ph1lou.pluginlg.utils.Title;
import io.github.ph1lou.pluginlgapi.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ScoreBoardLG {

	final GameManager game;
	private int group_size =5;
	private int player=0;
	private int timer=0;
	private int role=0;
	private List<String> scoreboard1;
	private List<String> scoreboard2;
	private final List<String> scoreboard3=new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private final List<UUID> kill_score = new ArrayList<>();
	
	public ScoreBoardLG(GameManager game) {
		this.game=game;
	}
	
	public void updateScoreBoard1() {

		scoreboard1 = new ArrayList<>();

		int i =0;

		while(game.language.containsKey("werewolf.score_board.scoreboard_1."+i)) {
			String line = game.translate("werewolf.score_board.scoreboard_1." + i);
			line = line.replace("&players&", String.valueOf(player));
			line = line.replace("&roles&", String.valueOf(role));
			line = line.replace("&max&", String.valueOf(game.getPlayerMax()));
			line = line.substring(0, Math.min(30, line.length()));
			scoreboard1.add(line);
			i++;
		}
		String line=game.translate("werewolf.score_board.game_name");
		scoreboard1.add(line.substring(0,Math.min(30,line.length())));
		line=game.translate("werewolf.score_board.name",game.getGameName());
		scoreboard1.add(line.substring(0,Math.min(30,line.length())));
	}
	
	public void updateScoreBoard2(FastBoard board) {

		UUID playerUUID = board.getPlayer().getUniqueId();
		List<String> score = new ArrayList<>(scoreboard2);
		String role;
		if(game.playerLG.containsKey(playerUUID)) {

			PlayerLG plg = game.playerLG.get(playerUUID);

			if(!plg.isState(State.DEATH)) {
				
				if(!game.isState(StateLG.GAME)) {

					if (game.config.isTrollSV()) {
						role= conversion(game.config.getTimerValues().get(TimerLG.ROLE_DURATION) - 120);
					} else role= conversion(game.config.getTimerValues().get(TimerLG.ROLE_DURATION));
				}
				else if (game.config.isTrollSV()){
					role = game.translate(RoleLG.VILLAGER.getKey());
				}
				else role=plg.getRole().getDisplay();
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

		WorldBorder wb = game.getWorld().getWorldBorder();
		String border_size=String.valueOf(Math.round(wb.getSize()));
		String border;

		if (game.config.getTimerValues().get(TimerLG.BORDER_BEGIN) > 0) {
			border=conversion(game.config.getTimerValues().get(TimerLG.BORDER_BEGIN));
		} else {
			border= game.translate("werewolf.utils.on");
			if (wb.getSize() > game.config.getBorderMin()) {
				border_size= border_size+" > " + game.config.getBorderMin();
			}
		}

		scoreboard2 = new ArrayList<>();

		int i=0;
		while(game.language.containsKey("werewolf.score_board.scoreboard_2."+i)){
			String line=game.translate("werewolf.score_board.scoreboard_2."+i);
			line=line.replace("&timer&",conversion(timer));
			line=line.replace("&day&",String.valueOf(timer / game.config.getTimerValues().get(TimerLG.DAY_DURATION) / 2 + 1));
			line=line.replace("&players&",String.valueOf(player));
			line=line.replace("&group&",String.valueOf(group_size));
			line=line.replace("&border&",border);
			line=line.replace("&border_size&",border_size);
			scoreboard2.add(line);
			i++;
		}
		scoreboard2.add(game.translate("werewolf.score_board.game_name"));
		scoreboard2.add(game.translate("werewolf.score_board.name",game.getGameName()));
	}

	private void updateScoreBoardRole(){

		roles.clear();
		for(RoleLG role:RoleLG.values()) {
			if (game.config.getRoleCount().get(role) > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("§3").append(game.config.getRoleCount().get(role)).append("§r ").append(game.translate(role.getKey()));
				roles.add(sb.toString().substring(0, Math.min(30, sb.length())));
			}
		}
		if(!roles.isEmpty()){

			int inf= 6*((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())%30/5);
			if(inf<roles.size()){
				int total=roles.size()/6;
				if(roles.size()%6!=0){
					total++;
				}
				StringBuilder sb = new StringBuilder(game.translate("werewolf.score_board.composition"));
				roles.add(inf,sb.toString().substring(0,Math.min(30,sb.length())));
				sb.delete(0,sb.length());
				sb.append(game.translate("werewolf.score_board.page",inf/6+1,total));
				roles.add(Math.min(roles.size(),inf+7),sb.toString().substring(0,Math.min(30,sb.length())));
				roles=roles.subList(inf,Math.min(roles.size(),inf+8));
			}
			else roles.clear();
		}
	}
	
	public void getKillCounter() {
		
		for(UUID uuid:game.playerLG.keySet()) {
			int i =0;
			while(i< kill_score.size() && game.playerLG.get(uuid).getNbKill()<game.playerLG.get(kill_score.get(i)).getNbKill()) {
				i++;
			}
			kill_score.add(i, uuid);
		}

		scoreboard3.add(game.translate("werewolf.score_board.score").substring(0,Math.min(30,game.translate("werewolf.score_board.score").length())));
		for(int i = 0; i<Math.min(game.playerLG.size(),8); i++) {
			scoreboard3.add(game.playerLG.get(kill_score.get(i)).getName() +"§3 "+game.playerLG.get(kill_score.get(i)).getNbKill());
		}
		String line=game.translate("werewolf.score_board.game_name");
		scoreboard3.add(line.substring(0,Math.min(30,line.length())));
		line=game.translate("werewolf.score_board.name",game.getGameName());
		scoreboard3.add(line.substring(0,Math.min(30,line.length())));
	}

	public int midDistance(Player player) {

		World world = player.getWorld();
		Location location = player.getLocation();
		location.setY(world.getSpawnLocation().getY());
		int distance = (int) location.distance(world.getSpawnLocation());

		return distance / 300 * 300;
	}

	public void actionBar(Player player) {

		UUID playerUUID=player.getUniqueId();

		if(!game.playerLG.containsKey(playerUUID) || !game.playerLG.get(playerUUID).isState(State.ALIVE)) return;

		StringBuilder stringbuilder = new StringBuilder();
		int d = midDistance(player);
		stringbuilder.append(game.translate("werewolf.action_bar.in_game", d, d + 300, (int) Math.floor(player.getLocation().getY())));

		if (!game.eventslg.chest_has_been_open.isEmpty()) {

			if (!game.eventslg.chest_has_been_open.containsValue(false)) {
				game.eventslg.chest_location.clear();
				game.eventslg.chest_has_been_open.clear();
				Bukkit.broadcastMessage(game.translate("werewolf.event.all_chest_find"));
				game.config.getConfigValues().put(ToolLG.EVENT_SEER_DEATH, true);
			} else {
				for (int i = 0; i < game.eventslg.chest_location.size(); i++) {
					if (!game.eventslg.chest_has_been_open.get(game.eventslg.chest_location.get(i))) {
						stringbuilder.append("§a");
					} else stringbuilder.append("§6");
					stringbuilder.append(" ").append(updateArrow(player, game.eventslg.chest_location.get(i)));
				}
			}
		}
		if (game.playerLG.containsKey(playerUUID)) {

			PlayerLG plg = game.playerLG.get(playerUUID);

			if (plg.isState(State.ALIVE)) {
				for (UUID uuid : plg.getLovers()) {
					if (Bukkit.getPlayer(uuid) != null) {
						stringbuilder.append("§d ").append(game.playerLG.get(uuid).getName()).append(" ").append(updateArrow(player, Bukkit.getPlayer(uuid).getLocation()));
					}
				}
				if(plg.getAmnesiacLoverUUID()!=null && plg.getRevealAmnesiacLover()){
					UUID uuid =plg.getAmnesiacLoverUUID();
					if (Bukkit.getPlayer(uuid) != null) {
						stringbuilder.append("§d ").append(game.playerLG.get(uuid).getName()).append(" ").append(updateArrow(player, Bukkit.getPlayer(uuid).getLocation()));
					}
				}
				if ((plg.getRole() instanceof Angel && ((Angel) plg.getRole()).isChoice(RoleLG.GUARDIAN_ANGEL)) || (plg.getRole() instanceof Trapper && !((Trapper) plg.getRole()).hasPower())) {
					AffectedPlayers role = (AffectedPlayers) plg.getRole();
					for (UUID uuid : role.getAffectedPlayers()) {
						if (game.playerLG.get(uuid).isState(State.ALIVE) && Bukkit.getPlayer(uuid) != null) {
							stringbuilder.append("§b ").append(game.playerLG.get(uuid).getName()).append(" ").append(updateArrow(player, Bukkit.getPlayer(uuid).getLocation()));
						}
					}
				}
			}
		}
		if (stringbuilder.length() > 0) {
			Title.sendActionBar(player, stringbuilder.toString());
		}
	}
		
	public void updateBoard() {

		if (!game.config.getConfigValues().get(ToolLG.HIDE_COMPOSITION) && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60 >= 30) {
			updateScoreBoardRole();
		} else roles.clear();

		if (roles.isEmpty()) {
			if (game.isState(StateLG.LOBBY)) updateScoreBoard1();
			else updateGlobalScoreBoard2();
		}

		for (FastBoard board : game.boards.values()) {

			if(game.isState(StateLG.END)) {
				board.updateLines(scoreboard3);
			}
			else if(!roles.isEmpty()){
				board.updateLines(roles);
			}
			else if(game.isState(StateLG.LOBBY)) {
				board.updateLines(scoreboard1);
			}
			else updateScoreBoard2(board);
		}

	}

	private String updateArrow(Player player, Location target) {

		Location location = player.getLocation();
		String arrow ;
		location.setY(target.getY());
		Vector dirToMiddle = target.toVector().subtract(player.getEyeLocation().toVector()).normalize();
		Integer distance = (int) Math.round(target.distance(location));
		Vector playerDirection = player.getEyeLocation().getDirection();
		double angle = dirToMiddle.angle(playerDirection);
		double det=dirToMiddle.getX()*playerDirection.getZ()-dirToMiddle.getZ()*playerDirection.getX();

		angle=angle*Math.signum(det);

		if (angle>-Math.PI/8 && angle<Math.PI/8) {
			arrow="⬆";
		}
		else if (angle>-3*Math.PI/8 && angle<-Math.PI/8) {
			arrow="⬈";
		}
		else if (angle<3*Math.PI/8 && angle>Math.PI/8) {
			arrow="⬉";
		}
		else if (angle>3*Math.PI/8 && angle<5*Math.PI/8) {
			arrow="←";
		}
		else if (angle<-3*Math.PI/8 && angle>-5*Math.PI/8) {
			arrow="➡";
		}
		else if (angle<-5*Math.PI/8 && angle>-7*Math.PI/8) {
			arrow="⬊";
		}
		else if (angle>5*Math.PI/8 && angle<7*Math.PI/8) {
			arrow="⬋";
		}
		else arrow="⬇";
			
			return distance+" §l"+arrow;
	}

	public String conversion(int timer) {

		String value;
		float sign=Math.signum(timer);
		timer=Math.abs(timer);

		if(timer%60>9) {
			value=timer%60+"s";
		}
		else value="0"+timer%60+"s";

		if(timer/3600>0) {

			if(timer%3600/60>9) {
				value = timer/3600+"h"+timer%3600/60+"m"+value;
			}
			else value = timer/3600+"h0"+timer%3600/60+"m"+value;
		}

		else if (timer/60>0){
			value = timer/60+"m"+value;
		}
		if(sign<0) value="-"+value;

		return value;
	}


	public void groupSizeChange() {

		if (player <= group_size * 3 && group_size > 3) {
			group_size--;

			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(game.translate("werewolf.commands.admin.group.group_change", group_size));
				Title.sendTitle(p, 20, 60, 20, game.translate("werewolf.commands.admin.group.top_title"), game.translate("werewolf.commands.admin.group.bot_title", game.score.getGroup()));
			}
		}
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	
	public int getTimer() {
		return timer;
	}


	public void addTimer() {
		this.timer++;
	}
	
	public int getPlayerSize() {
		return player;
	}

	public void removePlayerSize() {
		this.player = this.player - 1;
	}
	public void addPlayerSize() {
		this.player = this.player+1;
	}
	
	public int getGroup() {
		return this.group_size;
	}

	public void setGroup(int group) {
		this.group_size = group;

	}


	public List<String> getScoreboard3() {
		return scoreboard3;
	}
}