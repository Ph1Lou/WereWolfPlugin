package fr.ph1lou.werewolfplugin.save;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.game.Configuration;
import fr.ph1lou.werewolfplugin.game.GameManager;
import fr.ph1lou.werewolfplugin.game.StorageConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigurationLoader {

    public static void deleteConfig(String name){

        Main main = JavaPlugin.getPlugin(Main.class);
        File fileConfigPlugin = new File(main.getDataFolder() + File.separator + "configs", name+ ".json");
        fileConfigPlugin.delete();

        main.getRegisterManager().getModulesRegister()
                .stream()
                .map(javaPluginModuleWerewolfWrapper -> main.getRegisterManager().getAddon(javaPluginModuleWerewolfWrapper.getAddonKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(javaPlugin -> {
                    File fileValuesAddon = new File(javaPlugin.getDataFolder()
                            + File.separator + "values"
                            + File.separator, name + ".json");

                    fileValuesAddon.delete();
                });

    }

    public static void saveConfig(WereWolfAPI game, String name){

        Main main = JavaPlugin.getPlugin(Main.class);

        File file = new File(main.getDataFolder()
                + File.separator + "configs"
                + File.separator, name + ".json");

        FileUtils_.save(file, Serializer.serialize(game.getConfig()));

        ((Configuration)game.getConfig()).getStorageConfigurations()
                .forEach(storageConfiguration -> main.getRegisterManager()
                        .getModulesRegister()
                        .stream()
                        .filter(javaPluginAddonWrapper -> javaPluginAddonWrapper.getAddonKey()
                                .equals(storageConfiguration.getAddonKey()))
                        .map(javaPluginModuleWerewolfWrapper -> main.getRegisterManager().getAddon(javaPluginModuleWerewolfWrapper.getAddonKey()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .ifPresent(javaPlugin -> {
                            File file1 = new File(javaPlugin.getDataFolder()
                                    + File.separator + "values"
                                    + File.separator, name + ".json");
                            FileUtils_.save(file1, Serializer.serialize(storageConfiguration));
                        }));

    }

    public static void loadConfig(GameManager game, String name){

        Main main = JavaPlugin.getPlugin(Main.class);

        Map<String, StorageConfiguration> configurationMap = new HashMap<>();

        File file = new File(main.getDataFolder()
                + File.separator + "configs"
                + File.separator, name + ".json");

        main.getRegisterManager().getModulesRegister()
                .forEach(javaPluginModuleWerewolfWrapper -> main.getRegisterManager().getAddon(javaPluginModuleWerewolfWrapper.getAddonKey())
                        .ifPresent(javaPlugin -> configurationMap.put(javaPluginModuleWerewolfWrapper.getAddonKey(),
                                loadConfig(javaPlugin, name).setAddonKey(javaPluginModuleWerewolfWrapper.getAddonKey(), game))));


        Configuration config;

        if(!file.exists()){
            config =  new Configuration().setConfigurations(configurationMap);
        }
        else{
            config = Serializer.deserialize(FileUtils_.loadContent(file))
                    .setConfigurations(configurationMap);
        }
        game.setConfig(config);
        game.setRoleInitialSize(0);
        game.getModerationManager().checkQueue();
        game.getListenersManager().updateListeners();

        for (Wrapper<IRole, Role> roleRegister : main.getRegisterManager().getRolesRegister()) {
            String key = roleRegister.getMetaDatas().key();
            game.setRoleInitialSize(game.getRoleInitialSize() + config.getRoleCount(key));
        }
    }
    private static StorageConfiguration loadConfig(JavaPlugin plugin, String name){

        File file = new File(plugin.getDataFolder()
                + File.separator + "values"
                + File.separator, name + ".json");

        if(!file.exists()){
            return new StorageConfiguration();
        }

        return Serializer.deserializeConfiguration(FileUtils_.loadContent(file));
    }
}
