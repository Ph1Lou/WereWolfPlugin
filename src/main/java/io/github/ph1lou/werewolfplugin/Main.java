package io.github.ph1lou.werewolfplugin;


import io.github.ph1lou.pluginlgapi.Commands;
import io.github.ph1lou.pluginlgapi.GetWereWolfAPI;
import io.github.ph1lou.pluginlgapi.RoleRegister;
import io.github.ph1lou.pluginlgapi.WereWolfAPI;
import io.github.ph1lou.pluginlgapi.enumlg.Category;
import io.github.ph1lou.werewolfplugin.classesroles.neutralroles.*;
import io.github.ph1lou.werewolfplugin.classesroles.villageroles.*;
import io.github.ph1lou.werewolfplugin.classesroles.werewolfroles.*;
import io.github.ph1lou.werewolfplugin.game.GameManager;
import io.github.ph1lou.werewolfplugin.savelg.FileLG;
import io.github.ph1lou.werewolfplugin.savelg.LangLG;
import io.github.ph1lou.werewolfplugin.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin implements GetWereWolfAPI, Listener {


    public final LangLG lang = new LangLG(this);

    public GameManager currentGame;
    public final List<RoleRegister> rolesRegister = new ArrayList<>();
    private final Map<String, String> extraTexts=new HashMap<>();
    private final List<Plugin> listAddons = new ArrayList<>();
    private final Map<String, Commands> listCommands = new HashMap<>();
    private final Map<String, Commands> listAdminCommands = new HashMap<>();


    @Override
    public Map<String, Commands> getListCommands() {
        return listCommands;
    }

    @Override
    public Map<String, Commands> getListAdminCommands() {
        return listAdminCommands;
    }

    @Override
    public List<Plugin> getAddonsList() {
        return this.listAddons;
    }


    @Override
    public void onEnable() {
        saveDefaultConfig();
        lang.init();
        registerRole();
        listAddons.add(this);
        Bukkit.getPluginManager().registerEvents( this, this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            setWorld();
            currentGame = new GameManager(this);
            currentGame.createMap();
        },60);
    }

    @Override
	public void onLoad(){
		WorldUtils.patchBiomes();
	}


    @Override
    public WereWolfAPI getWereWolfAPI() {
        return currentGame;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        if(currentGame==null){
            player.kickPlayer("Waiting Addons");
        }
    }

    @Override
    public List<RoleRegister> getRegisterRoles(){
        return this.rolesRegister;
    }

    @Override
    public Map<String, String> getExtraTexts() {
        return this.extraTexts;
    }


    public void setWorld() {

        World world = Bukkit.getWorlds().get(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        try{
            if(world.getWorldBorder()!=null){
                world.getWorldBorder().reset();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("reducedDebugInfo", "true");
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("keepInventory", "true");
        int x = world.getSpawnLocation().getBlockX();
        int z = world.getSpawnLocation().getBlockZ();

        if(getConfig().getBoolean("default_lobby")){

            world.setSpawnLocation(x, 151, z);

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

    @Override
    public void loadTranslation(Plugin plugin, String defaultLang){

        plugin.saveConfig();
        String lang = plugin.getConfig().getString("lang");
        FileLG.copy(plugin.getResource(defaultLang+".json"),plugin.getDataFolder()+File.separator+"languages"+File.separator+defaultLang+".json");
        File file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, lang+".json");
        if(!file.exists()){
            File fileDefault = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator, lang+".json");
            String content = FileLG.loadContent(fileDefault);
            FileLG.save(file,content);
        }
        extraTexts.putAll( this.lang.loadTranslations(FileLG.loadContent(file)));
    }

    private void registerRole() {
        try {
            new RoleRegister(this, this,"werewolf.role.cupid.display").registerRole(Cupid.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.werewolf.display").registerRole(WereWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.falsifier_werewolf.display").registerRole(FalsifierWereWolf.class).addCategory(Category.WEREWOLF).create();

            new RoleRegister(this, this,"werewolf.role.infect_father_of_the_wolves.display").registerRole( InfectFatherOfTheWolves.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.witch.display").registerRole(Witch.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.elder.display").registerRole(Elder.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.naughty_little_wolf.display").registerRole(NaughtyLittleWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.fox.display").registerRole(Fox.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.mischievous_werewolf.display").registerRole(MischievousWereWolf.class).addCategory(Category.WEREWOLF).create();
            new RoleRegister(this, this,"werewolf.role.little_girl.display").registerRole(LittleGirl.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.wild_child.display").registerRole(WildChild.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.citizen.display").registerRole(Citizen.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.comedian.display").registerRole(Comedian.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.miner.display").registerRole(Miner.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.sister.display").registerRole(Sister.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.siamese_twin.display").registerRole(SiameseTwin.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.raven.display").registerRole(Raven.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.protector.display").registerRole(Protector.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.trapper.display").registerRole(Trapper.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.troublemaker.display").registerRole(Troublemaker.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.bear_trainer.display").registerRole(BearTrainer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.seer.display").registerRole(Seer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.chatty_seer.display").registerRole(ChattySeer.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.detective.display").registerRole(Detective.class).addCategory(Category.VILLAGER).create();

            new RoleRegister(this, this,"werewolf.role.succubus.display").registerRole(Succubus.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.angel.display").registerRole(Angel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.fallen_angel.display").registerRole(FallenAngel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.guardian_angel.display").registerRole(GuardianAngel.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.assassin.display").registerRole(Assassin.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.serial_killer.display").registerRole(SerialKiller.class).addCategory(Category.NEUTRAL).create();

            new RoleRegister(this, this,"werewolf.role.amnesiac_werewolf.display").registerRole(AmnesicWerewolf.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.white_werewolf.display").registerRole(WhiteWereWolf.class).addCategory(Category.NEUTRAL).create();

            new RoleRegister(this, this,"werewolf.role.thief.display").registerRole(Thief.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.flute_player.display").registerRole(FlutePlayer.class).addCategory(Category.NEUTRAL).create();
            new RoleRegister(this, this,"werewolf.role.librarian.display").registerRole(Librarian.class).addCategory(Category.VILLAGER).create();
            new RoleRegister(this, this,"werewolf.role.villager.display").registerRole(Villager.class).addCategory(Category.VILLAGER).create();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

}
		


