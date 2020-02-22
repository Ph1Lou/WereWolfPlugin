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
	
	private MainLG main;
	
	
	public AutoStartLG(MainLG main) {
		this.main=main;
	}
	
	@Override
	public void run() {

		
		World world = Bukkit.getWorld("world");
		WorldBorder wb = world.getWorldBorder();
		long time =world.getTime();
				
		main.score.updateBoard(); 
		main.proxlg.sister_proximity();
		main.proxlg.renard_proximity();
		main.deathmanage.deathtimer();
		main.couplemanage.couple_life();
		
		if (main.score.getTimer()==main.config.value.get(TimerLG.role)) {

			main.setState(StateLG.LG);
			main.rolemanage.RepartitionrolesLG();
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.EXPLODE,1,20);
			}
		}
		if (main.score.getTimer()==main.config.value.get(TimerLG.pvp)) {
			world.setPVP(true);
			Bukkit.broadcastMessage(main.texte.getText(6));
			for(Player p:Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.DONKEY_ANGRY,1,20);
			}
		}
		
		if(main.score.getTimer()==main.config.value.get(TimerLG.duration) + main.config.value.get(TimerLG.role)) {
			
			main.rolemanage.auto_ange() ;
			main.rolemanage.auto_master();
			main.couplemanage.auto_couple();
			
		}
		
		if(main.config.tool_switch.get(ToolLG.lg_liste) && main.score.getTimer()==main.config.value.get(TimerLG.lg_liste)) {
			
			main.rolemanage.lgliste();
			
		}
		
		
		if(main.score.getTimer()>main.config.value.get(TimerLG.beginning_border) ) {
			
			if(wb.getSize()==main.config.bordurevalue.get(BordureLG.borduremax) && wb.getSize()!=main.config.bordurevalue.get(BordureLG.borduremin)) {
				Bukkit.broadcastMessage(main.texte.getText(7));
				for(Player p:Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH,1,20);
				}
			}
			if(wb.getSize()>main.config.bordurevalue.get(BordureLG.borduremin)) {
				wb.setSize(wb.getSize()-0.5);
				wb.setWarningDistance((int) (wb.getSize()/7));
			}
		}
		
		if(main.score.getTimer()==main.config.value.get(TimerLG.minage)) {
			
			Bukkit.broadcastMessage(main.texte.getText(8));
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
		
		
		if (time<12000  && !main.isDay(Day.DAY)) {
			
			main.setDay(Day.DAY);
			
			if(main.config.tool_switch.get(ToolLG.vote) && main.score.getPlayerSize()<10) {
				
				main.config.tool_switch.put(ToolLG.vote,false);
				Bukkit.broadcastMessage(main.texte.getText(9));
			}
			
			main.cycle.jour();
			
		}
		
		if(main.score.getTimer()%(main.config.value.get(TimerLG.day_duration)*2) == main.config.value.get(TimerLG.vote_duration) ){
			
			
			main.vote.resultatvote();
			
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.day_duration)*2) == main.config.value.get(TimerLG.use_power) ){
			main.cycle.finselection();
		}
		if(main.score.getTimer()%(main.config.value.get(TimerLG.day_duration)*2) == main.config.value.get(TimerLG.day_duration)*2-30 ){
			main.cycle.prejour();
		}
		
		if (time>=12000 && !main.isDay(Day.NIGHT)) {
			main.setDay(Day.NIGHT);
			main.cycle.nuit();
		}
			
		world.setTime((long) (time+20*(600f/main.config.value.get(TimerLG.day_duration)-1))); 
		
		if(main.isState(StateLG.FIN)) {
			cancel();
		}
		
		main.score.addTimer();
	}

}

