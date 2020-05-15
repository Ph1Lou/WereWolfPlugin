package io.github.ph1lou.pluginlg.game;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.classesroles.RolesImpl;
import io.github.ph1lou.pluginlg.classesroles.villageroles.Cupid;
import io.github.ph1lou.pluginlg.classesroles.villageroles.SiameseTwin;
import io.github.ph1lou.pluginlg.events.NewWereWolfEvent;
import io.github.ph1lou.pluginlg.events.TargetStealEvent;
import io.github.ph1lou.pluginlgapi.enumlg.RoleLG;
import io.github.ph1lou.pluginlgapi.enumlg.State;
import io.github.ph1lou.pluginlgapi.enumlg.ToolLG;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RoleManagementLG {
	
	private final GameManager game;
	private final MainLG main;

	public RoleManagementLG(MainLG main, GameManager game) {
		this.game=game;
		this.main=main;
	}
	
	public void repartitionRolesLG() {

		List<UUID> playersUUID = new ArrayList<>(game.playerLG.keySet());
		List<RoleLG> config = new ArrayList<>();
		game.config.getConfigValues().put(ToolLG.CHAT, false);
		game.config.getRoleCount().put(RoleLG.VILLAGER, game.config.getRoleCount().get(RoleLG.VILLAGER) + playersUUID.size() - game.score.getRole());

		for (RoleLG role : RoleLG.values()) {
			for (int i = 0; i < game.config.getRoleCount().get(role); i++) {
				if (!role.equals(RoleLG.LOVER) && !role.equals(RoleLG.CURSED_LOVER) && !role.equals(RoleLG.AMNESIAC_LOVER)) {
					config.add(role);
				}
			}
		}

		while (!playersUUID.isEmpty()) {
			
			int n =(int) Math.floor(game.getRandom().nextFloat()*playersUUID.size());
			UUID playerUUID = playersUUID.get(n);
			PlayerLG plg = game.playerLG.get(playerUUID);

			try {
				PluginManager pm = Bukkit.getPluginManager();
				RolesImpl role =game.rolesRegister.get(config.get(0)).newInstance(game,playerUUID);
				pm.registerEvents(role, main);
				plg.setRole(role);

			} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}
			recoverRolePower(playerUUID);
			config.remove(0);	
			playersUUID.remove(n);
		}
		
		game.endlg.check_victory();
	}
	
	public void recoverRolePower(UUID uuid) {
		
		if (Bukkit.getPlayer(uuid)==null) return;
		
		Player player = Bukkit.getPlayer(uuid);
		PlayerLG plg = game.playerLG.get(uuid);
		plg.setKit(true);
		
		player.performCommand("ww role");
		player.sendMessage(game.translate("werewolf.announcement.review_role"));

		plg.getRole().recoverPotionEffect(player);
		plg.getRole().recoverPower(player);

		for(ItemStack i:game.stufflg.role_stuff.get(plg.getRole().getRoleEnum())) {
			
			if(player.getInventory().firstEmpty()==-1) {
				player.getWorld().dropItem(player.getLocation(),i);
			}
			else {
				player.getInventory().addItem(i);
				player.updateInventory();
			}
		}
	}

	
	public void thief_recover_role(UUID killerUUID,UUID playerUUID){

		PlayerLG plg = game.playerLG.get(playerUUID);
		RolesImpl role = plg.getRole();
		PlayerLG klg = game.playerLG.get(killerUUID);
		String killerName =klg.getName();

		try {
			RolesImpl roleClone= (RolesImpl) role.clone();
			Bukkit.getPluginManager().registerEvents(roleClone,main);
			klg.setRole(roleClone);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		klg.setThief(true);
		klg.getRole().setPlayerUUID(killerUUID);

		if(game.roleManage.isWereWolf(plg) && !klg.getInfected()) {
			Bukkit.getPluginManager().callEvent(new NewWereWolfEvent(killerUUID));
		}
		if(plg.getInfected()){
			klg.setInfected(true);
		}

		if (Bukkit.getPlayer(killerUUID)!=null) {

			Player killer = Bukkit.getPlayer(killerUUID);

			killer.sendMessage(game.translate("werewolf.role.thief.realized_theft", role.getDisplay()));
			killer.sendMessage(game.translate("werewolf.announcement.review_role"));

			if (!klg.hasSalvation()) {
				killer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}
			klg.getRole().recoverPotionEffect(killer);
			klg.getRole().stolen(playerUUID);
			Bukkit.getPluginManager().callEvent(new TargetStealEvent(killerUUID,playerUUID));

			if (!plg.getLovers().isEmpty() && !klg.getLovers().contains(playerUUID) && !plg.getLovers().contains(klg.getCursedLovers())) {

				for (UUID uuid1 : plg.getLovers()) {

					PlayerLG llg =game.playerLG.get(uuid1);
					if(llg.isState(State.ALIVE) ){
						klg.addLover(uuid1);
						llg.addLover(killerUUID);
						llg.removeLover(playerUUID);

						if (Bukkit.getPlayer(uuid1) != null) {
							Player pc = Bukkit.getPlayer(uuid1);
							pc.sendMessage(game.translate("werewolf.role.lover.description", killerName));
							pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
						}
						killer.sendMessage(game.translate("werewolf.role.lover.description", llg.getName()));
						killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR, 1, 20);

						if (!klg.getLovers().contains(killerUUID)) {
							plg.clearLovers();
						}

						for (UUID uuid2 : game.playerLG.keySet()) {
							PlayerLG plc =game.playerLG.get(uuid2);
							if (plc.getRole() instanceof Cupid){
								Cupid cupid = (Cupid) plc.getRole();
								if(cupid.getAffectedPlayers().contains(playerUUID)) {
									cupid.addAffectedPlayer(killerUUID);
									cupid.removeAffectedPlayer(playerUUID);
								}
							}
						}
						game.loversManage.thiefLoversRange(killerUUID, playerUUID);

					}

				}
			}

			if (plg.getAmnesiacLoverUUID()!=null && klg.getAmnesiacLoverUUID()==null && klg.getLovers().isEmpty()) {
				
				UUID uuid= plg.getAmnesiacLoverUUID();
				PlayerLG llg =game.playerLG.get(uuid);
				
				if(llg.isState(State.ALIVE)){
					
					klg.setAmnesiacLoverUUID(uuid);
					llg.setAmnesiacLoverUUID(killerUUID);

					if(plg.getRevealAmnesiacLover()){
						klg.setRevealAmnesiacLover(true);
						if (Bukkit.getPlayer(uuid) != null) {
							Player pc = Bukkit.getPlayer(uuid);
							pc.sendMessage(game.translate("werewolf.role.lover.description", killerName));
							pc.playSound(pc.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);
						}
						killer.sendMessage(game.translate("werewolf.role.lover.description", llg.getName()));
						killer.playSound(killer.getLocation(), Sound.PORTAL_TRAVEL, 1, 20);

						for (int i = 0; i < game.loversManage.amnesiacLoversRange.size(); i++) {
							if (game.loversManage.amnesiacLoversRange.get(i).contains(playerUUID)) {
								game.loversManage.amnesiacLoversRange.get(i).add(killerUUID);
								game.loversManage.amnesiacLoversRange.get(i).remove(playerUUID);
								break;
							}
						}
					}
				}
			}
			
			//Si le voleur est déjà en couple (maudit ou normal), il ne le vole pas

			if (plg.getCursedLovers()!=null && klg.getCursedLovers()==null) {

				UUID uuid = plg.getCursedLovers();
				PlayerLG llg =game.playerLG.get(uuid);

				if (!klg.getLovers().contains(uuid) && !klg.getAmnesiacLoverUUID().equals(uuid)) {

					if(llg.isState(State.ALIVE)){

						klg.setCursedLover(uuid);
						llg.setCursedLover(killerUUID);

						if (Bukkit.getPlayer(uuid) != null) {
							Player pc = Bukkit.getPlayer(uuid);
							pc.sendMessage(game.translate("werewolf.role.cursed_lover.description", killerUUID));
							pc.playSound(pc.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
						}
						killer.sendMessage(game.translate("werewolf.role.cursed_lover.description", uuid));
						killer.playSound(killer.getLocation(), Sound.SHEEP_SHEAR, 1, 20);
						killer.setMaxHealth(killer.getMaxHealth() + 1);

						for (int i = 0; i < game.loversManage.cursedLoversRange.size(); i++) {
							if (game.loversManage.cursedLoversRange.get(i).contains(playerUUID)) {
								game.loversManage.cursedLoversRange.get(i).add(killerUUID);
								game.loversManage.cursedLoversRange.get(i).remove(playerUUID);
								break;
							}
						}
					}
				}
			}
		}
		game.death_manage.death(playerUUID);
	}
	


	public void brotherLife() {

		int counter = 0;
		double health = 0;
		for (UUID uuid:game.playerLG.keySet()) {

			PlayerLG plg= game.playerLG.get(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole() instanceof SiameseTwin && Bukkit.getPlayer(uuid) != null) {
				Player c = Bukkit.getPlayer(uuid);
				counter++;
				health += c.getHealth() / c.getMaxHealth();
			}
		}
		health /= counter;
		for (UUID uuid:game.playerLG.keySet()) {

			PlayerLG plg= game.playerLG.get(uuid);

			if (plg.isState(State.ALIVE) && plg.getRole() instanceof SiameseTwin && Bukkit.getPlayer(uuid) != null) {
				Player c = Bukkit.getPlayer(uuid);
				if(health * c.getMaxHealth()>10){
					if(health * c.getMaxHealth()+1<c.getHealth()){
						c.playSound(c.getLocation(), Sound.BURP,1,20);
					}
					c.setHealth(health * c.getMaxHealth());
				}
			}
		}
	}

	public UUID autoSelect(UUID playerUUID) {
		
		List<UUID> players = new ArrayList<>();
		for(UUID uuid:game.playerLG.keySet()) {
			if(game.playerLG.get(uuid).isState(State.ALIVE) && !uuid.equals(playerUUID)) {
				players.add(uuid);
			}	
		}
		if(players.isEmpty()) {
			return playerUUID;
		}
		return 	players.get((int) Math.floor(game.getRandom().nextFloat()*players.size()));
	}
	
	
	

	public boolean isWereWolf(UUID uuid){
		if(game.playerLG.containsKey(uuid)){
			return  isWereWolf(game.playerLG.get(uuid));
		}
		return false;
	}

	public boolean isWereWolf(PlayerLG plg){
		return(plg.getRole()!=null && plg.getRole().isWereWolf());
	}
	

}
