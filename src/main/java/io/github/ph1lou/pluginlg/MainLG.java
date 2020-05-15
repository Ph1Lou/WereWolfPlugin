package io.github.ph1lou.pluginlg;


import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.LangLG;
import io.github.ph1lou.pluginlg.utils.WorldUtils;
import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class MainLG extends JavaPlugin implements GetWereWolfAPI {


    public final LangLG lang = new LangLG(this);

    public GameManager currentGame;


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
        setWorld();
        lang.init();
        currentGame = new GameManager(this);
    }


    @Override
    public WereWolfAPI getWereWolfAPI() {
        return currentGame;
    }

    public void setWorld() {

        World world = Bukkit.getWorlds().get(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        world.setGameRuleValue("doFireTick", "false");
        int x = (int) world.getSpawnLocation().getX();
        int z = (int) world.getSpawnLocation().getZ();

        world.setSpawnLocation(x, 151, z);

        if(getConfig().getBoolean("default_lobby")){
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
		


