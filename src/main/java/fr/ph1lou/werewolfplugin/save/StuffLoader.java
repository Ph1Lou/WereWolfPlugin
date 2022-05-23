package fr.ph1lou.werewolfplugin.save;

import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfapi.versions.VersionUtils;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

public class StuffLoader {


    public static void deleteStuff(Main main, String replace) {
        //todo
    }

    public static void loadAllStuffDefault(Main main, WereWolfAPI game) {
        FileUtils_.copy(main.getResource("stuffRole.yml"),
                main.getDataFolder() +
                        File.separator + "stuffs" + File.separator + "stuffRole.yml");
        loadStuffRole(main, game, "stuffRole");
    }

    public static  void loadAllStuffMeetUP(Main main, WereWolfAPI game) {
        FileUtils_.copy(main.getResource("stuffMeetUp.yml"),
                main.getDataFolder() +
                        File.separator + "stuffs" + File.separator + "stuffMeetUp.yml");
        loadStuffRole(main, game, "stuffMeetUp");
        loadStuffStartAndDeath(main, game, "stuffMeetUp");
    }

    public static void loadStuffChill(Main main, WereWolfAPI game) {
        FileUtils_.copy(main.getResource("stuffChill.yml"), main.getDataFolder() + File.separator + "stuffs" + File.separator + "stuffChill.yml");
        loadStuffStartAndDeath(main, game, "stuffChill");
    }

    public static void loadStuff(Main main, GameManager game, String configName) {
        loadStuffRole(main, game, configName);
        loadStuffStartAndDeath(main, game,configName);
    }

    public static void saveStuff(Main main, String configName) {

        GameManager game = (GameManager) main.getWereWolfAPI();

        for(Wrapper<JavaPlugin, ModuleWerewolf> addon:Register.get().getModulesRegister()){
            addon.getObject()
                    .ifPresent(javaPlugin -> saveStuffRole(
                            javaPlugin,
                            game,
                            configName,
                            addon.getMetaDatas().key()));
        }

        saveStuffRole(main, game, configName, "werewolf.name");
        saveStuffStartAndDeath(main, game, configName);

    }

    private static void saveStuffStartAndDeath(Main main, WereWolfAPI game, String configName) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(main, configName);

        if (config == null) {
            Bukkit.getLogger().warning("[pluginLG] backup error");
            return;
        }

        for (ItemStack i : game.getStuffs().getStartLoot()) {
            setItem(config,"start_loot." + pos,i);
            pos++;
        }
        pos = 0;
        for (ItemStack i : game.getStuffs().getDeathLoot()) {
            setItem(config,"death_loot." + pos, i);
            pos++;
        }

        File file = new File(main.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void saveStuffRole(JavaPlugin plugin,
                                      WereWolfAPI game,
                                      String configName,
                                      String addonKey) {

        int pos = 0;
        FileConfiguration config = getOrCreateCustomConfig(plugin, configName);
        if (config == null) {
            Bukkit.getLogger().warning("[pluginLG] backup error");
            return;
        }

        for (Wrapper<IRole, Role> roleRegister: Register.get().getRolesRegister()) {
            if(roleRegister.getAddonKey().equals(addonKey)){
                String key = roleRegister.getMetaDatas().key();
                for (ItemStack i : game.getStuffs().getStuffRole(key)) {
                    setItem(config,key + "." + pos,i);
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

    public static Map<String, List<ItemStack>> loadStuff(Plugin plugin, String addonKey, String configName) {

        Map<String, List<ItemStack>> temp = new HashMap<>();

        if (!(new File(plugin.getDataFolder() + File.separator + "stuffs" + File.separator, configName + ".yml")).exists()) {
            FileUtils_.copy(plugin.getResource("stuffRole.yml"), plugin.getDataFolder() + File.separator + "stuffs" + File.separator + configName + ".yml");
        }
        FileConfiguration config = getOrCreateCustomConfig(plugin, configName);

        for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {

            if (roleRegister.getAddonKey().equals(addonKey)) {
                String key = roleRegister.getMetaDatas().key();
                temp.put(key, new ArrayList<>());
                ConfigurationSection configurationSection = config.getConfigurationSection(key);
                if (configurationSection != null) {
                    Set<String> sl = configurationSection.getKeys(false);
                    for (String s2 : sl) {
                        temp.get(key).add(getItem(config,key + "." + s2));
                    }
                }
            }
        }
        return temp;
    }

    public static void loadStuffStartAndDeath(Main main, WereWolfAPI game, String configName) {

        game.getStuffs().clearDeathLoot();
        game.getStuffs().clearStartLoot();

        FileConfiguration config = getOrCreateCustomConfig(main, configName);

        ConfigurationSection configurationSection = config.getConfigurationSection("start_loot");

        if (configurationSection != null) {
            Set<String> sl = configurationSection.getKeys(false);

            for (String s : sl) {
                game.getStuffs().addStartLoot(getItem(config,"start_loot." + s));
            }
        }

        configurationSection = config.getConfigurationSection("death_loot");

        if(configurationSection!=null){
            Set<String> sl = configurationSection.getKeys(false);
            for (String s : sl) {
                game.getStuffs().addDeathLoot(getItem(config,"death_loot." + s));
            }
        }
    }

    private static ItemStack getItem(FileConfiguration file, String path){

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

    private static void setItem(FileConfiguration file, String path,@Nullable ItemStack item){

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

    private static FileConfiguration getOrCreateCustomConfig(Plugin plugin, String configName) {
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

    public static void loadStuffRole(Main main, WereWolfAPI game, String configName) {

        game.getStuffs().clearStuffRoles();

        Register.get()
                .getModulesRegister()
                .forEach(addonRegister -> addonRegister.getObject()
                        .ifPresent(javaPlugin -> loadStuff(javaPlugin, addonRegister.getAddonKey(), configName)
                                .forEach((key, itemStacks) -> game.getStuffs().setStuffRole(key, itemStacks))));


        loadStuff(main, Main.KEY, configName)
                .forEach((key, itemStacks) -> game.getStuffs().setStuffRole(key, itemStacks));
    }
}
