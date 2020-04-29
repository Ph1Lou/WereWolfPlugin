package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.commandlg.AdminLG;
import io.github.ph1lou.pluginlg.commandlg.CommandLG;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.game.LobbyGenerator;
import io.github.ph1lou.pluginlg.savelg.LangLG;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class MainLG extends JavaPlugin implements GetWereWolfAPI {


    public final LangLG lang = new LangLG(this);
    public TextLG textEN;
    public TextLG textFR;
    public TextLG defaultLanguage;
    public GameManager currentGame;
    public final WereWolfApiImpl wereWolfApi = new WereWolfApiImpl(this);

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTask(this, this::enable);
    }

    @Override
    public void onDisable() {
        try {
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + currentGame.getWorld().getName()));
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
        setWorld();
        lang.init(this);
        getCommand("lg").setExecutor(new CommandLG(this, textFR));
        getCommand("a").setExecutor(new AdminLG(this));
        getCommand("ww").setExecutor(new CommandLG(this, textEN));
        currentGame = new GameManager(this);
    }


    @Override
    public WereWolfAPI getWereWolfAPI() {
        return wereWolfApi;
    }

    public void setWorld() {

        World world = Bukkit.getWorlds().get(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        world.setGameRuleValue("doFireTick", "false");
        int x = (int) world.getSpawnLocation().getX();
        int z = (int) world.getSpawnLocation().getZ();

        File file = new File(getDataFolder(), File.separator + "schematics" + File.separator + "ww.schematic");

        world.setSpawnLocation(x, 151, z);

        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null && file.exists()) {

            new LobbyGenerator(this, world);
        } else {
            for (int i = -16; i <= 16; i++) {

                for (int j = -16; j <= 16; j++) {

                    new Location(world, i + x, 150, j + z).getBlock().setType(Material.BARRIER);
                    new Location(world, i + x, 154, j + z).getBlock().setType(Material.BARRIER);
                }
                for (int j = 151; j < 154; j++) {
                    new Location(world, i + x, j, z - 16).getBlock().setType(Material.BARRIER);
                    new Location(world, i + x, j, z + 16).getBlock().setType(Material.BARRIER);
                    new Location(world, x - 16, j, i + z).getBlock().setType(Material.BARRIER);
                    new Location(world, x + 16, j, i + z).getBlock().setType(Material.BARRIER);
                }
            }
        }
    }
}
		


