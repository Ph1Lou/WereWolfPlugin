package io.github.ph1lou.pluginlg;

import java.util.ArrayList;
import java.util.List;
import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import fr.mrmicky.fastboard.FastBoard;





public class ScoreBoardLG {

	MainLG main;
	private int groupsize=5;
	private int player=0;
	private int timer=0;
	private String host="";
	private int role=0;
	
	private List<String> killscore = new ArrayList<>();
	
	public ScoreBoardLG(MainLG main) {
		this.main=main;
	}
	
	
	
	public void updateScoreBoard1(FastBoard board) {
		
		String[] score =main.texte.getScoreBoard(0).clone();
		
		score[1]=score[1]+host;
		score[5]=score[5] +Bukkit.getOnlinePlayers().size()+"/"+role;
		
		for(int i=0;i<score.length;i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(score[i]);
			if(sb.length()>30) {
				sb.delete(29,sb.length()-1);
			}
			score[i]=sb.toString();
		}
		board.updateLines(score);
		
	}
	
	
	
	
	public void updateScoreBoard2(FastBoard board) {
		
	
		
		String playername = board.getPlayer().getName();
		World world = board.getPlayer().getWorld();
		WorldBorder wb = world.getWorldBorder();
		String[] score = main.texte.getScoreBoard(1).clone();
		
		score[0]=score[0]+host;
		
		
		if(main.playerlg.containsKey(playername)) {
			
			if(!main.playerlg.get(playername).isState(State.MORT)) {
				
				if(!main.isState(StateLG.LG)) {
					score[1]="§6Rôle "+main.conversion(main.config.value.get(TimerLG.role)-timer);
				}
				else score[1]=main.texte.translaterole.get(main.playerlg.get(playername).getRole());
			}
			else score[1]="§6Vous êtes Mort";
		}
		else score[1]="§6Mode Spectateur";
		if(main.isState(StateLG.TELEPORTATION)) {
			score[2]="§eTéléportation en Cours";
		}
		else score[2]=score[2]+main.conversion(timer);
		
		score[3]=score[3]+timer/main.config.value.get(TimerLG.day_duration)/2;
		score[4]=score[4]+player;
		score[5]=score[5]+groupsize;
	
		if(timer>main.config.value.get(TimerLG.beginning_border) && wb.getSize()>main.config.bordurevalue.get(BordureLG.borduremin)) {
			score[6]=score[6]+Math.round(wb.getSize())+" -> "+main.config.bordurevalue.get(BordureLG.borduremin);
		}
		else score[6]=score[6]+Math.round(wb.getSize());
		
		if(main.config.tool_switch.get(ToolLG.distance_middle)) {
			score[7]=score[7]+middistance(board.getPlayer());
		}
		score[8]=score[8]+Math.floor(board.getPlayer().getLocation().getY());
		
		for(int i=0;i<score.length;i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(score[i]);
			if(sb.length()>30) {
				sb.delete(29,sb.length()-1);
			}
			score[i]=sb.toString();
		}
		board.updateLines(score);
		
		
		
	}
	
	private void updateScoreBoardRole(FastBoard board) {
		
		String[] score = {"","","","","","","","","","","","","","","",""};
		List<RoleLG> roles = new ArrayList<>();
		
		PlayerLG plg = main.playerlg.get(board.getPlayer().getName());
		
		for(RoleLG role:RoleLG.values()) {
			if(main.config.rolecount.get(role)>0) {
				roles.add(role);
			}
		}
		
		if (plg.getCompoTime()>6) {
			for (int i=0;i<roles.size()/2;i++) {
				score[i]="§r"+main.config.rolecount.get(roles.get(i))+" "+main.texte.translaterole.get(roles.get(i));
			}
		}
		else {
			for (int i=roles.size()/2;i<roles.size();i++) {
				score[i-roles.size()/2]="§r"+main.config.rolecount.get(roles.get(i))+" "+main.texte.translaterole.get(roles.get(i));
			}
		}
		
		for(int i=0;i<score.length;i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(score[i]);
			if(sb.length()>30) {
				sb.delete(29,sb.length()-1);
			}
			score[i]=sb.toString();
		}
		board.updateLines(score);
		
		
	}
	
	public void updateScoreBoard3(FastBoard board) {
		
		String[] score = {"","","","","","","","","","",""};
		int n=10;
		if(main.playerlg.size()<10) {
			n=main.playerlg.size();
		}
        
        for(int i=0;i<n;i++) {
        	score[i+1]= "§l"+killscore.get(i) +" :§6§l "+main.playerlg.get(killscore.get(i)).getNbKill();
        }
        
      
		
        board.updateLines(score);
	
	}
	
	public void getKillCounter() {
		
		for(String p:main.playerlg.keySet()) {
			
			int i =0;
			while(i<killscore.size() && main.playerlg.get(p).getNbKill()<main.playerlg.get(killscore.get(i)).getNbKill()) {
				i++;
			}
			killscore.add(i, p);
		}
	}
	
	
		
