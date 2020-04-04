package io.github.ph1lou.pluginlg.savelg;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.enumlg.RoleLG;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StuffLG {

    public final Map<RoleLG, List<ItemStack>> role_stuff = new HashMap<>();
    private final List<ItemStack> death_loot = new ArrayList<>();
    private final List<ItemStack> start_loot = new ArrayList<>();
    final MainLG main;

    public StuffLG(MainLG main) {
        this.main = main;
    }

    public List<ItemStack> getDeathLoot() {
        return this.death_loot;
    }

    public List<ItemStack> getStartLoot() {
        return this.start_loot;
    }

    public void clearDeathLoot() {
        death_loot.clear();
    }

    public void clearStartLoot() {
        start_loot.clear();
    }

    public void addDeathLoot(ItemStack i) {
        death_loot.add(i);
    }

    public void addStartLoot(ItemStack i) {
        start_loot.add(i);
    }

    public void save(String configName) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(configName);
        if (config == null) {
            System.out.println("[pluginLG] backup error");
            return;
        }
        for (RoleLG role : RoleLG.values()) {
            for (ItemStack i : role_stuff.get(role)) {
                config.set(role.toString() + "." + pos, i);
                pos++;
            }
            pos = 0;
        }
        for (ItemStack i : start_loot) {
            config.set("start_loot." + pos, i);
            pos++;
        }
        pos = 0;
        for (ItemStack i : death_loot) {
            config.set("death_loot." + pos, i);
            pos++;
        }
        File file = new File(main.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void load(String configName) {
        loadStuff(configName);
        loadStuffStartAndDeath(configName);
    }


    public void loadStuff(String configName) {

        role_stuff.clear();
        if (!(new File(main.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml")).exists()) {
            main.filelg.copy(main.getClass().getResourceAsStream("/stuffRole.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + configName + ".yml");
        }
        FileConfiguration config = getOrCreateCustomConfig(configName);
        for (RoleLG role : RoleLG.values()) {
            role_stuff.put(role, new ArrayList<>());
            if (config.getItemStack(role.toString() + ".0") != null) {
                Set<String> sl = config.getConfigurationSection(role.toString() + ".").getKeys(false);
                for (String s : sl) {
                    role_stuff.get(role).add(config.getItemStack(role.toString() + "." + s));
                }
            }
        }
    }

    public void loadStuffStartAndDeath(String configName) {

        start_loot.clear();
        death_loot.clear();
        FileConfiguration config = getOrCreateCustomConfig(configName);

        if (config.getItemStack("start_loot.0") != null) {
            Set<String> sl = config.getConfigurationSection("start_loot.").getKeys(false);

            for (String s : sl) {
                start_loot.add(config.getItemStack("start_loot." + s));
            }
        }
        if (config.getItemStack("death_loot.0") != null) {
            Set<String> sl = config.getConfigurationSection("death_loot").getKeys(false);
            for (String s : sl) {
                death_loot.add(config.getItemStack("death_loot." + s));
            }
        }

    }

    public void loadStuffDefault() {
        main.filelg.copy(main.getClass().getResourceAsStream("/stuffRole.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + "stuffRole.yml");
        loadStuff("stuffRole");
    }

    public void loadStuffMeetUP() {
        main.filelg.copy(main.getClass().getResourceAsStream( "/stuffMeetUp.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + "stuffMeetUp.yml");
        load("stuffMeetUp");
    }

    public void loadStuffChill() {
        main.filelg.copy(main.getClass().getResourceAsStream("/stuffChill.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + "stuffChill.yml");
        loadStuffStartAndDeath("stuffChill");
    }

    public FileConfiguration getOrCreateCustomConfig(String configName) {

        File customConfigFile = new File(main.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml");
        FileConfiguration customConfig = null;
        if (!customConfigFile.exists()) {
            try {
                main.filelg.createFile(customConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            customConfig = new YamlConfiguration();
            customConfig.load(customConfigFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return customConfig;
    }


}
