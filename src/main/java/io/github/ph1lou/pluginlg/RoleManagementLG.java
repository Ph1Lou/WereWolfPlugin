package io.github.ph1lou.pluginlg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;





public class RoleManagementLG {
	
	private MainLG main;

	
	public RoleManagementLG(MainLG main) {
		this.main=main;	
	}
	
	public void RepartitionrolesLG() {
		
		List<String> joueurs = new ArrayList<>(main.playerlg.keySet());
		List<RoleLG> config = new ArrayList<>();
		main.config.tool_switch.put(ToolLG.chat,false);
		main.config.rolecount.put(RoleLG.VILLAGEOIS,main.config.rolecount.get(RoleLG.VILLAGEOIS)+joueurs.size()-main.score.getRole());
		for(RoleLG role:RoleLG.values()) {
			for(int i=0;i<main.config.rolecount.get(role);i++) {
				if(!role.equals(RoleLG.COUPLE)) {
					config.add(role);
				}
			}
		}
	
	
		while(!joueurs.isEmpty()) { 
			
			int n =(int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*joueurs.size());
			String playername = joueurs.get(n);
			PlayerLG plg = main.playerlg.get(playername);
			plg.setCamp(config.get(0).getCamp());
			plg.setPower(config.get(0).getPower());
			plg.setRole(config.get(0));
			if(plg.isRole(RoleLG.LOUP_PERFIDE)){
				main.boardlg.registerNewTeam(joueurs.get(n));
				main.boardlg.getTeam(joueurs.get(n)).setPrefix("");
			}
			recover_rolepower(playername);
			config.remove(0);	
			joueurs.remove(n);	
		}
		
		main.check_victory();
	}
	
