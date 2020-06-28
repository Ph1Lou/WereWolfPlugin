package io.github.ph1lou.werewolfplugin.save;

import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.StuffManager;
import io.github.ph1lou.werewolfplugin.Main;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;

public class Stuff implements StuffManager {

    private final Map<String, List<ItemStack>> stuffRoles = new HashMap<>();
    private final List<ItemStack> death_loot = new ArrayList<>();
    private final Inventory start_loot = Bukkit.createInventory(null, 45);
    private final GameManager game;
    private final Main main;

    public Stuff(Main main, GameManager game) {
        this.main = main;
        this.game = game;
    }
    @Override
    public List<ItemStack> getDeathLoot() {
        return this.death_loot;
    }

    @Override
    public Inventory getStartLoot() {
        return this.start_loot;
    }

    @Override
    public void clearDeathLoot() {
        death_loot.clear();
    }

    @Override
    public void clearStartLoot() {
        start_loot.clear();
    }



    @Override
    public void addDeathLoot(ItemStack i) {
        death_loot.add(i);
    }

    @Override
    public void save(String configName) {

        for(Plugin plugin:main.getAddonsList()){

            int pos = 0;
            FileConfiguration config = getOrCreateCustomConfig(plugin, configName);
            if (config == null) {
                System.out.println("[pluginLG] backup error");
            }
            else{

                for (RoleRegister roleRegister:game.getRolesRegister()) {
                    if(roleRegister.getPlugin().equals(plugin)){
                        String key = roleRegister.getKey();
                        for (ItemStack i : stuffRoles.get(key)) {
                            config.set(key + "." + pos, i);
                            pos++;
                        }
                        pos = 0;
                    }
                }
                if(plugin.equals(main)){
                    for (ItemStack i : start_loot) {
                        config.set("start_loot." + pos, i);
                        pos++;
                    }
                    pos = 0;
                    for (ItemStack i : death_loot) {
                        config.set("death_loot." + pos, i);
                        pos++;
                    }
                }

                java.io.File file = new java.io.File(plugin.getDataFolder() + java.io.File.separator + "stuffs" + java.io.File.separator, configName + ".yml");
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void load(String configName) {
        loadStuff(configName);
        loadStuffStartAndDeath(configName);
    }


    public Map<String, List<ItemStack>> loadStuff(Plugin plugin, String configName) {

        Map<String, List<ItemStack>> temp = new HashMap<>();

        if (!(new java.io.File(plugin.getDataFolder() + java.io.File.separator + "stuffs" + java.io.File.separator, configName + ".yml")).exists()) {
            FileUtils.copy(plugin.getResource("stuffRole.yml"), plugin.getDataFolder() + java.io.File.separator + "stuffs" + java.io.File.separator + configName + ".yml");
        }
        FileConfiguration config = getOrCreateCustomConfig(plugin, configName);

        for (RoleRegister roleRegister:game.getRolesRegister()) {
            if(roleRegister.getPlugin().equals(plugin)){
                String key = roleRegister.getKey();
                temp.put(key, new ArrayList<>());
                if (config.getItemStack(key + ".0") != null) {
                    Set<String> sl = config.getConfigurationSection(key + ".").getKeys(false);
                    for (String s2 : sl) {
                        temp.get(key).add(config.getItemStack(key + "." + s2));
                    }
                }
            }
        }
        return temp;
    }

    public void loadStuffStartAndDeath(String configName) {

        start_loot.clear();
        death_loot.clear();
        FileConfiguration config = getOrCreateCustomConfig(main,configName);

        if (config.getItemStack("start_loot.0") != null) {
            Set<String> sl = config.getConfigurationSection("start_loot.").getKeys(false);

            for (String s : sl) {
                start_loot.addItem(config.getItemStack("start_loot." + s));
            }
        }
        if (config.getItemStack("death_loot.0") != null) {
            Set<String> sl = config.getConfigurationSection("death_loot").getKeys(false);
            for (String s : sl) {
                death_loot.add(config.getItemStack("death_loot." + s));
            }
        }

    }

    @Override
    public void loadStuffChill() {
        FileUtils.copy(main.getResource("stuffChill.yml"), main.getDataFolder() + java.io.File.separator + "stuffs" + java.io.File.separator + "stuffChill.yml");
        loadStuffStartAndDeath("stuffChill");
    }

    public FileConfiguration getOrCreateCustomConfig(Plugin plugin, String configName) {

        java.io.File customConfigFile = new java.io.File(plugin.getDataFolder() + java.io.File.separator + "stuffs" + java.io.File.separator, configName + ".yml");
        FileConfiguration customConfig = null;
        if (!customConfigFile.exists()) {
            try {
                FileUtils.createFile(customConfigFile);
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

    public void loadStuff(String configName){

        stuffRoles.clear();
        for(Plugin plugin:main.getAddonsList()){
            stuffRoles.putAll(loadStuff(plugin,configName));
        }
    }


    public void loadAllStuffDefault() {
        loadStuff("stuffRole");
    }

    public void loadAllStuffMeetUP() {
        FileUtils.copy(main.getResource("stuffMeetUp.yml"), main.getDataFolder() + java.io.File.separator + "stuffs" + java.io.File.separator + "stuffMeetUp.yml");
        loadStuff("stuffMeetUp");
        loadStuffStartAndDeath("stuffMeetUp");
    }

    @Override
    public Map<String, List<ItemStack>> getStuffRoles() {
        return stuffRoles;
    }
}
