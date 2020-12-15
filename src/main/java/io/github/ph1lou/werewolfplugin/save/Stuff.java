package io.github.ph1lou.werewolfplugin.save;

import io.github.ph1lou.werewolfapi.StuffManager;
import io.github.ph1lou.werewolfapi.registers.AddonRegister;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Stuff implements StuffManager {

    private final Map<String, List<ItemStack>> stuffRoles = new HashMap<>();

    private final Map<UUID, Inventory> tempStuff = new HashMap<>();
    private final List<ItemStack> deathLoot = new ArrayList<>();
    private final Inventory startLoot = Bukkit.createInventory(null, 45);
    private final Main main;


    public Stuff(Main main) {
        this.main = main;
    }

    @Override
    public List<ItemStack> getDeathLoot() {
        return this.deathLoot;
    }

    @Override
    public Inventory getStartLoot() {
        return this.startLoot;
    }

    @Override
    public void clearDeathLoot() {
        deathLoot.clear();
    }

    @Override
    public void clearStartLoot() {
        startLoot.clear();
    }

    @Override
    public void addDeathLoot(ItemStack i) {
        deathLoot.add(i);
    }

    @Override
    public void save(String configName) {

        for(AddonRegister addon:main.getRegisterManager().getAddonsRegister()){
            Plugin plugin = addon.getPlugin();
            saveStuffRole(plugin,configName,addon.getAddonKey());
        }

        saveStuffRole(main,configName,"werewolf.name");
        saveStuffStartAndDeath(configName);

    }

    private void saveStuffStartAndDeath(String configName) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(main, configName);
        if (config == null) {
            System.out.println("[pluginLG] backup error");
            return;
        }

        for (ItemStack i : startLoot) {
            config.set("start_loot." + pos, i);
            pos++;
        }
        pos = 0;
        for (ItemStack i : deathLoot) {
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

    private void saveStuffRole(Plugin plugin,String configName, String keyAddon) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(plugin, configName);
        if (config == null) {
            System.out.println("[pluginLG] backup error");
            return;
        }

        for (RoleRegister roleRegister:main.getRegisterManager().getRolesRegister()) {
            if(roleRegister.getAddonKey().equals(keyAddon)){
                String key = roleRegister.getKey();
                for (ItemStack i : stuffRoles.get(key)) {
                    config.set(key + "." + pos, i);
                    pos++;
                }
                pos = 0;
            }
        }

        File file = new File(plugin.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void load(String configName) {
        loadStuff(configName);
        loadStuffStartAndDeath(configName);
    }


    public Map<String, List<ItemStack>> loadStuff(Plugin plugin, String addonKey, String configName) {

        Map<String, List<ItemStack>> temp = new HashMap<>();

        if (!(new File(plugin.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml")).exists()) {
            FileUtils_.copy(plugin.getResource("stuffRole.yml"), plugin.getDataFolder() + File.separator + "stuffs" + File.separator + configName + ".yml");
        }
        FileConfiguration config = getOrCreateCustomConfig(plugin, configName);

        for (RoleRegister roleRegister : main.getRegisterManager().getRolesRegister()) {

            if (roleRegister.getAddonKey().equals(addonKey)) {
                String key = roleRegister.getKey();
                temp.put(key, new ArrayList<>());
                ConfigurationSection configurationSection = config.getConfigurationSection(key);
                if (configurationSection != null) {
                    Set<String> sl = configurationSection.getKeys(false);
                    for (String s2 : sl) {
                        temp.get(key).add(config.getItemStack(key + "." + s2));
                    }
                }
            }
        }
        return temp;
    }

    public void loadStuffStartAndDeath(String configName) {

        startLoot.clear();
        deathLoot.clear();
        FileConfiguration config = getOrCreateCustomConfig(main, configName);

        ConfigurationSection configurationSection = config.getConfigurationSection("start_loot");

        if (configurationSection != null) {
            Set<String> sl = configurationSection.getKeys(false);

            for (String s : sl) {
                ItemStack item = config.getItemStack("start_loot." + s);
                if (item != null) {
                    startLoot.addItem(item);
                }
            }
        }

        configurationSection = config.getConfigurationSection("death_loot");

        if(configurationSection!=null){
            Set<String> sl = configurationSection.getKeys(false);
            for (String s : sl) {
                deathLoot.add(config.getItemStack("death_loot." + s));
            }
        }
    }

    @Override
    public void loadStuffChill() {
        FileUtils_.copy(main.getResource("stuffChill.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + "stuffChill.yml");
        loadStuffStartAndDeath("stuffChill");
    }

    public FileConfiguration getOrCreateCustomConfig(Plugin plugin, String configName) {
        File customConfigFile = new File(plugin.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml");
        FileConfiguration customConfig = null;
        if (!customConfigFile.exists()) {
            try {
                FileUtils_.createFile(customConfigFile);
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

    public void loadStuff(String configName) {

        stuffRoles.clear();

        main.getRegisterManager().getAddonsRegister().forEach(addonRegister -> stuffRoles.putAll(loadStuff(addonRegister.getPlugin(), addonRegister.getAddonKey(), configName)));
        stuffRoles.putAll(loadStuff(main, "werewolf.name", configName));
    }


    public void loadAllStuffDefault() {
        loadStuff("stuffRole");
    }

    public void loadAllStuffMeetUP() {
        FileUtils_.copy(main.getResource("stuffMeetUp.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + "stuffMeetUp.yml");
        loadStuff("stuffMeetUp");
        loadStuffStartAndDeath("stuffMeetUp");
    }

    @Override
    public Map<String, List<ItemStack>> getStuffRoles() {
        return stuffRoles;
    }

    @Override
    public Map<UUID, Inventory> getTempStuff() {
        return tempStuff;
    }
}
