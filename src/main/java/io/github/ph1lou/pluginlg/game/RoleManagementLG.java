package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.enumlg.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class RoleManagementLG {
	
	private final GameManager game;
	final Random r = new Random(System.currentTimeMillis());

	public RoleManagementLG(GameManager game) {
		this.game=game;	
	}
	
	public void repartitionRolesLG() {

		List<String> players = new ArrayList<>(game.playerLG.keySet());
		List<RoleLG> config = new ArrayList<>();
		game.config.configValues.put(ToolLG.CHAT, false);
		game.config.roleCount.put(RoleLG.VILLAGEOIS, game.config.roleCount.get(RoleLG.VILLAGEOIS) + players.size() - game.score.getRole());

		for (RoleLG role : RoleLG.values()) {
			for (int i = 0; i < game.config.roleCount.get(role); i++) {
				if (!role.equals(RoleLG.COUPLE) && !role.equals(RoleLG.COUPLE_MAUDIT)) {
					config.add(role);
				}
			}
		}

		while (!players.isEmpty()) {
			
			int n =(int) Math.floor(r.nextFloat()*players.size());
			String playername = players.get(n);
			PlayerLG plg = game.playerLG.get(playername);
			plg.setCamp(config.get(0).getCamp());
			plg.setPower(config.get(0).getPower());
			plg.setRole(config.get(0));
			recoverRolePower(playername);
			config.remove(0);	
			players.remove(n);
		}
		
		game.endlg.check_victory();
	}
	
	public void recoverRolePower(String playername) {
		
		if (Bukkit.getPlayer(playername)==null) return;
		
		Player player = Bukkit.getPlayer(playername);
		PlayerLG plg = game.playerLG.get(playername);
		
		plg.setKit(true);
		
		player.sendMessage(game.text.description.get(plg.getRole()));
		player.sendMessage(game.text.getText(43));

		for(PotionEffectType p:effect_recover(playername)) {
			player.addPotionEffect(new PotionEffect(p,Integer.MAX_VALUE,0,false,false));
		}
		
		for(ItemStack i:game.stufflg.role_stuff.get(plg.getRole())) {
			
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
			if(game.isDay(Day.NIGHT)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
			}
		}
		else if(plg.isRole(RoleLG.FRERE_SIAMOIS)){
			player.setMaxHealth(26);
		}
		else if ((plg.isRole(RoleLG.ASSASSIN) && !game.isDay(Day.NIGHT)) || (plg.isCamp(Camp.LG) && game.isDay(Day.NIGHT))) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,-1,false,false));
		}	
		else if (plg.isRole(RoleLG.ENFANT_SAUVAGE)) {
			player.sendMessage(String.format(game.text.powerUse.get(RoleLG.ENFANT_SAUVAGE), game.score.conversion(game.config.timerValues.get(TimerLG.MASTER_DURATION))));
		} else if (plg.isRole(RoleLG.SUCCUBUS)) {
			player.sendMessage(game.text.powerUse.get(RoleLG.SUCCUBUS));
		} else if (plg.isRole(RoleLG.CUPIDON)) {
			player.sendMessage(String.format(game.text.powerUse.get(RoleLG.CUPIDON), game.score.conversion(game.config.timerValues.get(TimerLG.COUPLE_DURATION))));
		} else if (plg.isRole(RoleLG.ANGE)) {
			player.sendMessage(String.format(game.text.powerUse.get(RoleLG.ANGE), game.score.conversion(game.config.timerValues.get(TimerLG.ANGE_DURATION))));
		}
		if(plg.isRole(RoleLG.ANGE_DECHU) || plg.isRole(RoleLG.ANGE_GARDIEN) || plg.isRole(RoleLG.ANGE)) {
			player.setMaxHealth(24);
			player.setHealth(24);
		}
	}

	
	public List<PotionEffectType> effect_recover(String playername) {
		
		List <PotionEffectType> effect = new ArrayList<>();
		
		if (game.playerLG.get(playername).isRole(RoleLG.VOYANTE) || game.playerLG.get(playername).isRole(RoleLG.VOYANTE_BAVARDE) || game.playerLG.get(playername).isRole(RoleLG.PETITE_FILLE) || game.playerLG.get(playername).isCamp(Camp.LG)) {
			effect.add(PotionEffectType.NIGHT_VISION);
		}
		if ((game.playerLG.get(playername).isRole(RoleLG.VOLEUR) || (game.playerLG.get(playername).isRole(RoleLG.ANCIEN)) && game.playerLG.get(playername).hasPower())) {
			effect.add(PotionEffectType.DAMAGE_RESISTANCE);
		}
		if (game.playerLG.get(playername).isRole(RoleLG.MINEUR)) {
			effect.add(PotionEffectType.FAST_DIGGING);
		}
		if (game.playerLG.get(playername).isRole(RoleLG.RENARD) || game.playerLG.get(playername).isRole(RoleLG.VILAIN_PETIT_LOUP)) {
			effect.add(PotionEffectType.SPEED);
		}
		if (game.config.scenarioValues.get(ScenarioLG.CAT_EYES)) {
			effect.add(PotionEffectType.NIGHT_VISION);
		}
		return effect;
	}
	
	public void thief_recover_role(String killername,String playername){
		
		RoleLG role = game.playerLG.get(playername).getRole();
		
		PlayerLG klg = game.playerLG.get(killername);
		PlayerLG plg = game.playerLG.get(playername);
		
		klg.setRole(role);
		klg.setThief(true);
		klg.setPower(plg.hasPower());
		klg.setUse(plg.getUse());
		
		if((plg.isCamp(Camp.LG) || plg.isRole(RoleLG.LOUP_GAROU_BLANC)) && !klg.isCamp(Camp.LG)) {
			newLG(killername);
		}
		else if(plg.isCamp(Camp.VILLAGE) && !klg.isCamp(Camp.LG)) {
			klg.setCamp(Camp.VILLAGE);
		}
		
		if (Bukkit.getPlayer(killername)!=null) {

			Player killer = Bukkit.getPlayer(killername);

			killer.sendMessage(String.format(game.text.powerHasBeenUse.get(RoleLG.VOLEUR), game.text.translateRole.get(role)));
			killer.sendMessage(game.text.getText(43));
			if (!klg.hasSalvation()) {
				killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}


			if (klg.isRole(RoleLG.VILAIN_PETIT_LOUP) || klg.isRole(RoleLG.RENARD)) {
				killer.removePotionEffect(PotionEffectType.SPEED);
			}

			for (PotionEffectType p : effect_recover(killername)) {
				killer.addPotionEffect(new PotionEffect(p, Integer.MAX_VALUE, 0, false, false));
			}
			
			if (klg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
				killer.setMaxHealth(30);
				if(game.isDay(Day.NIGHT)) {
					killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
				}
			}
			else if(plg.isRole(RoleLG.FRERE_SIAMOIS)){
				killer.setMaxHealth(26);
			}
			else if(klg.isRole(RoleLG.PETITE_FILLE) || klg.isRole(RoleLG.LOUP_PERFIDE)) {
				klg.setPower(true);
			}	
			
			if (klg.isRole(RoleLG.ENFANT_SAUVAGE)) {
				
				if(klg.hasPower()) {
					killer.sendMessage(String.format(game.text.powerUse.get(RoleLG.ENFANT_SAUVAGE), game.score.conversion(game.config.timerValues.get(TimerLG.MASTER_DURATION))));
				}
				else {
					String mastername = plg.getAffectedPlayer().get(0);
					klg.clearAffectedPlayer();
					plg.clearAffectedPlayer();
					klg.addAffectedPlayer(mastername);
					game.playerLG.get(mastername).addTargetOf(killername);
					game.playerLG.get(mastername).removeTargetOf(playername);

					if (mastername.equals(killername)) {
						game.roleManage.newLG(killername);
					} else
						killer.sendMessage(String.format(game.text.powerHasBeenUse.get(RoleLG.ENFANT_SAUVAGE), mastername));
				}
			} else if (plg.isRole(RoleLG.SUCCUBUS)) {
				if (klg.hasPower()) {
					killer.sendMessage(game.text.powerUse.get(RoleLG.SUCCUBUS));
				} else {
					if (!plg.getAffectedPlayer().isEmpty()) {
						klg.addAffectedPlayer(plg.getAffectedPlayer().get(0));
						plg.clearAffectedPlayer();
						killer.sendMessage(String.format(game.getText(314), klg.getAffectedPlayer().get(0)));
					}
				}
			} else if (klg.isRole(RoleLG.CUPIDON)) {

				if (klg.hasPower()) {
					killer.sendMessage(String.format(game.text.powerUse.get(RoleLG.CUPIDON), game.score.conversion(game.config.timerValues.get(TimerLG.COUPLE_DURATION))));
				} else {
					klg.addAffectedPlayer(plg.getAffectedPlayer().get(0));
					klg.addAffectedPlayer(plg.getAffectedPlayer().get(1));
					plg.clearAffectedPlayer();
					killer.sendMessage(String.format(game.text.powerHasBeenUse.get(RoleLG.CUPIDON), klg.getAffectedPlayer().get(0), klg.getAffectedPlayer().get(1)));
				}
			}
			if ((klg.isRole(RoleLG.ASSASSIN) && !game.isDay(Day.NIGHT)) || (klg.isCamp(Camp.LG) && game.isDay(Day.NIGHT) )) {	
				killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));

			}
			if (klg.isRole(RoleLG.LOUP_FEUTRE)){
				klg.setPosterCamp(plg.getPosterCamp());
				klg.setPosterRole(plg.getPosterRole());
				killer.sendMessage(String.format(game.text.getText(15),game.text.translateRole.get(klg.getRole())));
			}
			if (klg.isRole(RoleLG.ANGE)) {
				killer.sendMessage(String.format(game.text.powerUse.get(RoleLG.ANGE), game.score.conversion(game.config.timerValues.get(TimerLG.ANGE_DURATION))));
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
				
				if(game.playerLG.get(klg.getAffectedPlayer().get(0)).isState(State.MORT) || klg.getAffectedPlayer().get(0).equals(killername)) {
					if (klg.isRole(RoleLG.ANGE_DECHU)) {
						killer.setMaxHealth(30);
					}
				} else {
					killer.setMaxHealth(24);
				}
				killer.sendMessage(String.format(game.text.powerHasBeenUse.get(klg.getRole()), klg.getAffectedPlayer().get(0)));

			}
			if (!plg.getLovers().isEmpty()) {

				for (String c : plg.getLovers()) {

					if (!klg.getLovers().contains(c)) {

						klg.addLover(c);
						game.playerLG.get(c).addLover(killername);
						game.playerLG.get(c).removeLover(playername);
						if (Bukkit.getPlayer(c) != null) {
							Player pc = Bukkit.getPlayer(c);
							pc.sendMessage(String.format(game.text.description.get(RoleLG.COUPLE), killername));
							pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
						}
						killer.sendMessage(String.format(game.text.description.get(RoleLG.COUPLE), c));
						killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
					}
				}
				if (!klg.getLovers().contains(killername)) {
					plg.clearLovers();
				}

				for (String cup : game.playerLG.keySet()) {
					if (game.playerLG.get(cup).isRole(RoleLG.CUPIDON) && game.playerLG.get(cup).getAffectedPlayer().contains(playername)) {
						game.playerLG.get(cup).addAffectedPlayer(killername);
						game.playerLG.get(cup).removeAffectedPlayer(playername);
					}
				}
				game.loversManage.thiefLoversRange(killername, playername);
			}
			//Si le voleur est déjà en couple (maudit ou normal), il ne le vole pas

			if (!plg.getCursedLovers().isEmpty() && klg.getCursedLovers().isEmpty()) {
				String c = plg.getCursedLovers();
				if (!klg.getLovers().contains(c)) {
					klg.setCursedLover(c);
					game.playerLG.get(c).setCursedLover(killername);
					if (Bukkit.getPlayer(c) != null) {
						Player pc = Bukkit.getPlayer(c);
						pc.sendMessage(String.format(game.text.description.get(RoleLG.COUPLE_MAUDIT), killername));
						pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
					}
					killer.sendMessage(String.format(game.text.description.get(RoleLG.COUPLE_MAUDIT), c));
					killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
					killer.setMaxHealth(killer.getMaxHealth() + 1);

					for (int i = 0; i < game.loversManage.cursedLoversRange.size(); i++) {
						if (game.loversManage.cursedLoversRange.get(i).contains(playername)) {
							game.loversManage.cursedLoversRange.get(i).add(killername);
							game.loversManage.cursedLoversRange.get(i).remove(playername);
							break;
						}
					}
				}
			}
		}
		game.death_manage.death(playername);
	}
	
	public void auto_master() {


		for(String playername:game.playerLG.keySet()) {

			PlayerLG plg = game.playerLG.get(playername);

			if (plg.isState(State.LIVING) && plg.isRole(RoleLG.ENFANT_SAUVAGE) && plg.hasPower()) {

				String mastername = autoSelect(r.nextFloat(), playername);
				game.playerLG.get(mastername).addTargetOf(playername);
				plg.addAffectedPlayer(mastername);
				plg.setPower(false);
				if (Bukkit.getPlayer(playername) != null) {
					Player player = Bukkit.getPlayer(playername);
					player.sendMessage(String.format(game.text.getText(47), mastername));
					player.playSound(player.getLocation(), Sound.BAT_IDLE, 1, 20);
				}
			}
		}
		
	}

	public void brotherLife() {

		int counter = 0;
		double health = 0;
		for (String p:game.playerLG.keySet()) {
			if (game.playerLG.get(p).isState(State.LIVING) && game.playerLG.get(p).isRole(RoleLG.FRERE_SIAMOIS) && Bukkit.getPlayer(p) != null) {
				Player c = Bukkit.getPlayer(p);
				counter++;
				health += c.getHealth() / c.getMaxHealth();
			}
		}
		health /= counter;
		for (String p:game.playerLG.keySet()) {
			if (game.playerLG.get(p).isState(State.LIVING) && game.playerLG.get(p).isRole(RoleLG.FRERE_SIAMOIS) && Bukkit.getPlayer(p) != null) {
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

		for(String playername:game.playerLG.keySet()) {
			
			PlayerLG plg = game.playerLG.get(playername);
			if (plg.isState(State.LIVING)){
				if(plg.isRole(RoleLG.ANGE)){
					plg.setPower(false);
					if(r.nextBoolean()){
						plg.setRole(RoleLG.ANGE_DECHU);
					}
					else plg.setRole(RoleLG.ANGE_GARDIEN);
				}
				if (plg.isRole(RoleLG.ANGE_DECHU) || plg.isRole(RoleLG.ANGE_GARDIEN)) {

					String targetname = autoSelect(r.nextFloat(),playername);
					plg.addAffectedPlayer(targetname);
					game.playerLG.get(targetname).addTargetOf(playername);

					if(Bukkit.getPlayer(playername) != null){
						Player player = Bukkit.getPlayer(playername);
						player.sendMessage(String.format(game.text.powerHasBeenUse.get(plg.getRole()),targetname));
						player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER,1,20);
					}
				}
			}
		}
		if(!game.isState(StateLG.FIN)) {
			game.endlg.check_victory();
		}
	}


	public String autoSelect(float f, String playername) {
		
		List<String> players = new ArrayList<>();
		for(String p:game.playerLG.keySet()) {
			if(game.playerLG.get(p).isState(State.LIVING) && !p.equals(playername)) {
				players.add(p);
			}	
		}
		if(players.isEmpty()) {
			return playername;
		}
		return 	players.get((int) Math.floor(f*players.size()));
	}
	
	
	
	public void newLG(String playername) {

		PlayerLG plg = game.playerLG.get(playername);

		if (game.config.configValues.get(ToolLG.LG_LIST) && game.config.timerValues.get(TimerLG.LG_LIST) < 0) {

			if(game.config.configValues.get(ToolLG.RED_NAME_TAG)){
				game.board.getTeam(playername).setPrefix("§4");
			}
			plg.setScoreBoard(game.board);

			for (String lgName : game.playerLG.keySet()) {

				PlayerLG lg=game.playerLG.get(lgName);
				if ((lg.isCamp(Camp.LG) || lg.isRole(RoleLG.LOUP_GAROU_BLANC)) && lg.isState(State.LIVING) && Bukkit.getPlayer(lgName) != null) {
					Player lg1 = Bukkit.getPlayer(lgName);
					lg1.sendMessage(game.text.getText(50));
					lg1.playSound(lg1.getLocation(), Sound.WOLF_HOWL, 1, 20);
				}
			}
		}
		
		if(!plg.isRole(RoleLG.LOUP_GAROU_BLANC)) {
			plg.setCamp(Camp.LG);
		}

		if(Bukkit.getPlayer(playername)!=null) {
			
			Player player = Bukkit.getPlayer(playername);
			player.setScoreboard(game.board);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false));
			player.sendMessage(game.text.getText(51));
			player.playSound(player.getLocation(),Sound.WOLF_HOWL, 1, 20);
			if (game.isDay(Day.NIGHT)) {	
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE,-1,false,false));
			}
		}
	}
	
	public void lgList() {

		for(String lgName : game.playerLG.keySet()) {

			PlayerLG lg = game.playerLG.get(lgName);

			if((lg.isCamp(Camp.LG) || lg.isRole(RoleLG.LOUP_GAROU_BLANC)) && lg.isState(State.LIVING)) {

				if (game.config.configValues.get(ToolLG.RED_NAME_TAG)) {
					game.board.getTeam(lgName).setPrefix("§4");
				}
				lg.setScoreBoard(game.board);

				if (Bukkit.getPlayer(lgName) != null) {
					Player player = Bukkit.getPlayer(lgName);
					player.sendMessage(game.text.getText(52));
					player.playSound(player.getLocation(), Sound.WOLF_HOWL, 1, 20);
					player.setScoreboard(game.board);
				}
			}	
		}
	}
}
