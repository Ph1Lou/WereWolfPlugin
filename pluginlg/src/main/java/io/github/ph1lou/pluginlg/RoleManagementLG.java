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
import org.bukkit.scoreboard.NameTagVisibility;


public class RoleManagementLG {
	
	private final MainLG main;
	
	public RoleManagementLG(MainLG main) {
		this.main=main;	
	}
	
	public void repartitionRolesLG() {
		
		List<String> players = new ArrayList<>(main.playerlg.keySet());
		List<RoleLG> config = new ArrayList<>();
		main.config.tool_switch.put(ToolLG.CHAT,false);
		main.config.role_count.put(RoleLG.VILLAGEOIS,main.config.role_count.get(RoleLG.VILLAGEOIS)+players.size()-main.score.getRole());

		for(RoleLG role:RoleLG.values()) {
			for(int i = 0; i<main.config.role_count.get(role); i++) {
				if(!role.equals(RoleLG.COUPLE)) {
					config.add(role);
				}
			}
		}
	
		Random r = new Random(System.currentTimeMillis());

		while(!players.isEmpty()) {
			
			int n =(int) Math.floor(r.nextFloat()*players.size());
			String playername = players.get(n);
			PlayerLG plg = main.playerlg.get(playername);
			plg.setCamp(config.get(0).getCamp());
			plg.setPower(config.get(0).getPower());
			plg.setRole(config.get(0));
			recoverRolePower(playername);
			config.remove(0);	
			players.remove(n);
		}
		
		main.endlg.check_victory();
	}
	