	public void recover_rolepower(String playername) {
		
		if (Bukkit.getPlayer(playername)==null) return;
		
		Player player = Bukkit.getPlayer(playername);
		PlayerLG plg = main.playerlg.get(playername);
		
		plg.setKit(true);
		
		player.sendMessage(main.texte.esthetique("§m", "§6",main.texte.getText(42)+main.texte.translaterole.get(plg.getRole())+main.texte.getText(43)));

		for(PotionEffectType p:effect_recover(playername)) {
			player.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
		}
		
		for(ItemStack i:main.stufflg.rolestuff.get(plg.getRole())) {
			
			if(player.getInventory().firstEmpty()==-1) {
				player.getWorld().dropItem(player.getLocation(),i);
			}
			else {
				player.getInventory().addItem(i);
				player.updateInventory();
			}
		}
		
		if (plg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
			player.setMaxHealth(30);
			player.setHealth(30);
			if(main.isDay(Day.NIGHT)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
			}
		}	
		else if ((plg.isRole(RoleLG.ASSASSIN) && !main.isDay(Day.NIGHT)) || (plg.isCamp(Camp.LG) && main.isDay(Day.NIGHT))) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,-1,false,false));
		}	
		else if (plg.isRole(RoleLG.ENFANT_SAUVAGE)) {
			player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.poweruse.get(RoleLG.ENFANT_SAUVAGE)+main.conversion(main.config.value.get(TimerLG.maitre_duration))));
		}
		else if (plg.isRole(RoleLG.CUPIDON)) {
			player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.poweruse.get(RoleLG.CUPIDON)+main.conversion(main.config.value.get(TimerLG.couple_duration))));
		}
		else if (plg.isRole(RoleLG.ANGE)) {
			player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.poweruse.get(RoleLG.ANGE)+main.conversion(main.config.value.get(TimerLG.ange_duration))));
		}
		if(plg.isRole(RoleLG.ANGE_DECHU) || plg.isRole(RoleLG.ANGE_GARDIEN) || plg.isRole(RoleLG.ANGE)) {
			player.setMaxHealth(24);
			player.setHealth(24);
		}
		
		
	}
	
	
	
	public List<PotionEffectType> effect_recover(String playername) {
		
		List <PotionEffectType> effect = new ArrayList<>();
		
		if (main.playerlg.get(playername).isRole(RoleLG.VOYANTE) || main.playerlg.get(playername).isRole(RoleLG.VOYANTE_BAVARDE) || main.playerlg.get(playername).isRole(RoleLG.PETITE_FILLE) || main.playerlg.get(playername).isCamp(Camp.LG)) {
			effect.add(PotionEffectType.NIGHT_VISION);
		}
		if((main.playerlg.get(playername).isRole(RoleLG.VOLEUR) || (main.playerlg.get(playername).isRole(RoleLG.ANCIEN))  && main.playerlg.get(playername).hasPower())){
			effect.add(PotionEffectType.DAMAGE_RESISTANCE);
		}
		if(main.playerlg.get(playername).isRole(RoleLG.MINEUR)){
			effect.add(PotionEffectType.FAST_DIGGING);
		}
		/*if (main.playerlg.get(playername).isRole(RoleLG.CORBEAU)) {
			effect.add(PotionEffectType.SLOW_FALLING);
		}*/
		if (main.playerlg.get(playername).isRole(RoleLG.RENARD) || main.playerlg.get(playername).isRole(RoleLG.VILAIN_PETIT_LOUP)) {
			effect.add(PotionEffectType.SPEED);
		}
		
		return effect;
	}
	
	public void thief_recover_role(String killername,String playername){
		
		RoleLG role = main.playerlg.get(playername).getRole();
		
		PlayerLG klg = main.playerlg.get(killername);
		PlayerLG plg = main.playerlg.get(playername);
		
		klg.setRole(role);
		klg.setVoleur(true);
		klg.setPower(plg.hasPower());
		
		if((plg.isCamp(Camp.LG) || plg.isRole(RoleLG.LOUP_GAROU_BLANC)) && !klg.isCamp(Camp.LG)) {
			newLG(killername);
		}
		else if(plg.isCamp(Camp.VILLAGE) && !klg.isCamp(Camp.LG)) {
			klg.setCamp(Camp.VILLAGE);
		}
		
		if (Bukkit.getPlayer(killername)!=null) {
			
			Player killer = Bukkit.getPlayer(killername);
			
			killer.sendMessage(main.texte.esthetique("§m", "§6",main.texte.powerhasbeenuse.get(RoleLG.VOLEUR)+main.texte.translaterole.get(role)+main.texte.getText(43)));
			killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			
			if (klg.isRole(RoleLG.VILAIN_PETIT_LOUP) || klg.isRole(RoleLG.RENARD)) {
				killer.removePotionEffect(PotionEffectType.SPEED);
			}
			
			for(PotionEffectType p:effect_recover(killername)) {
				killer.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
			}
			
			if (klg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
				killer.setMaxHealth(30);
				killer.setHealth(30);
				if(main.isDay(Day.NIGHT)) {
					killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
				}
			}
			else if(klg.isRole(RoleLG.PETITE_FILLE) || klg.isRole(RoleLG.LOUP_PERFIDE)) {
				klg.setPower(true);
			}	
			
			if (klg.isRole(RoleLG.ENFANT_SAUVAGE)) {
				
				if(klg.hasPower()) {
					killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.poweruse.get(RoleLG.ENFANT_SAUVAGE)+main.conversion(main.config.value.get(TimerLG.maitre_duration)-main.score.getTimer()+main.config.value.get(TimerLG.role))));
				}
				else {
					String maitrename = plg.getAffectedPlayer().get(0);
					klg.clearAffectedPlayer();
					plg.clearAffectedPlayer();
					klg.addAffectedPlayer(maitrename);
					main.playerlg.get(maitrename).addDisciple(killername);
					main.playerlg.get(maitrename).removeDisciple(playername);
					
					if(maitrename.equals(killername)) {
						main.rolemanage.newLG(killername);
					}
					else killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.ENFANT_SAUVAGE)+maitrename));
				}
			}
			if (klg.isRole(RoleLG.CUPIDON)) {
				
				if(klg.hasPower()) {
					killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.poweruse.get(RoleLG.CUPIDON)+main.conversion(main.config.value.get(TimerLG.couple_duration)-main.score.getTimer()+main.config.value.get(TimerLG.role))));
				}
				else {
					klg.addAffectedPlayer(plg.getAffectedPlayer().get(0));
					klg.addAffectedPlayer(plg.getAffectedPlayer().get(1));
					plg.clearAffectedPlayer();
					killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.CUPIDON)+klg.getAffectedPlayer().get(0)+" et "+klg.getAffectedPlayer().get(1)));
				}
			}
			if ((klg.isRole(RoleLG.ASSASSIN) && !main.isDay(Day.NIGHT)) || (klg.isCamp(Camp.LG) && main.isDay(Day.NIGHT) )) {	
				killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));

			}
			if (klg.isRole(RoleLG.LOUP_FEUTRE)){
				klg.setCampFeutre(plg.getCampFeutre());
				klg.setRoleFeutre(plg.getRoleFeutre());
				killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.getText(15)+main.texte.translaterole.get(klg.getRole())));
			}
			if (klg.isRole(RoleLG.ANGE)) {
				killer.sendMessage(main.texte.esthetique("§m", "§e",RoleLG.ANGE.getPowerUse()+main.conversion(main.config.value.get(TimerLG.ange_duration)-main.score.getTimer()+main.config.value.get(TimerLG.role))));
				killer.setMaxHealth(24);
			}
			else if (klg.isRole(RoleLG.ANGE_DECHU) || klg.isRole(RoleLG.ANGE_GARDIEN)){
				klg.clearAffectedPlayer();
				klg.addAffectedPlayer(plg.getAffectedPlayer().get(0));
				plg.clearAffectedPlayer();
			

				
				if(main.playerlg.get(klg.getAffectedPlayer().get(0)).isState(State.MORT) || klg.getAffectedPlayer().get(0).equals(killername)) {
					if(klg.isRole(RoleLG.ANGE_DECHU)) {
						killer.setMaxHealth(30);
						killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.getText(48)+klg.getAffectedPlayer().get(0)));
					}
				}
				else {
					killer.setMaxHealth(24);
					killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.getText(49)+klg.getAffectedPlayer().get(0)));		
				}
				
				
				
			}
			if(!plg.getCouple().isEmpty()) {
				
				for(String c:plg.getCouple()) {
					
					if(!klg.getCouple().contains(c)) {
						
						klg.addCouple(c);
						main.playerlg.get(c).addCouple(killername);
						main.playerlg.get(c).removeCouple(playername);
						if(Bukkit.getPlayer(c)!=null) {
							Player pc = Bukkit.getPlayer(c);
							pc.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.COUPLE)+killername+main.texte.getText(11)));	
							pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR,1,20);
						}

						killer.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(RoleLG.COUPLE)+c+main.texte.getText(11)));
						killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR,1,20);
					}
				}
				plg.clearCouple();
				
				for(String cup:main.playerlg.keySet()) {
					if(main.playerlg.get(cup).isRole(RoleLG.CUPIDON) && main.playerlg.get(cup).getAffectedPlayer().contains(playername)) {
						main.playerlg.get(cup).addAffectedPlayer(killername);
						main.playerlg.get(cup).removeAffectedPlayer(playername);
					}
				}
				main.couplemanage.thief_couplerange(killername,playername);
			}
		}
		
		main.deathmanage.mortdefinitive(playername);
	}
	
	public void auto_master() {
		
		for(String playername:main.playerlg.keySet()) {

			if (main.playerlg.get(playername).isState(State.VIVANT) && main.playerlg.get(playername).isRole(RoleLG.ENFANT_SAUVAGE) && main.playerlg.get(playername).hasPower()) {
				
				String maitrename = auto_selec(playername);
				main.playerlg.get(maitrename).addDisciple(playername);
				main.playerlg.get(playername).addAffectedPlayer(maitrename);
				main.playerlg.get(playername).setPower(false);
				if(Bukkit.getPlayer(playername) != null){
					Player player = Bukkit.getPlayer(playername);
					player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.getText(47)+maitrename));
					player.playSound(player.getLocation(), Sound.BAT_IDLE,1,20);
				}
			}
		}
		
	}
	
	public void auto_ange() {
		
		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			if (plg.isState(State.VIVANT)){
				if(plg.isRole(RoleLG.ANGE)){
					plg.setPower(false);
					if(new Random(System.currentTimeMillis()).nextBoolean()){
						plg.setRole(RoleLG.ANGE_DECHU);
					}
					else plg.setRole(RoleLG.ANGE_GARDIEN);

				}
				if (plg.isRole(RoleLG.ANGE_DECHU) || plg.isRole(RoleLG.ANGE_GARDIEN)) {

					String ciblename = auto_selec(playername);
					plg.addAffectedPlayer(ciblename);
					main.playerlg.get(ciblename).addCibleOf(playername);

					if(Bukkit.getPlayer(playername) != null){
						Player player = Bukkit.getPlayer(playername);
						player.sendMessage(main.texte.esthetique("§m", "§e",main.texte.powerhasbeenuse.get(plg.getRole())+ciblename));
						player.playSound(player.getLocation(), Sound.CAT_MEOW,1,20);
					}
				}
			}

		}
		if(!main.isState(StateLG.FIN)) {
			main.check_victory();
		}
		
	}
	
	public String auto_selec(String playername) {
		
		List<String> players = new ArrayList<>();
		for(String p:main.playerlg.keySet()) {
			if(main.playerlg.get(p).isState(State.VIVANT) && !p.equals(playername)) {
				players.add(p);
			}	
		}
		if(players.isEmpty()) {
			return playername;
		}
		return 	players.get((int) Math.floor(new Random(System.currentTimeMillis()).nextFloat()*players.size()));
	}
	
	
	
	public void newLG(String playername) {
		
		if(main.config.tool_switch.get(ToolLG.lg_liste) && main.score.getTimer()>main.config.value.get(TimerLG.lg_liste)) {

			if(main.playerlg.get(playername).isRole(RoleLG.LOUP_PERFIDE)){
				main.boardlg.registerNewTeam(playername);
				main.boardlg.getTeam(playername).setPrefix("§4");
				main.boardlg.getTeam(playername).addEntry(playername);
			}
			else main.boardlg.getTeam("lgteam").addEntry(playername);

			for(String lgname : main.playerlg.keySet()) {
			
				if((main.playerlg.get(lgname).isCamp(Camp.LG) || main.playerlg.get(lgname).isRole(RoleLG.LOUP_GAROU_BLANC) )&& main.playerlg.get(lgname).isState(State.VIVANT) && Bukkit.getPlayer(lgname)!=null ) {
					Player lg1 = Bukkit.getPlayer(lgname);
					lg1.sendMessage(main.texte.getText(50));
					lg1.playSound(lg1.getLocation(),Sound.WOLF_HOWL, 1, 20);
				}
			}
		}
		
		if(!main.playerlg.get(playername).isRole(RoleLG.LOUP_GAROU_BLANC)) {
			main.playerlg.get(playername).setCamp(Camp.LG);
		}


		if(Bukkit.getPlayer(playername)!=null) {
			
			Player player = Bukkit.getPlayer(playername);		
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
			player.sendMessage(main.texte.getText(51));	
			player.setScoreboard(main.boardlg);
			player.playSound(player.getLocation(),Sound.WOLF_HOWL, 1, 20);
			if (main.isDay(Day.NIGHT)) {	
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
			}
		}
		
	}
	
	public void lgliste() {
		
		main.boardlg.getTeam("lgteam").setPrefix("§4");
		for(String lgname : main.playerlg.keySet()) {
			PlayerLG lg = main.playerlg.get(lgname);
			if((lg.isCamp(Camp.LG) || lg.isRole(RoleLG.LOUP_GAROU_BLANC)) && lg.isState(State.VIVANT)) {

				if(lg.isRole(RoleLG.LOUP_PERFIDE)){
					main.boardlg.getTeam(lgname).setPrefix("§4");
					main.boardlg.getTeam(lgname).addEntry(lgname);
				}
				else main.boardlg.getTeam("lgteam").addEntry(lgname);
				if(Bukkit.getPlayer(lgname)!=null) {
					Player player = Bukkit.getPlayer(lgname);
					player.sendMessage(main.texte.getText(52));
					player.setScoreboard(main.boardlg);
					player.playSound(player.getLocation(),Sound.WOLF_HOWL, 1, 20);
				}
			}	
		}
	}
	
}
