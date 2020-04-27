package io.github.ph1lou.pluginlg;


import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.pluginlg.commandlg.AdminLG;
import io.github.ph1lou.pluginlg.commandlg.CommandLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.listener.ServerListener;
import io.github.ph1lou.pluginlg.savelg.LangLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.tasks.HubTask;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainLG extends JavaPlugin implements GetWereWolfAPI {


    public final LangLG lang = new LangLG(this);
    public TextLG textEN;
    public TextLG textFR;
    public TextLG defaultLanguage;
    public final Map<UUID, GameManager> listGames = new HashMap<>();
    public Map<UUID, FastBoard> boards= new HashMap<>();
    public Inventory hubTool;
    public WereWolfApiImpl wereWolfApi = new WereWolfApiImpl(this);
    public HubTask hubTask;
    public Scoreboard board;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTask(this, this::enable);
    }

    @Override
    public void onDisable() {
        List<World> worlds = Bukkit.getWorlds().subList(1,Bukkit.getWorlds().size());
        try {
            for(World world: worlds){
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + world.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
	public void onLoad(){
		WorldUtils.patchBiomes();
	}


	public void enable() {

        saveDefaultConfig();

        lang.init(this);


        getCommand("lg").setExecutor(new CommandLG(this,textFR));
        getCommand("adminWW").setExecutor(new AdminLG(this));
        getCommand("ww").setExecutor(new CommandLG(this,textEN));
        Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        hubTask = new HubTask(this);
        hubTask.initInventory();
        hubTask.runTaskTimer(this, 0, 20);
    }


    @Override
    public WereWolfAPI getWereWolfAPI() {
        return wereWolfApi;
    }
}
		