	public void recoverRolePower(String playername) {
		
		if (Bukkit.getPlayer(playername)==null) return;
		
		Player player = Bukkit.getPlayer(playername);
		PlayerLG plg = main.playerlg.get(playername);
		
		plg.setKit(true);
		
		player.sendMessage(main.text.esthetique("§m", "§6",main.text.getText(42)+main.text.translaterole.get(plg.getRole())+main.text.getText(43)));

		for(PotionEffectType p:effect_recover(playername)) {
			player.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
		}
		
		for(ItemStack i:main.stufflg.role_stuff.get(plg.getRole())) {
			
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
		else if(plg.isRole(RoleLG.FRERE_SIAMOIS)){
			player.setMaxHealth(30);
		}
		else if ((plg.isRole(RoleLG.ASSASSIN) && !main.isDay(Day.NIGHT)) || (plg.isCamp(Camp.LG) && main.isDay(Day.NIGHT))) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,-1,false,false));
		}	
		else if (plg.isRole(RoleLG.ENFANT_SAUVAGE)) {
			player.sendMessage(main.text.esthetique("§m", "§e",main.text.poweruse.get(RoleLG.ENFANT_SAUVAGE)+main.conversion(main.config.value.get(TimerLG.MASTER_DURATION))));
		}
		else if (plg.isRole(RoleLG.CUPIDON)) {
			player.sendMessage(main.text.esthetique("§m", "§e",main.text.poweruse.get(RoleLG.CUPIDON)+main.conversion(main.config.value.get(TimerLG.COUPLE_DURATION))));
		}
		else if (plg.isRole(RoleLG.ANGE)) {
			player.sendMessage(main.text.esthetique("§m", "§e",main.text.poweruse.get(RoleLG.ANGE)+main.conversion(main.config.value.get(TimerLG.ANGE_DURATION))));
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
		if(main.config.scenario.get(ScenarioLG.CAT_EYES)){
			effect.add(PotionEffectType.NIGHT_VISION);
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
			
			killer.sendMessage(main.text.esthetique("§m", "§6",main.text.powerhasbeenuse.get(RoleLG.VOLEUR)+main.text.translaterole.get(role)+main.text.getText(43)));
			killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			
			if (klg.isRole(RoleLG.VILAIN_PETIT_LOUP) || klg.isRole(RoleLG.RENARD)) {
				killer.removePotionEffect(PotionEffectType.SPEED);
			}
			
			for(PotionEffectType p:effect_recover(killername)) {
				killer.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
			}
			
			if (klg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
				killer.setMaxHealth(30);
				if(main.isDay(Day.NIGHT)) {
					killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
				}
			}
			else if(plg.isRole(RoleLG.FRERE_SIAMOIS)){
				killer.setMaxHealth(30);
			}
			else if(klg.isRole(RoleLG.PETITE_FILLE) || klg.isRole(RoleLG.LOUP_PERFIDE)) {
				klg.setPower(true);
			}	
			
			if (klg.isRole(RoleLG.ENFANT_SAUVAGE)) {
				
				if(klg.hasPower()) {
					killer.sendMessage(main.text.esthetique("§m", "§e",main.text.poweruse.get(RoleLG.ENFANT_SAUVAGE)+main.conversion(main.config.value.get(TimerLG.MASTER_DURATION)-main.score.getTimer()+main.config.value.get(TimerLG.ROLE_DURATION))));
				}
				else {
					String mastername = plg.getAffectedPlayer().get(0);
					klg.clearAffectedPlayer();
					plg.clearAffectedPlayer();
					klg.addAffectedPlayer(mastername);
					main.playerlg.get(mastername).addDisciple(killername);
					main.playerlg.get(mastername).removeDisciple(playername);
					
					if(mastername.equals(killername)) {
						main.role_manage.newLG(killername);
					}
					else killer.sendMessage(main.text.esthetique("§m", "§e",main.text.powerhasbeenuse.get(RoleLG.ENFANT_SAUVAGE)+mastername));
				}
			}
			if (klg.isRole(RoleLG.CUPIDON)) {
				
				if(klg.hasPower()) {
					killer.sendMessage(main.text.esthetique("§m", "§e",main.text.poweruse.get(RoleLG.CUPIDON)+main.conversion(main.config.value.get(TimerLG.COUPLE_DURATION)-main.score.getTimer()+main.config.value.get(TimerLG.ROLE_DURATION))));
				}
				else {
					klg.addAffectedPlayer(plg.getAffectedPlayer().get(0));
					klg.addAffectedPlayer(plg.getAffectedPlayer().get(1));
					plg.clearAffectedPlayer();
					killer.sendMessage(main.text.esthetique("§m", "§e",main.text.powerhasbeenuse.get(RoleLG.CUPIDON)+klg.getAffectedPlayer().get(0)+" et "+klg.getAffectedPlayer().get(1)));
				}
			}
			if ((klg.isRole(RoleLG.ASSASSIN) && !main.isDay(Day.NIGHT)) || (klg.isCamp(Camp.LG) && main.isDay(Day.NIGHT) )) {	
				killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));

			}
			if (klg.isRole(RoleLG.LOUP_FEUTRE)){
				klg.setCampFeutre(plg.getCampFeutre());
				klg.setRoleFeutre(plg.getRoleFeutre());
				killer.sendMessage(main.text.esthetique("§m", "§e",main.text.getText(15)+main.text.translaterole.get(klg.getRole())));
			}
			if (klg.isRole(RoleLG.ANGE)) {
				killer.sendMessage(main.text.esthetique("§m", "§e",RoleLG.ANGE.getPowerUse()+main.conversion(main.config.value.get(TimerLG.ANGE_DURATION)-main.score.getTimer()+main.config.value.get(TimerLG.ROLE_DURATION))));
				killer.setMaxHealth(24);
			}
			if (klg.isRole(RoleLG.TUEUR_EN_SERIE)) {
				if (Bukkit.getPlayer(playername)!=null) {
					killer.setMaxHealth(Bukkit.getPlayer(playername).getMaxHealth());
					klg.addKLostHeart(plg.getLostHeart());
				}
			}
			else if (klg.isRole(RoleLG.ANGE_DECHU) || klg.isRole(RoleLG.ANGE_GARDIEN)){
				klg.clearAffectedPlayer();
				klg.addAffectedPlayer(plg.getAffectedPlayer().get(0));
				plg.clearAffectedPlayer();
				
				if(main.playerlg.get(klg.getAffectedPlayer().get(0)).isState(State.MORT) || klg.getAffectedPlayer().get(0).equals(killername)) {
					if(klg.isRole(RoleLG.ANGE_DECHU)) {
						killer.setMaxHealth(30);
						killer.sendMessage(main.text.esthetique("§m", "§e",main.text.getText(48)+klg.getAffectedPlayer().get(0)));
					}
				}
				else {
					killer.setMaxHealth(24);
					killer.sendMessage(main.text.esthetique("§m", "§e",main.text.getText(49)+klg.getAffectedPlayer().get(0)));
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
							pc.sendMessage(main.text.esthetique("§m", "§e",main.text.powerhasbeenuse.get(RoleLG.COUPLE)+killername+main.text.getText(11)));
							pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR,1,20);
						}
						killer.sendMessage(main.text.esthetique("§m", "§e",main.text.powerhasbeenuse.get(RoleLG.COUPLE)+c+main.text.getText(11)));
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
				main.couple_manage.thief_couplerange(killername,playername);
			}
		}
		main.death_manage.mortdefinitive(playername);
	}
	
	public void auto_master() {
		
		for(String playername:main.playerlg.keySet()) {

			if (main.playerlg.get(playername).isState(State.VIVANT) && main.playerlg.get(playername).isRole(RoleLG.ENFANT_SAUVAGE) && main.playerlg.get(playername).hasPower()) {
				
				String mastername = autoSelec(playername);
				main.playerlg.get(mastername).addDisciple(playername);
				main.playerlg.get(playername).addAffectedPlayer(mastername);
				main.playerlg.get(playername).setPower(false);
				if(Bukkit.getPlayer(playername) != null){
					Player player = Bukkit.getPlayer(playername);
					player.sendMessage(main.text.esthetique("§m", "§e",main.text.getText(47)+mastername));
					player.playSound(player.getLocation(), Sound.BAT_IDLE,1,20);
				}
			}
		}
		
	}

	public void frereLife() {

		int nbok = 0;
		double health = 0;
		for (String p:main.playerlg.keySet()) {
			if (main.playerlg.get(p).isState(State.VIVANT) && main.playerlg.get(p).isRole(RoleLG.FRERE_SIAMOIS) && Bukkit.getPlayer(p) != null) {
				Player c = Bukkit.getPlayer(p);
				nbok++;
				health += c.getHealth() / c.getMaxHealth();
			}
		}
		health /= nbok;
		for (String p:main.playerlg.keySet()) {
			if (main.playerlg.get(p).isState(State.VIVANT) && main.playerlg.get(p).isRole(RoleLG.FRERE_SIAMOIS) && Bukkit.getPlayer(p) != null) {
				Player c = Bukkit.getPlayer(p);
				if(health * c.getMaxHealth()>10){
					if(health * c.getMaxHealth()+1<c.getHealth()){
						c.playSound(c.getLocation(), Sound.BURP,1,20);
					}
					c.setHealth(health * c.getMaxHealth());
				}
			}
		}
	}
	
	public void auto_ange() {

		Random r = new Random(System.currentTimeMillis());

		for(String playername:main.playerlg.keySet()) {
			
			PlayerLG plg = main.playerlg.get(playername);
			if (plg.isState(State.VIVANT)){
				if(plg.isRole(RoleLG.ANGE)){
					plg.setPower(false);
					if(r.nextBoolean()){
						plg.setRole(RoleLG.ANGE_DECHU);
					}
					else plg.setRole(RoleLG.ANGE_GARDIEN);
				}
				if (plg.isRole(RoleLG.ANGE_DECHU) || plg.isRole(RoleLG.ANGE_GARDIEN)) {

					String targetname = autoSelec(playername);
					plg.addAffectedPlayer(targetname);
					main.playerlg.get(targetname).addTargetOf(playername);

					if(Bukkit.getPlayer(playername) != null){
						Player player = Bukkit.getPlayer(playername);
						player.sendMessage(main.text.esthetique("§m", "§e",main.text.powerhasbeenuse.get(plg.getRole())+targetname));
						player.playSound(player.getLocation(), Sound.CAT_MEOW,1,20);
					}
				}
			}
		}
		if(!main.isState(StateLG.FIN)) {
			main.endlg.check_victory();
		}
	}
	
	public String autoSelec(String playername) {
		
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
		
		if(main.config.tool_switch.get(ToolLG.LG_LIST) && main.score.getTimer()>main.config.value.get(TimerLG.LG_LIST)) {

			main.board.getTeam(playername).setPrefix("§4");
			main.playerlg.get(playername).setScoreBoard(main.board);

			for(String lgname : main.playerlg.keySet()) {
			
				if((main.playerlg.get(lgname).isCamp(Camp.LG) || main.playerlg.get(lgname).isRole(RoleLG.LOUP_GAROU_BLANC) )&& main.playerlg.get(lgname).isState(State.VIVANT) && Bukkit.getPlayer(lgname)!=null ) {
					Player lg1 = Bukkit.getPlayer(lgname);
					lg1.sendMessage(main.text.getText(50));
					lg1.playSound(lg1.getLocation(),Sound.WOLF_HOWL, 1, 20);
				}
			}
		}
		
		if(!main.playerlg.get(playername).isRole(RoleLG.LOUP_GAROU_BLANC)) {
			main.playerlg.get(playername).setCamp(Camp.LG);
		}

		if(Bukkit.getPlayer(playername)!=null) {
			
			Player player = Bukkit.getPlayer(playername);
			player.setScoreboard(main.board);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
			player.sendMessage(main.text.getText(51));
			player.playSound(player.getLocation(),Sound.WOLF_HOWL, 1, 20);
			if (main.isDay(Day.NIGHT)) {	
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
			}
		}
	}
	
	public void lgList() {

		for(String lgname : main.playerlg.keySet()) {

			PlayerLG lg = main.playerlg.get(lgname);

			if((lg.isCamp(Camp.LG) || lg.isRole(RoleLG.LOUP_GAROU_BLANC)) && lg.isState(State.VIVANT)) {

				main.board.getTeam(lgname).setPrefix("§4");
				lg.setScoreBoard(main.board);

				if(Bukkit.getPlayer(lgname)!=null) {
					Player player = Bukkit.getPlayer(lgname);
					player.sendMessage(main.text.getText(52));
					player.playSound(player.getLocation(),Sound.WOLF_HOWL, 1, 20);
					player.setScoreboard(main.board);
				}
			}	
		}
	}
}
