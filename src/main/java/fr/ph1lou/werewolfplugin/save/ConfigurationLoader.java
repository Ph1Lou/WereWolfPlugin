package fr.ph1lou.werewolfplugin.save;

import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.role.interfaces.IRole;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.utils.Wrapper;
import fr.ph1lou.werewolfplugin.Main;
import fr.ph1lou.werewolfplugin.Register;
import fr.ph1lou.werewolfplugin.game.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigurationLoader {

    public static Configuration loadConfig(Main main, GameManager game, String name){

        Map<String, StorageConfiguration> configurationMap = new HashMap<>();

        File file = new File(main.getDataFolder()
                + File.separator + "configs"
                + File.separator, name + ".json");

        configurationMap.put(Main.KEY,
                loadConfig(main, name).setAddonKey(Main.KEY));

        for (Wrapper<JavaPlugin, ModuleWerewolf> addonWrapper : Register.get().getModulesRegister()) {
            addonWrapper.getObject()
                    .ifPresent(javaPlugin -> configurationMap.put(addonWrapper.getAddonKey(),
                            loadConfig(javaPlugin, name).setAddonKey(addonWrapper.getAddonKey())));
        }

        if(!file.exists()){
            return new Configuration().setConfigurations(configurationMap);
        }

        Configuration config = Serializer.deserialize(FileUtils_.loadContent(file))
                .setConfigurations(configurationMap);


        game.setRoleInitialSize(0);
        game.getModerationManager().checkQueue();
        BukkitUtils.scheduleSyncDelayedTask(() -> game.getListenersLoader().update());


        for (Wrapper<IRole, Role> roleRegister : Register.get().getRolesRegister()) {
            String key = roleRegister.getMetaDatas().key();
            game.setRoleInitialSize(game.getRoleInitialSize() + config.getRoleCount(key));
        }

        return config;
    }
    public static StorageConfiguration loadConfig(JavaPlugin plugin, String name){

        File file = new File(plugin.getDataFolder()
                + File.separator + "values"
                + File.separator, name + ".json");

        if(!file.exists()){
            return new StorageConfiguration();
        }

        return Serializer.deserializeConfiguration(FileUtils_.loadContent(file));
    }

    public static void saveConfig(Main main, String name){

        GameManager game = (GameManager) main.getWereWolfAPI();

        File file = new File(main.getDataFolder()
                + File.separator + "configs"
                + File.separator, name + ".json");

        FileUtils_.save(file, Serializer.serialize(game.getConfig()));

        ((Configuration)game.getConfig()).getStorageConfigurations()
                        .forEach(storageConfiguration -> Register.get()
                                .getModulesRegister()
                                .stream()
                                .filter(javaPluginAddonWrapper -> javaPluginAddonWrapper.getAddonKey()
                                        .equals(storageConfiguration.getAddonKey()))
                                .map(Wrapper::getObject)
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

}
