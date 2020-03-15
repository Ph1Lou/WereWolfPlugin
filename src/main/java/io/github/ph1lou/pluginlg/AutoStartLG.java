package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class AutoStartLG extends BukkitRunnable{
	
	private final MainLG main;
	int compteur=0;
	
	public AutoStartLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public void run() {

		compteur++;
		for(Player p:Bukkit.getOnlinePlayers()){
			main.score.actionBar(p);
		}

		if(compteur%4!=0) return;


		World world = Bukkit.getWorld("world");
		WorldBorder wb = world.getWorldBorder();
		long time = world.getTime();
		main.optionlg.updateSelectionTimer(main.optionlg.invtimer,main.optionlg.findSelect(main.optionlg.invtimer));
		main.optionlg.updateSelectionBorder(main.optionlg.invborder,main.optionlg.findSelect(main.optionlg.invborder));
		main.score.updateBoard();
		main.prox_lg.sister_proximity();
		main.prox_lg.renard_proximity();
		main.death_manage.deathtimer();
		main.role_manage.brotherLife();
		
		if (main.config.value.get(TimerLG.ROLE_DURATION)==0) {
			main.setState(StateLG.LG);
			main.role_manage.repartitionRolesLG();
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.EXPLODE,1,20);
			}
			main.config.value.put(TimerLG.ROLE_DURATION,-1);
		}
		else main.config.value.put(TimerLG.ROLE_DURATION,main.config.value.get(TimerLG.ROLE_DURATION)-1);


		if (main.config.value.get(TimerLG.PVP)==0) {
			world.setPVP(true);
			Bukkit.broadcastMessage(main.text.getText(6));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.DONKEY_ANGRY,1,20);
			}
			main.config.value.put(TimerLG.PVP,-1);
		}
		else main.config.value.put(TimerLG.PVP,main.config.value.get(TimerLG.PVP)-1);
		
		if(main.config.value.get(TimerLG.ROLE_DURATION)<0) {

			if (main.config.value.get(TimerLG.MASTER_DURATION)==0){
				main.role_manage.auto_master();
				main.config.value.put(TimerLG.MASTER_DURATION,-1);
			}
			else main.config.value.put(TimerLG.MASTER_DURATION,main.config.value.get(TimerLG.MASTER_DURATION)-1);
			if(main.config.value.get(TimerLG.COUPLE_DURATION)==0) {
				main.couple_manage.auto_couple();
				main.config.value.put(TimerLG.COUPLE_DURATION,-1);
			}
			else main.config.value.put(TimerLG.COUPLE_DURATION,main.config.value.get(TimerLG.COUPLE_DURATION)-1);
			if(main.config.value.get(TimerLG.ANGE_DURATION)==0) {
				main.role_manage.auto_ange();
				main.config.value.put(TimerLG.ANGE_DURATION,-1);
			}
			else  main.config.value.put(TimerLG.ANGE_DURATION,main.config.value.get(TimerLG.ANGE_DURATION)-1);
		}
		
		if(main.config.tool_switch.get(ToolLG.LG_LIST)) {
			if(main.config.value.get(TimerLG.LG_LIST)==0){
				main.role_manage.lgList();
				main.config.value.put(TimerLG.LG_LIST,-1);
			}
			else main.config.value.put(TimerLG.LG_LIST,main.config.value.get(TimerLG.LG_LIST)-1);
		}
		
		if(main.config.value.get(TimerLG.BORDER_BEGIN)==0) {

			if(wb.getSize()!=main.config.border_value.get(BorderLG.BORDER_MIN)) {
				Bukkit.broadcastMessage(main.text.getText(7));
				for(Player p:Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH,1,20);
				}
				main.config.value.put(TimerLG.BORDER_BEGIN,-1);
			}
		}
		else {
			main.config.value.put(TimerLG.BORDER_BEGIN,main.config.value.get(TimerLG.BORDER_BEGIN)-1);
			if(main.config.value.get(TimerLG.BORDER_BEGIN)<0){
				main.config.border_value.put(BorderLG.BORDER_MAX, (int) wb.getSize());
				wb.setSize(main.config.border_value.get(BorderLG.BORDER_MIN),(long) (wb.getSize()-main.config.border_value.get(BorderLG.BORDER_MIN))*main.config.value.get(TimerLG.BORDER_DURATION)/100);
			}
		}
		
		if(main.config.value.get(TimerLG.DIGGING)==0) {
			Bukkit.broadcastMessage(main.text.getText(8));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK,1,20);
			}
			main.config.value.put(TimerLG.DIGGING,-1);
		}
		else main.config.value.put(TimerLG.DIGGING,main.config.value.get(TimerLG.DIGGING)-1);
		
		if(main.isDay(Day.NIGHT)) {
			for(Player p:Bukkit.getOnlinePlayers()) {
				if(main.playerlg.containsKey(p.getName())  && main.playerlg.get(p.getName()).isState(State.VIVANT) && !main.playerlg.get(p.getName()).hasPower() && (main.playerlg.get(p.getName()).isRole(RoleLG.LOUP_PERFIDE) || main.playerlg.get(p.getName()).isRole(RoleLG.PETITE_FILLE) )) {
					for(Player p2:Bukkit.getOnlinePlayers()) {
						if(main.playerlg.containsKey(p2.getName())  && main.playerlg.get(p2.getName()).isState(State.VIVANT) && !main.playerlg.get(p2.getName()).hasPower() && !p.equals(p2) && (main.playerlg.get(p2.getName()).isRole(RoleLG.LOUP_PERFIDE) || main.playerlg.get(p2.getName()).isRole(RoleLG.PETITE_FILLE)) ) {
							
							if(main.playerlg.get(p2.getName()).isRole(RoleLG.LOUP_PERFIDE) ) {
								p.playEffect(p2.getLocation(),Effect.STEP_SOUND,Material.REDSTONE_BLOCK);
							}
							if( main.playerlg.get(p2.getName()).isRole(RoleLG.PETITE_FILLE)) {
								p.playEffect(p2.getLocation(),Effect.STEP_SOUND,Material.LAPIS_BLOCK);
							}
						}
					}
				}
			}
		}
		if (main.config.value.get(TimerLG.VOTE_BEGIN)==0) {
			main.config.value.put(TimerLG.VOTE_BEGIN,-1);
		}
		else main.config.value.put(TimerLG.VOTE_BEGIN,main.config.value.get(TimerLG.VOTE_BEGIN)-1);
		
		if (main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2)==0  && !main.isDay(Day.DAY)) {
			
			main.setDay(Day.DAY);
			if(main.config.tool_switch.get(ToolLG.VOTE) && main.score.getPlayerSize()<main.config.getPlayerRequiredVoteEnd()) {
				main.config.tool_switch.put(ToolLG.VOTE,false);
				Bukkit.broadcastMessage(main.text.getText(9));
			}
			main.cycle.day();
		}

		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.VOTE_DURATION) ){
			if(main.config.tool_switch.get(ToolLG.VOTE)) {
				if(!main.vote.templayer.isEmpty()){
					main.cycle.preVoteResult();
				}
			}
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.VOTE_DURATION)+main.config.value.get(TimerLG.CITIZEN_DURATION) ){
			main.vote.showresultatvote(main.vote.getResult());
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.POWER_DURATION) ){
			main.cycle.selectionEnd();
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.DAY_DURATION)*2-30 ){
			main.cycle.preDay();
		}

		if (main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2)==main.config.value.get(TimerLG.DAY_DURATION) && !main.isDay(Day.NIGHT)) {
			main.setDay(Day.NIGHT);
			main.cycle.night();
		}
			
		world.setTime((long) (time+20*(600f/main.config.value.get(TimerLG.DAY_DURATION)-1)));
		
		if(main.isState(StateLG.FIN)) {
			cancel();
		}
		
		main.score.addTimer();
	}

}

