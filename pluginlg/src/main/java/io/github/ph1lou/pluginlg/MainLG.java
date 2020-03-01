package io.github.ph1lou.pluginlg;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.commandlg.AdminLG;
import io.github.ph1lou.pluginlg.commandlg.CommandLG;
import io.github.ph1lou.pluginlg.enumlg.*;
import io.github.ph1lou.pluginlg.listener.MenuListener;
import io.github.ph1lou.pluginlg.listener.PlayerListener;
import io.github.ph1lou.pluginlg.listener.ScenarioListener;
import io.github.ph1lou.pluginlg.listener.WorldListener;
import io.github.ph1lou.pluginlg.savelg.*;
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

public class MainLG extends JavaPlugin {

	public Scoreboard board;
	public final Map<UUID, FastBoard> boards = new HashMap<>();
	public final Map<String, PlayerLG> playerlg = new HashMap<>();
	private StateLG state;
	private Day daystate;
	public final CycleLG cycle = new CycleLG(this);
	public final CommandLG cmdlg = new CommandLG(this);
	public final DeathManagementLG death_manage = new DeathManagementLG(this);
	public final VoteLG vote = new VoteLG(this);
	public final ScoreBoardLG score = new ScoreBoardLG(this);
	public final EventsLG eventslg = new EventsLG(this);
	public final OptionLG optionlg = new OptionLG(this);
	public final ProximityLG prox_lg = new ProximityLG(this);
	public final RoleManagementLG role_manage = new RoleManagementLG(this);
	public final CoupleManagement couple_manage = new CoupleManagement(this);
	public final SerializerLG serialize = new SerializerLG();
	public final ConfigLG config = new ConfigLG();
	public final TextLG text = new TextLG();
	public final EndLG endlg = new EndLG(this);
	public final FileLG filelg = new FileLG();
	public final StuffLG stufflg = new StuffLG();
	public final String loupid = "1Lou";


	
	
	@Override
	public void onEnable() {
		
		saveDefaultConfig();
		setState(StateLG.LOBBY);
		setDay(Day.DEFAULT);

		config.getConfig(this,0);
		stufflg.load(this, 0);
		text.getTextTranslate(this, "text.json");
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this),this);
		pm.registerEvents(new WorldListener(this),this);
		pm.registerEvents(new MenuListener(this),this);
		pm.registerEvents(new ScenarioListener(this),this);
		getCommand("lg").setExecutor(new CommandLG(this));
		getCommand("adminlg").setExecutor(new AdminLG(this));
		
		
		new UpdateChecker(this, 73113).getVersion(version -> {
			
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
            	console.sendMessage(text.getText(2));
            } else {
            	console.sendMessage(text.getText(185));
            }
        });  
		
		setWorld();
		
		for(Player player:Bukkit.getOnlinePlayers()) {
			joinPlayer(player) ;	
		}
		
		TeleportationLG start = new TeleportationLG(this);
		start.runTaskTimer(this, 0, 20);
	}
	
	
	
	private void setWorld() {

		try{
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

		}catch(Exception e){
			Bukkit.broadcastMessage(text.getText(21));
		}
	}

	public void joinPlayer(Player player) {


		String playername = player.getName();
		FastBoard fastboard = new FastBoard(player);
		fastboard.updateTitle(text.getText(125));
        boards.put(player.getUniqueId(), fastboard);
		Title.sendTabTitle(player, text.getText(0), text.getText(184)+"P"+"h"+loupid);
		new UpdateChecker(this, 73113).getVersion(version -> {
			
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
            	console.sendMessage(text.getText(185));
            }
        });  
		
		if(isState(StateLG.LOBBY)) {
			board.registerNewTeam(playername);
			board.getTeam(playername).addEntry(playername);
			player.setMaxHealth(20); 
			player.setHealth(20);
			player.setExp(0);
			player.setLevel(0);
			player.getInventory().clear();
	        player.getInventory().setHelmet(null);
	        player.getInventory().setChestplate(null);
	        player.getInventory().setLeggings(null);
	        player.getInventory().setBoots(null);
	        player.teleport(player.getWorld().getSpawnLocation());
			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(text.getText(1));
			playerlg.put(playername, new PlayerLG());
			score.addPlayerSize();
			for(PotionEffect po:player.getActivePotionEffects()) {
				player.removePotionEffect(po.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0,false,false));
			
		}
		else if (!playerlg.containsKey(playername)) {
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(text.getText(38));
			return;
		}
		else if(playerlg.get(playername).isState(State.MORT)) {
			player.setGameMode(GameMode.SPECTATOR);
		}
		else if (isState(StateLG.LG) &&  !playerlg.get(playername).hasKit()) {
			role_manage.recoverRolePower(playername);
		}
		player.setScoreboard(playerlg.get(playername).getScoreBoard());
		optionlg.updateScenario();
	}

	public void eparpillement(String playername,double d,String message) {

		if(Bukkit.getPlayer(playername)!=null) {
			
			Player player = Bukkit.getPlayer(playername);
			World world = player.getWorld();
			WorldBorder wb = world.getWorldBorder();
			double a = d*2*Math.PI/Bukkit.getOnlinePlayers().size();
			int x = (int) (Math.round(wb.getSize()/3*Math.cos(a)+world.getSpawnLocation().getX()));
			int z = (int) (Math.round(wb.getSize()/3*Math.sin(a)+world.getSpawnLocation().getZ()));
			Location spawnp = new Location(world,x,world.getHighestBlockYAt(x,z)+1,z);
			playerlg.get(playername).setSpawn(spawnp.clone());
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
	

}
		


