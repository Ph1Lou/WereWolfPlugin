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
	private String[] scoreboard1;
	private String[] scoreboard2;
	public final List<String> scoreboard3=new ArrayList<>();
	private List<String> roles = new ArrayList<>();
	private final List<String> kill_score = new ArrayList<>();
	
	public ScoreBoardLG(MainLG main) {
		this.main=main;
	}
	
	public void updateScoreBoard1() {
		
		String[] score =main.text.getScoreBoard(0).clone();
		score[5]=String.format(score[5],host);
		score[3]=String.format(score[3],Bukkit.getOnlinePlayers().size(),role);
		
		for(int i=0;i<score.length;i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(score[i]);
			score[i]=sb.toString().substring(0,Math.min(30,sb.length()));
		}
		scoreboard1=score;
	}
	
	public void updateScoreBoard2(FastBoard board) {

		String playername = board.getPlayer().getName();
		String[] score = scoreboard2.clone();

		if(main.playerlg.containsKey(playername)) {
			
			if(!main.playerlg.get(playername).isState(State.MORT)) {
				
				if(!main.isState(StateLG.LG)) {
					score[1]=String.format(score[1], conversion(main.config.value.get(TimerLG.ROLE_DURATION)));
				}
				else score[1]=main.text.translaterole.get(main.playerlg.get(playername).getRole());
			}
			else score[1]=main.text.getText(45);
		}
		else score[1]=main.text.getText(46);
		StringBuilder sb = new StringBuilder(score[1]);
		score[1]=sb.toString().substring(0,Math.min(30,sb.length()));
		board.updateLines(score);
	}

	private void updateGlobalScoreBoard2(){

		WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();
		String[] score = main.text.getScoreBoard(1).clone();
		score[3]=String.format(score[3],conversion(timer));
		score[4]=String.format(score[4],timer/main.config.value.get(TimerLG.DAY_DURATION)/2);
		score[5]=String.format(score[5],player);
		score[6]=String.format(score[6], group_size);
		score[9]=String.format(score[9],Math.round(wb.getSize()));
		score[11]=String.format(score[11],host);

		if(main.config.value.get(TimerLG.BORDER_BEGIN)>0) {
			score[8] = String.format(score[8], conversion(main.config.value.get(TimerLG.BORDER_BEGIN)));
		}
		else {
			score[8] = String.format(score[8],main.text.getText(80));
			if(wb.getSize()>main.config.border_value.get(BorderLG.BORDER_MIN)){
				score[9]=score[9]+" > "+main.config.border_value.get(BorderLG.BORDER_MIN);
			}
		}

		for(int i=0;i<score.length;i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(score[i]);
			score[i]=sb.toString().substring(0,Math.min(30,sb.length()));
		}
		scoreboard2=score;
	}

	private void updateScoreBoardRole(){

		roles.clear();
		for(RoleLG role:RoleLG.values()) {
			if(main.config.role_count.get(role)>0) {
				StringBuilder sb = new StringBuilder();
				sb.append("§3").append(main.config.role_count.get(role)).append("§r ").append(main.text.translaterole.get(role));
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
		
		for(String p:main.playerlg.keySet()) {
			int i =0;
			while(i< kill_score.size() && main.playerlg.get(p).getNbKill()<main.playerlg.get(kill_score.get(i)).getNbKill()) {
				i++;
			}
			kill_score.add(i, p);
		}
		scoreboard3.clear();
		scoreboard3.add(main.text.getText(26).substring(0,Math.min(30,main.text.getText(26).length())));
		for(int i=0;i<Math.min(main.playerlg.size(),10);i++) {
			scoreboard3.add(kill_score.get(i) +"§3 "+main.playerlg.get(kill_score.get(i)).getNbKill());
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
				main.config.tool_switch.put(ToolLG.EVENT_VOYANTE_DEATH,true);
			}
			else {
				for (int i = 0; i<main.eventslg.chest_location.size(); i++) {
					if(!main.eventslg.chest_has_been_open.get(main.eventslg.chest_location.get(i))) {
						stringbuilder.append("§a");
					}
					else stringbuilder.append("§6");
					stringbuilder.append(updateArrow(player, main.eventslg.chest_location.get(i))).append(" ");
				}
			}
		}
		if(main.playerlg.containsKey(player.getName()) && main.playerlg.get(player.getName()).isState(State.LIVING)) {
			
			for (String p:main.playerlg.get(player.getName()).getCouple()) {
				if(Bukkit.getPlayer(p)!=null) {
					stringbuilder.append("§d ").append(p).append(" ").append(updateArrow(player, Bukkit.getPlayer(p).getLocation()));
				}
			}
			if(main.playerlg.get(player.getName()).isRole(RoleLG.ANGE_GARDIEN)){
				for (String p:main.playerlg.get(player.getName()).getAffectedPlayer()) {
					if(main.playerlg.get(p).isState(State.LIVING) && Bukkit.getPlayer(p)!=null) {
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

		String valeur;
		float sign=Math.signum(timer);
		timer=Math.abs(timer);

		if(timer%60>9) {
			valeur=timer%60+"s";
		}
		else valeur="0"+timer%60+"s";

		if(timer/3600>0) {

			if(timer%3600/60>9) {
				valeur = timer/3600+"h"+timer%3600/60+"m"+valeur;
			}
			else valeur = timer/3600+"h0"+timer%3600/60+"m"+valeur;
		}

		else if (timer/60>0){
			valeur = timer/60+"m"+valeur;
		}
		if(sign<0) valeur="-"+valeur;

		return valeur;
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

    public void setGroup(int groupe) {
		this.group_size =groupe;
    }
}