	public String middistance(Player player) {
		
		World world = player.getWorld();
		Location plocation = player.getLocation();
		String retour;
		plocation.setY(world.getSpawnLocation().getY());
		double distance= plocation.distance(world.getSpawnLocation());
		
		if(distance<300){
			retour="0 à 300 blocs";
		}
		else if(distance<600) {
			retour="300 à 600 blocs";
		}
		else if(distance<900) {
			retour="600 à 900 blocs";
		}
		else if(distance<1200) {
			retour="900 à 1200 blocs";
		}
		else retour="> à 1200 blocs";
		return retour;
	}
	
	private void actionBar(Player player) {
		
		
		
		StringBuilder stringbuilder=new StringBuilder();
		
		if (!main.eventslg.chesthasbeenopen.isEmpty()) {
				
			if(!main.eventslg.chesthasbeenopen.containsValue(false)) {
				
				main.eventslg.chestlocation.clear();
				main.eventslg.chesthasbeenopen.clear();
				Bukkit.broadcastMessage(main.texte.getText(165));
				main.config.tool_switch.put(ToolLG.event_voyante_death,true);
			}
			
			else {
				
				for (int i=0;i<main.eventslg.chestlocation.size();i++) {
					
					if(!main.eventslg.chesthasbeenopen.get(main.eventslg.chestlocation.get(i))) {
						stringbuilder.append("§a").append(updatearrow(player, main.eventslg.chestlocation.get(i))).append(" ");
					}
					else stringbuilder.append("§6").append(updatearrow(player, main.eventslg.chestlocation.get(i))).append(" ");
				}
			}
			
		}
		if(main.playerlg.containsKey(player.getName()) && main.playerlg.get(player.getName()).isState(State.VIVANT)) {
			
			for (String p:main.playerlg.get(player.getName()).getCouple()) {
				if(Bukkit.getPlayer(p)!=null) {
					stringbuilder.append("§d ").append(p).append(" ").append(updatearrow(player, Bukkit.getPlayer(p).getLocation()));
				}
			}
			
		}
		if(stringbuilder.length()>0) {
			Title.sendActionBar(player, stringbuilder.toString());
		}
	}
		
	public void updateBoard() {
		

		for (FastBoard board : main.boards.values()) {
			
			if(main.playerlg.containsKey(board.getPlayer().getName()) && main.playerlg.get(board.getPlayer().getName()).getCompoTime()>0) {
				main.playerlg.get(board.getPlayer().getName()).setCompoTime(main.playerlg.get(board.getPlayer().getName()).getCompoTime()-1);
				updateScoreBoardRole(board);
			}
			else if(main.isState(StateLG.LOBBY)) {
				updateScoreBoard1(board);	
			}
			else if(!main.isState(StateLG.FIN)) {
				
				updateScoreBoard2(board);
				actionBar(board.getPlayer());	
			} 	
			else 	
			    main.score.updateScoreBoard3(board);	
				
		}	
	}

	



	private String updatearrow(Player player,Location cible) {
		
			Location plocation = player.getLocation();
			String flech ;
			plocation.setY(cible.getY());
			Vector dirToMiddle = cible.toVector().subtract(player.getEyeLocation().toVector());
			Integer distance = (int) Math.round(cible.distance(plocation));
			Vector playerDirection = player.getEyeLocation().getDirection();
			double angle = dirToMiddle.angle(playerDirection);
			double det=dirToMiddle.getX()*playerDirection.getZ()-dirToMiddle.getZ()*playerDirection.getX();
			angle=angle*Math.signum(Math.round(det));
			
			if (angle>-Math.PI/6 && angle<Math.PI/6) {
				flech=" §l↑";
			}
			else if (angle>-Math.PI/3 && angle<-Math.PI/6) {
				flech=" §l➚";
			}
			else if (angle<Math.PI/3 && angle>Math.PI/6) {
				flech=" §l↖";
			}
			else if (angle>Math.PI/3 && angle<2*Math.PI/3) {
				flech=" §l←";
			}
			else if (angle<-Math.PI/3 && angle>-2*Math.PI/3) {
				flech=" §l→";
			}
			else if (angle<-2*Math.PI/3 && angle>-5*Math.PI/6) {
				flech=" §l➘";
			}
			else if (angle>2*Math.PI/3 && angle<5*Math.PI/6) {
				flech=" §l↙";
			}
			else flech=" §l↓";	
			
			return distance+flech;
	}
	
	public void groupsizechange() {
		
		if(player<=groupsize*3 && groupsize>3) {
			groupsize--;
			Bukkit.broadcastMessage(main.texte.esthetique("§m", "§2",main.texte.getText(137)+groupsize));
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
	
	
	public int getGroupe() {
		return this.groupsize;
	}




}