package io.github.ph1lou.pluginlg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.commandlg.AdminLG;
import io.github.ph1lou.pluginlg.commandlg.CommandLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.listener.MenuListener;
import io.github.ph1lou.pluginlg.listener.PlayerListeners;
import io.github.ph1lou.pluginlg.listener.ScenarioLG;
import io.github.ph1lou.pluginlg.listener.WorldListener;
import io.github.ph1lou.pluginlg.savelg.ConfigLG;
import io.github.ph1lou.pluginlg.savelg.FileLG;
import io.github.ph1lou.pluginlg.savelg.SerializerLG;
import io.github.ph1lou.pluginlg.savelg.StuffLG;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;


import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MainLG extends JavaPlugin {
	
	public Scoreboard board ;
	public Scoreboard boardlg ;
	public final Map<UUID, FastBoard> boards = new HashMap<>();
	public Map<String, PlayerLG> playerlg = new HashMap<>();
	public List<List<String>> couplerange = new ArrayList<>();
	private StateLG state;
	private Day daystate;
	public CycleLG cycle = new CycleLG(this);
	public CommandLG cmdlg = new CommandLG(this);
	public DeathManagementLG deathmanage = new DeathManagementLG(this);
	public VoteLG vote = new VoteLG(this);
	public ScoreBoardLG score = new ScoreBoardLG(this);
	public EventsLG eventslg = new EventsLG(this);
	public OptionLG optionlg = new OptionLG(this);
	public ProximityLG proxlg = new ProximityLG(this);
	public RoleManagementLG rolemanage = new RoleManagementLG(this);
	public CoupleManagement couplemanage = new CoupleManagement(this);
	public SerializerLG serialize = new SerializerLG();
	public ConfigLG config = new ConfigLG();
	public TextLG texte = new TextLG();
	public FileLG filelg = new FileLG();
	public StuffLG stufflg = new StuffLG();
	

	private String soustitrevictoire="";
	
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		setState(StateLG.LOBBY);
		config.getconfig(this,0);
		stufflg.load(this, 0);
		texte.getTexttranslate(this, "text.json");
		board= Bukkit.getScoreboardManager().getNewScoreboard();
		boardlg = Bukkit.getScoreboardManager().getNewScoreboard();
		boardlg.registerNewTeam("lgteam");
		boardlg.getTeam("lgteam").setPrefix("");
		PluginManager pm =getServer().getPluginManager();
		pm.registerEvents(new PlayerListeners(this),this);
		pm.registerEvents(new WorldListener(this),this);
		pm.registerEvents(new MenuListener(this),this);
		pm.registerEvents(new ScenarioLG(this),this);
		getCommand("lg").setExecutor(new CommandLG(this));
		getCommand("adminlg").setExecutor(new AdminLG(this));
		
		
		new UpdateChecker(this, 73113).getVersion(version -> {
			
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
            	console.sendMessage(texte.getText(2));
            } else {
            	console.sendMessage(texte.getText(185));
            }
        });  
		
		setworld();
		
		for(Player player:Bukkit.getOnlinePlayers()) {
			joinPlayer(player) ;	
		}
		
		TeleportationLG start = new TeleportationLG(this);
		start.runTaskTimer(this, 0, 20);

	}
	
	
	
	private void setworld() {
		
		World world = Bukkit.getWorld("world");
		world.setPVP(false);
	 	world.setWeatherDuration(0);
	 	world.setThunderDuration(0);
	 	world.setTime(0);
		world.setGameRuleValue("reducedDebugInfo", "true");
		world.setGameRuleValue("keepInventory", "true");
		world.setGameRuleValue("naturalRegeneration", "false");
		world.getWorldBorder().reset();
		
		int x=(int) world.getSpawnLocation().getX();
		int z=(int) world.getSpawnLocation().getZ();
		
		world.setSpawnLocation(x, 151,z);
		
		for(int i=-16;i<=16;i++) {
			
			for(int j=-16;j<=16;j++) {
				
				new Location(world, i+x, 150,j+z).getBlock().setType(Material.BARRIER);
				new Location(world, i+x, 154,j+z).getBlock().setType(Material.BARRIER);
			}
			
			new Location(world, i+x, 151,z-16).getBlock().setType(Material.BARRIER);
			new Location(world, i+x, 152,z-16).getBlock().setType(Material.BARRIER);
			new Location(world, i+x, 153,z-16).getBlock().setType(Material.BARRIER);
			new Location(world, i+x, 151,z+16).getBlock().setType(Material.BARRIER);
			new Location(world, i+x, 152,z+16).getBlock().setType(Material.BARRIER);
			new Location(world, i+x, 153,z+16).getBlock().setType(Material.BARRIER);
			new Location(world, x-16, 151,i+z).getBlock().setType(Material.BARRIER);
			new Location(world, x-16, 152,i+z).getBlock().setType(Material.BARRIER);
			new Location(world, x-16, 153,i+z).getBlock().setType(Material.BARRIER);
			new Location(world, x+16, 151,i+z).getBlock().setType(Material.BARRIER);
			new Location(world, x+16, 152,i+z).getBlock().setType(Material.BARRIER);
			new Location(world, x+16, 153,i+z).getBlock().setType(Material.BARRIER);
		}
		
	}
	
	
	
	public void joinPlayer(Player player) {
		
		String playername = player.getName();
		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(texte.getText(125));
        boards.put(player.getUniqueId(), fastboard);
        player.setScoreboard(board);
		Title.sendTabTitle(player, texte.getText(0),texte.getText(184)+"§9§lPh1Lou");
		new UpdateChecker(this, 73113).getVersion(version -> {
			
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
            	console.sendMessage(texte.getText(185));
            }
        });  
		
		if(isState(StateLG.LOBBY)) {
			 
			player.setMaxHealth(20); 
			player.setHealth(20); 
			player.getInventory().clear();
	        player.getInventory().setHelmet(null);
	        player.getInventory().setChestplate(null);
	        player.getInventory().setLeggings(null);
	        player.getInventory().setBoots(null);
	        player.teleport(player.getWorld().getSpawnLocation());
			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(texte.getText(1));
			playerlg.put(playername, new PlayerLG());
			score.addPlayerSize();
			for(PotionEffect po:player.getActivePotionEffects()) {
				player.removePotionEffect(po.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0,false,false));
			
		}
		else {
			if (!playerlg.containsKey(playername)) {
				player.setGameMode(GameMode.SPECTATOR);
				player.sendMessage(texte.getText(38));	
			}
			else if(playerlg.get(playername).isState(State.MORT)) {
				player.setGameMode(GameMode.SPECTATOR);
			}
			else if(playerlg.get(playername).isCamp(Camp.LG) || playerlg.get(playername).isRole(RoleLG.LOUP_GAROU_BLANC)) {
				 player.setScoreboard(boardlg);
			}
			else if (isState(StateLG.LG) &&  !playerlg.get(playername).hasKit()) {
				rolemanage.recover_rolepower(playername);
				
			}
		} 
		score.updateBoard();
	}
	
	
	
	
	public void eparpillement(String playername,double d,String message) {
		
		
		if(Bukkit.getPlayer(playername)!=null) {
			
			Player player = Bukkit.getPlayer(playername);
			World world = player.getWorld();
			WorldBorder wb = world.getWorldBorder();
			double a = d*2*Math.PI/Bukkit.getOnlinePlayers().size();
			int x = (int) (Math.round(wb.getSize()/3*Math.cos(a)+world.getSpawnLocation().getX()));
			int z = (int) (Math.round(wb.getSize()/3*Math.sin(a)+world.getSpawnLocation().getBlockZ()));
			Location spawnp = new Location(world,x,world.getHighestBlockYAt(x,z)+1,z);
			playerlg.get(playername).setSpawn(spawnp);
			if(isState(StateLG.TELEPORTATION)) {
				spawnp.setY(spawnp.getY()+100);
			}
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.setRemainingAir(300);
			player.setCompassTarget(spawnp);
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(message);
			player.teleport(spawnp);
		}
	}


	public void setState(StateLG state) {
		this.state=state;
	}
	

	public boolean isState(StateLG state) {
		return this.state==state;
	}
	
	
	public void setDay(Day daystate) {
		this.daystate=daystate;
	}
	

	public boolean isDay(Day daystate) {
		return this.daystate==daystate;
	}


	public String conversion(int timer) {
		
		String valeur;
		
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
		
		return valeur;
	}
	
	public void check_victory() {
		
		int player=score.getPlayerSize();
		
		if(couplerange.size()==1) {
			
			int cupiok=0;
			for(String p:playerlg.keySet()) {
				if(!couplerange.get(0).contains(p)) {
					if (playerlg.get(p).isState(State.VIVANT) && ((playerlg.get(p).isRole(RoleLG.CUPIDON) && couplerange.get(0).contains(playerlg.get(p).getAffectedPlayer().get(0)) || (!playerlg.get(p).getCibleOf().isEmpty() && !couplerange.get(0).contains(playerlg.get(p).getCibleOf().get(0))) && !playerlg.get(playerlg.get(p).getCibleOf().get(0)).isRole(RoleLG.ANGE_GARDIEN))) || (playerlg.get(p).isRole(RoleLG.ANGE_GARDIEN) && !playerlg.get(p).getAffectedPlayer().isEmpty() && couplerange.get(0).contains(playerlg.get(p).getAffectedPlayer().get(0))) ){
						cupiok++;
					}
				}
			}
			
			if(player ==couplerange.get(0).size()+cupiok) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.COUPLE);
				fin();
				return;
			}	
		}
		
		
		
		if(config.tool_switch.get(ToolLG.victoirecoupleonly) && !couplerange.isEmpty()) {
			return;
		}
		
		
		
		for(String p1:playerlg.keySet()) {
			
			if(playerlg.get(p1).isState(State.JUGEMENT)) return;
			if(playerlg.get(p1).isState(State.VIVANT)) { 
			
				List<String> teamange= new ArrayList<>();
				
				teamange.add(p1);
				
				for(int i=0;i<teamange.size();i++) {
					if(!playerlg.get(teamange.get(i)).getCibleOf().isEmpty()) {
						for(String p2:playerlg.get(teamange.get(i)).getCibleOf()) {
							if(playerlg.get(p2).isRole(RoleLG.ANGE_GARDIEN) && playerlg.get(p2).isState(State.VIVANT) && !teamange.contains(p2)) {
								teamange.add(p2);
							}
						}
					}
				}
				
				if(teamange.size()>1 && teamange.size()==score.getPlayerSize()) {
					soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.ANGE_GARDIEN);
					fin();
					return;
				}
			}
		}
			
		Camp camp = null;
		
		for(String p:playerlg.keySet()) {
			if(playerlg.get(p).isState(State.JUGEMENT)) return;
			PlayerLG plg = playerlg.get(p);
			if(plg.isState(State.VIVANT)) {
				
				if(camp==null || !plg.isCamp(camp)) {
					if(camp!=null) {
						return;
					}
					camp=plg.getCamp();
				}
			}
		}
		if(camp==null) {

			soustitrevictoire=texte.getText(5);
			fin();
			return;
		}
		if(camp.equals(Camp.LG)) {
			soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.LOUP_GAROU);
			fin();
			return;
			
		}
		if(camp.equals(Camp.VILLAGE)) {
			soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.VILLAGEOIS);
			fin();
			return;
		}
		
		if(camp.equals(Camp.NEUTRE)) {
			
			if(!config.tool_switch.get(ToolLG.victoireneutreequipe) && player !=1 ) {
				return;
			}
			RoleLG role=null;
			
			for(String p:playerlg.keySet()) {
				
				PlayerLG plg = playerlg.get(p);
				if(plg.isState(State.VIVANT)) {
					
					if(role==null || !plg.isRole(role)) {
						if(role!=null) {
							return;
						}
						role=plg.getRole();
					}
				}
			}

			if(role == null){
				return;
			}
			if (role.equals(RoleLG.VOLEUR)) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.VOLEUR);
				fin();
				return;
			}
			if (role.equals(RoleLG.LOUP_GAROU_BLANC)) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.LOUP_GAROU_BLANC);
				fin();
				return;
			}
			if (role.equals(RoleLG.ASSASSIN)) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.ASSASSIN);
				fin();
				return;
			}
			if (role.equals(RoleLG.LOUP_AMNESIQUE)) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.LOUP_AMNESIQUE);
				fin();
				return;
			}
			if (role.equals(RoleLG.ANGE_DECHU)) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.ANGE_DECHU);
				fin();
				return;
			}
			if (role.equals(RoleLG.ANGE_GARDIEN)) {
				
				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.ANGE_GARDIEN);
				fin();
			}
			if (role.equals(RoleLG.ANGE)) {

				soustitrevictoire=texte.getText(4)+texte.translaterole.get(RoleLG.ANGE);
				fin();
			}
		}
	}
	

	public void fin() {
		
		setState(StateLG.FIN);
		score.getKillCounter();
		Bukkit.broadcastMessage(texte.esthetique("§m", "§6",texte.getText(3)+soustitrevictoire));
		config.tool_switch.put(ToolLG.chat,true);
		
		for(String p:playerlg.keySet()) {
			
			
			if(playerlg.get(p).isState(State.MORT)) {
				if(playerlg.get(p).isVoleur()) {
					Bukkit.broadcastMessage("§m§l"+p+texte.getText(187)+texte.translaterole.get(RoleLG.VOLEUR)+texte.getText(188)+texte.translaterole.get(playerlg.get(p).getRole()));
				}
				else Bukkit.broadcastMessage("§m§l"+p+texte.getText(187)+texte.translaterole.get(playerlg.get(p).getRole()));
			}
			else {
				if(playerlg.get(p).isVoleur()) {
					Bukkit.broadcastMessage("§e§l"+p+texte.getText(187)+texte.translaterole.get(RoleLG.VOLEUR)+texte.getText(188)+texte.translaterole.get(playerlg.get(p).getRole()));
				}
				else Bukkit.broadcastMessage("§e§l"+p+texte.getText(187)+texte.translaterole.get(playerlg.get(p).getRole()));
			}
			score.updateBoard();	
			
			
		}
		
		for(Player player:Bukkit.getOnlinePlayers()) {
			Title.sendTitle(player,20,60, 20,texte.getText(3), soustitrevictoire);

			TextComponent msgbug = new TextComponent(texte.getText(186));
			msgbug.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://discord.gg/GXXCVUA"));
			player.spigot().sendMessage(msgbug);

		}
		
	}
		
}
		


