package fr.ph1lou.werewolfplugin.save;

import fr.ph1lou.werewolfapi.annotations.Addon;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfapi.game.IStuffManager;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class Stuff implements IStuffManager {

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

        for(Wrapper<JavaPlugin, Addon> addon:main.getRegisterManager().getAddonsRegister()){
            addon.getObject()
                    .ifPresent(javaPlugin -> saveStuffRole(javaPlugin,
                            configName,
                            addon.getMetaDatas().key()));
        }

        saveStuffRole(main,configName, "werewolf.name");
        saveStuffStartAndDeath(configName);

    }

    private void saveStuffStartAndDeath(String configName) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(main, configName);

        if (config == null) {
            Bukkit.getLogger().warning("[pluginLG] backup error");
            return;
        }

        for (ItemStack i : startLoot) {
            this.setItem(config,"start_loot." + pos,i);
            pos++;
        }
        pos = 0;
        for (ItemStack i : deathLoot) {
            this.setItem(config,"death_loot." + pos, i);
            pos++;
        }

        File file = new File(main.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveStuffRole(JavaPlugin plugin,String configName, String addonKey) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(plugin, configName);
        if (config == null) {
            Bukkit.getLogger().warning("[pluginLG] backup error");
            return;
        }

        for (Wrapper<IRole, Role> roleRegister:main.getRegisterManager().getRolesRegister()) {
            if(roleRegister.getAddonKey().equals(addonKey)){
                String key = roleRegister.getMetaDatas().key();
                for (ItemStack i : stuffRoles.get(key)) {
                    this.setItem(config,key + "." + pos,i);
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

        for (Wrapper<IRole, Role> roleRegister : main.getRegisterManager().getRolesRegister()) {

            if (roleRegister.getAddonKey().equals(addonKey)) {
                String key = roleRegister.getMetaDatas().key();
                temp.put(key, new ArrayList<>());
                ConfigurationSection configurationSection = config.getConfigurationSection(key);
                if (configurationSection != null) {
                    Set<String> sl = configurationSection.getKeys(false);
                    for (String s2 : sl) {
                        temp.get(key).add(this.getItem(config,key + "." + s2));
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
                startLoot.addItem(this.getItem(config,"start_loot." + s));
            }
        }

        configurationSection = config.getConfigurationSection("death_loot");

        if(configurationSection!=null){
            Set<String> sl = configurationSection.getKeys(false);
            for (String s : sl) {
                deathLoot.add(this.getItem(config,"death_loot." + s));
            }
        }
    }

    private ItemStack getItem(FileConfiguration file, String path){

        ItemStack item;
        if(file.contains(path+".potion_data")){
            item=file.getItemStack(path+".item");
        }
        else{
            item=file.getItemStack(path);
        }
        if(item ==null){
            item=new ItemStack(Material.AIR);
        }

        if (file.contains(path+".potion_data")) {
            int amount = item.getAmount();
            item = VersionUtils.getVersionUtils()
                    .getPotionItem((short) file.getInt(path+".potion_data"));
            item.setAmount(amount);
        }
        return item;
    }

    private void setItem(FileConfiguration file, String path,@Nullable ItemStack item){

        if(item == null){
            file.set(path,null);
            return;
        }

        short id = VersionUtils.getVersionUtils().generatePotionId(item);

        if(id != 0){
            file.set(path+".potion_data",id);
            file.set(path+".item",item);
        }
        else{
            file.set(path,item);
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

        main.getRegisterManager()
                .getAddonsRegister()
                .forEach(addonRegister -> addonRegister.getObject()
                        .ifPresent(javaPlugin -> stuffRoles.putAll(loadStuff(javaPlugin,
                        addonRegister.getAddonKey(), configName))));
        stuffRoles.putAll(loadStuff(main,
                "werewolf.name",
                configName));
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
