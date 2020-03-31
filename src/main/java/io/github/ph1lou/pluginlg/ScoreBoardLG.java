package io.github.ph1lou.pluginlg;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ScoreBoardLG {

	final MainLG main;
	private int group_size =5;
	private int player=0;
	private int timer=0;
	private String host="";
	private int role=0;
	private List<String> scoreboard1;
	private List<String> scoreboard2;
	public final List<String> scoreboard3=new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private final List<String> kill_score = new ArrayList<>();
	
	public ScoreBoardLG(MainLG main) {
		this.main=main;
	}
	
	public void updateScoreBoard1() {

		scoreboard1 = new ArrayList<>(main.text.getScoreBoard1());
		scoreboard1.set(5,String.format(scoreboard1.get(5),host));
		scoreboard1.set(3,String.format(scoreboard1.get(3),Bukkit.getOnlinePlayers().size(),role));
		
		for(int i=0;i<scoreboard1.size();i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(scoreboard1.get(i));
			scoreboard1.set(i,sb.toString().substring(0,Math.min(30,sb.length())));
		}
	}
	
	public void updateScoreBoard2(FastBoard board) {

		String playername = board.getPlayer().getName();
		List<String> score = new ArrayList<>(scoreboard2);
		
		if(main.playerLG.containsKey(playername)) {
			
			if(!main.playerLG.get(playername).isState(State.MORT)) {
				
				if(!main.isState(StateLG.LG)) {
					score.set(1,String.format(score.get(1), conversion(main.config.value.get(TimerLG.ROLE_DURATION))));
				}
				else score.set(1,main.text.translateRole.get(main.playerLG.get(playername).getRole()));
			}
			else score.set(1,main.text.getText(45));
		}
		else score.set(1,main.text.getText(46));
		StringBuilder sb = new StringBuilder(score.get(1));
		score.set(1,sb.toString().substring(0,Math.min(30,sb.length())));
		board.updateLines(score);
	}

	private void updateGlobalScoreBoard2(){

		WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
		scoreboard2 = new ArrayList<>(main.text.getScoreBoard2());
		scoreboard2.set(3,String.format(scoreboard2.get(3),conversion(timer)));
		scoreboard2.set(4,String.format(scoreboard2.get(4),timer/main.config.value.get(TimerLG.DAY_DURATION)/2));
		scoreboard2.set(5,String.format(scoreboard2.get(5),player));
		scoreboard2.set(6,String.format(scoreboard2.get(6), group_size));
		scoreboard2.set(9,String.format(scoreboard2.get(9),Math.round(wb.getSize())));
		scoreboard2.set(11,String.format(scoreboard2.get(11),host));

		if(main.config.value.get(TimerLG.BORDER_BEGIN)>0) {
			scoreboard2.set(8,String.format(scoreboard2.get(8), conversion(main.config.value.get(TimerLG.BORDER_BEGIN))));
		}
		else {
			scoreboard2.set(8,String.format(scoreboard2.get(8),main.text.getText(80)));
			if(wb.getSize()>main.config.border_value.get(BorderLG.BORDER_MIN)){
				scoreboard2.set(9,scoreboard2.get(9)+" > "+main.config.border_value.get(BorderLG.BORDER_MIN));
			}
		}

		for(int i=0;i<scoreboard2.size();i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(scoreboard2.get(i));
			scoreboard2.set(i,sb.toString().substring(0,Math.min(30,sb.length())));
		}
	}

	private void updateScoreBoardRole(){

		roles.clear();
		for(RoleLG role:RoleLG.values()) {
			if(main.config.role_count.get(role)>0) {
				StringBuilder sb = new StringBuilder();
				sb.append("§3").append(main.config.role_count.get(role)).append("§r ").append(main.text.translateRole.get(role));
				roles.add(sb.toString().substring(0,Math.min(30,sb.length())));
			}
		}
		if(!roles.isEmpty()){

			int inf= 6*((int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())%30/5);
			if(inf<roles.size()){
				int total=roles.size()/6;
				if(roles.size()%6!=0){
					total++;
				}
				StringBuilder sb = new StringBuilder(main.text.getText(24));
				roles.add(inf,sb.toString().substring(0,Math.min(30,sb.length())));
				sb.delete(0,sb.length());
				sb.append(String.format(main.text.getText(25),inf/6+1,total));
				roles.add(Math.min(roles.size(),inf+7),sb.toString().substring(0,Math.min(30,sb.length())));
				roles=roles.subList(inf,Math.min(roles.size(),inf+8));
			}
			else roles.clear();
		}
	}
	
	public void getKillCounter() {
		
		for(String p:main.playerLG.keySet()) {
			int i =0;
			while(i< kill_score.size() && main.playerLG.get(p).getNbKill()<main.playerLG.get(kill_score.get(i)).getNbKill()) {
				i++;
			}
			kill_score.add(i, p);
		}
		scoreboard3.clear();
		scoreboard3.add(main.text.getText(26).substring(0,Math.min(30,main.text.getText(26).length())));
		for(int i = 0; i<Math.min(main.playerLG.size(),10); i++) {
			scoreboard3.add(kill_score.get(i) +"§3 "+main.playerLG.get(kill_score.get(i)).getNbKill());
		}
	}
		
	public int midDistance(Player player) {
		
		World world = player.getWorld();
		Location location = player.getLocation();
		location.setY(world.getSpawnLocation().getY());
		int distance= (int) location.distance(world.getSpawnLocation());
		
		return distance/300*300;
	}
	
	void actionBar(Player player) {

		StringBuilder stringbuilder=new StringBuilder();
		int d = midDistance(player);
		stringbuilder.append(String.format(main.text.getText(78),d,d+300,(int) Math.floor(player.getLocation().getY())));

		if (!main.eventslg.chest_has_been_open.isEmpty()) {
				
			if(!main.eventslg.chest_has_been_open.containsValue(false)) {
				main.eventslg.chest_location.clear();
				main.eventslg.chest_has_been_open.clear();
				Bukkit.broadcastMessage(main.text.getText(165));
				main.config.tool_switch.put(ToolLG.EVENT_SEER_DEATH,true);
			}
			else {
				for (int i = 0; i<main.eventslg.chest_location.size(); i++) {
					if(!main.eventslg.chest_has_been_open.get(main.eventslg.chest_location.get(i))) {
						stringbuilder.append("§a");
					}
					else stringbuilder.append("§6");
					stringbuilder.append(" ").append(updateArrow(player, main.eventslg.chest_location.get(i)));
				}
			}
		}
		if(main.playerLG.containsKey(player.getName()) && main.playerLG.get(player.getName()).isState(State.LIVING)) {
			
			for (String p:main.playerLG.get(player.getName()).getCouple()) {
				if(Bukkit.getPlayer(p)!=null) {
					stringbuilder.append("§d ").append(p).append(" ").append(updateArrow(player, Bukkit.getPlayer(p).getLocation()));
				}
			}
			if(main.playerLG.get(player.getName()).isRole(RoleLG.ANGE_GARDIEN)){
				for (String p:main.playerLG.get(player.getName()).getAffectedPlayer()) {
					if(main.playerLG.get(p).isState(State.LIVING) && Bukkit.getPlayer(p)!=null) {
						stringbuilder.append("§1 ").append(p).append(" ").append(updateArrow(player, Bukkit.getPlayer(p).getLocation()));
					}
				}
			}
		}
		if(stringbuilder.length()>0) {
			Title.sendActionBar(player, stringbuilder.toString());
		}
	}
		
	public void updateBoard() {

		if(main.config.tool_switch.get(ToolLG.COMPO_VISIBLE)  && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())%60>=30){
			updateScoreBoardRole();
		}
		else roles.clear();

		if(roles.isEmpty()){
			if(main.isState(StateLG.LOBBY)) updateScoreBoard1();
			else updateGlobalScoreBoard2();
		}

		for (FastBoard board : main.boards.values()) {

			if(main.isState(StateLG.FIN)) {
				board.updateLines(scoreboard3);
			}
			else if(!roles.isEmpty()){
				board.updateLines(roles);
			}
			else if(main.isState(StateLG.LOBBY)) {
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
		
		if(main.config.tool_switch.get(ToolLG.AUTO_GROUP) && player<= group_size *3 && group_size >3) {
			group_size--;
			Bukkit.broadcastMessage(String.format(main.text.getText(137), group_size));
			for (Player player:Bukkit.getOnlinePlayers()) {
				Title.sendTitle(player,20,60, 20,main.text.getText(138),String.format( main.text.getText(139),main.score.getGroup()));
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

	public void setHost(String host) {
		this.host = host;
	}

	public void addTimer() {
		this.timer++;
	}
	
	public int getPlayerSize() {
		return player;
	}

	public void removePlayerSize() {
		this.player = this.player-1;
	}
	
	public void addPlayerSize() {
		this.player = this.player+1;
	}
	
	public int getGroup() {
		return this.group_size;
	}

    public void setGroup(int group) {
		this.group_size =group;
    }
}