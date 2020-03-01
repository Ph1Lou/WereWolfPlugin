package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;




public class AutoStartLG extends BukkitRunnable{
	
	private final MainLG main;
	
	
	public AutoStartLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public void run() {

		
		World world = Bukkit.getWorld("world");
		WorldBorder wb = world.getWorldBorder();
		long time = world.getTime();

		main.score.updateBoard(); 
		main.prox_lg.sister_proximity();
		main.prox_lg.renard_proximity();
		main.death_manage.deathtimer();
		main.role_manage.frereLife();
		
		if (main.score.getTimer()==main.config.value.get(TimerLG.ROLE_DURATION)) {

			main.setState(StateLG.LG);
			main.role_manage.repartitionRolesLG();
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.EXPLODE,1,20);
			}
		}
		if (main.score.getTimer()==main.config.value.get(TimerLG.PVP)) {
			world.setPVP(true);
			Bukkit.broadcastMessage(main.text.getText(6));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.DONKEY_ANGRY,1,20);
			}
		}
		
		if(main.score.getTimer()==main.config.value.get(TimerLG.MASTER_DURATION) + main.config.value.get(TimerLG.ROLE_DURATION)) {

			main.role_manage.auto_master();

		}
		if(main.score.getTimer()==main.config.value.get(TimerLG.COUPLE_DURATION) + main.config.value.get(TimerLG.ROLE_DURATION)) {

			main.couple_manage.auto_couple();

		}
		if(main.score.getTimer()==main.config.value.get(TimerLG.ANGE_DURATION) + main.config.value.get(TimerLG.ROLE_DURATION)) {

			main.role_manage.auto_ange() ;

		}
		
		if(main.config.tool_switch.get(ToolLG.LG_LIST) && main.score.getTimer()==main.config.value.get(TimerLG.LG_LIST)) {
			
			main.role_manage.lgList();
			
		}
		
		
		if(main.score.getTimer()>main.config.value.get(TimerLG.BORDER_BEGIN) ) {
			
			if(wb.getSize()==main.config.border_value.get(BorderLG.BORDER_MAX) && wb.getSize()!=main.config.border_value.get(BorderLG.BORDER_MIN)) {
				Bukkit.broadcastMessage(main.text.getText(7));
				for(Player p:Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH,1,20);
				}
			}
			if(wb.getSize()>main.config.border_value.get(BorderLG.BORDER_MIN)) {
				wb.setSize(wb.getSize()-0.5);
				wb.setWarningDistance((int) (wb.getSize()/7));
			}
		}
		
		if(main.score.getTimer()==main.config.value.get(TimerLG.DIGGING)) {
			
			Bukkit.broadcastMessage(main.text.getText(8));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ANVIL_BREAK,1,20);
			}
		}
		
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
		
		
		if (main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2)==0  && !main.isDay(Day.DAY)) {
			
			main.setDay(Day.DAY);
			world.setTime(0);
			if(main.config.tool_switch.get(ToolLG.VOTE) && main.score.getPlayerSize()<main.config.getPlayerRequiredVoteEnd()) {
				
				main.config.tool_switch.put(ToolLG.VOTE,false);
				Bukkit.broadcastMessage(main.text.getText(9));
			}
			main.cycle.jour();
		}
		
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.VOTE_DURATION) ){
			if(main.config.tool_switch.get(ToolLG.VOTE) && main.score.getTimer()-main.config.value.get(TimerLG.VOTE_DURATION)>=main.config.value.get(TimerLG.VOTE_BEGIN)) {
				main.cycle.prevoteresult();
			}
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.VOTE_DURATION)+main.config.value.get(TimerLG.CITIZEN_DURATION) ){
			main.vote.showresultatvote(main.vote.getResult());
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.POWER_DURATION) ){
			main.cycle.finselection();
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2) == main.config.value.get(TimerLG.DAY_DURATION)*2-30 ){
			main.cycle.prejour();
		}

		if (main.score.getTimer()%(main.config.value.get(TimerLG.DAY_DURATION)*2)==main.config.value.get(TimerLG.DAY_DURATION) && !main.isDay(Day.NIGHT)) {
			main.setDay(Day.NIGHT);
			world.setTime(12000);
			main.cycle.nuit();
		}
			
		world.setTime((long) (time+20*(600f/main.config.value.get(TimerLG.DAY_DURATION)-1)));
		
		if(main.isState(StateLG.FIN)) {
			cancel();
		}
		
		main.score.addTimer();
	}

}

