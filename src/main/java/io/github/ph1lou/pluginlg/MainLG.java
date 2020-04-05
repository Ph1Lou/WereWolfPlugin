package io.github.ph1lou.pluginlg;


import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.commandlg.AdminLG;
import io.github.ph1lou.pluginlg.commandlg.CommandLG;
import io.github.ph1lou.pluginlg.enumlg.Day;
import io.github.ph1lou.pluginlg.enumlg.State;
import io.github.ph1lou.pluginlg.enumlg.StateLG;
import io.github.ph1lou.pluginlg.listener.*;
import io.github.ph1lou.pluginlg.savelg.*;
import io.github.ph1lou.pluginlg.worldloader.WorldFillTask;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainLG extends JavaPlugin {

	public Scoreboard board;
	public final Map<UUID, FastBoard> boards = new HashMap<>();
	public final Map<String, PlayerLG> playerLG = new HashMap<>();
	private StateLG state;
	private Day dayState;
	public final CycleLG cycle = new CycleLG(this);
	public final DeathManagementLG death_manage = new DeathManagementLG(this);
	public final VoteLG vote = new VoteLG(this);
	public final ScoreBoardLG score = new ScoreBoardLG(this);
	public final EventsLG eventslg = new EventsLG(this);
	public final OptionLG optionlg = new OptionLG(this);
	public final ProximityLG proximity = new ProximityLG(this);
	public final RoleManagementLG role_manage = new RoleManagementLG(this);
	public final CoupleManagement couple_manage = new CoupleManagement(this);
	public final SerializerLG serialize = new SerializerLG();
	public final ConfigLG config = new ConfigLG();
	public TextLG text = new TextLG();
	public final EndLG endlg = new EndLG(this);
	public final FileLG filelg = new FileLG();
	public final StuffLG stufflg = new StuffLG(this);
	public final LangLG lang = new LangLG(this);
	public final SparkLG spark = new SparkLG(this);
	public WorldFillTask wft;


	@Override
	public void onEnable() {
		Bukkit.getScheduler().runTask(this, this::enable);
	}


	@Override
	public void onLoad(){
		WorldUtils.patchBiomes();
	}

	public void enable() {
		saveDefaultConfig();

		setState(StateLG.LOBBY);
		setDay(Day.DEFAULT);
		lang.initLanguage();
		optionlg.initInv();
		config.getConfig(this,"saveCurrent");
		stufflg.load("saveCurrent");
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this),this);
		pm.registerEvents(new WorldListener(this),this);
		pm.registerEvents(new MenuListener(this),this);
		pm.registerEvents(new ScenarioListener(this),this);
		pm.registerEvents(new EnchantmentListener(this),this);
		pm.registerEvents(new ServerListener(this),this);

		getCommand("lg").setExecutor(new CommandLG(this));
		getCommand("adminlg").setExecutor(new AdminLG(this));
		//Chunk per tick


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
		TransportationLG start = new TransportationLG(this);
		start.runTaskTimer(this, 0, 20);
	}
	
	private void setWorld() {

		try{
			World world = Bukkit.getWorld("world");
			world.setPVP(false);
			world.setWeatherDuration(0);
			world.setThundering(false);
			world.setTime(0);
			world.setGameRuleValue("reducedDebugInfo", "true");
			world.setGameRuleValue("keepInventory", "true");
			world.setGameRuleValue("naturalRegeneration", "false");
			world.getWorldBorder().reset();

			int x=(int) world.getSpawnLocation().getX();
			int z=(int)  world.getSpawnLocation().getZ();

			if(getConfig().getBoolean("autoRoofedMiddle")){
				Location biome = WorldUtils.findBiome(Biome.ROOFED_FOREST, world, 2000);
				x=(int) biome.getX();
				z=(int) biome.getZ();
			}
			world.setSpawnLocation(x, 151,z);

			for(int i=-16;i<=16;i++) {

				for(int j=-16;j<=16;j++) {

					new Location(world, i+x, 150,j+z).getBlock().setType(Material.BARRIER);
					new Location(world, i+x, 154,j+z).getBlock().setType(Material.BARRIER);
				}
				for(int j=151;j<154;j++){
					new Location(world, i+x, j,z-16).getBlock().setType(Material.BARRIER);
					new Location(world, i+x, j,z+16).getBlock().setType(Material.BARRIER);
					new Location(world, x-16, j,i+z).getBlock().setType(Material.BARRIER);
					new Location(world, x+16, j,i+z).getBlock().setType(Material.BARRIER);
				}
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
		Title.sendTabTitle(player, text.getText(125), text.getText(184));
		new UpdateChecker(this, 73113).getVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
				Bukkit.getServer().getConsoleSender().sendMessage(text.getText(185));
            }
        });  
		
		if(isState(StateLG.LOBBY)) {
			board.registerNewTeam(playername);
			board.getTeam(playername).addEntry(playername);
			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(text.getText(1));
			player.teleport(player.getWorld().getSpawnLocation());
			playerLG.put(playername, new PlayerLG(player));
			player.setScoreboard(playerLG.get(playername).getScoreBoard());
			score.addPlayerSize();
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0,false,false));
		}
		else if (!playerLG.containsKey(playername)) {
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(text.getText(38));
		}
		else {
			player.setScoreboard(playerLG.get(playername).getScoreBoard());
			if(playerLG.get(playername).isState(State.MORT)) {
				player.setGameMode(GameMode.SPECTATOR);
			}
			else if (isState(StateLG.LG) &&  !playerLG.get(playername).hasKit()) {
				role_manage.recoverRolePower(playername);
			}
		}
		if(isState(StateLG.FIN)){
			fastboard.updateLines(score.scoreboard3);
		}
		optionlg.updateNameTag();
		optionlg.updateCompass();
	}

	public void setState(StateLG state) {
		this.state=state;
	}

	public StateLG getState() {return this.state;}

	public boolean isState(StateLG state) {
		return this.state==state;
	}


	public void setDay(Day day) {
		this.dayState =day;
	}


	public boolean isDay(Day day) {
		return this.dayState ==day;
	}


	public Day getDay() { return this.dayState;}
}
		


