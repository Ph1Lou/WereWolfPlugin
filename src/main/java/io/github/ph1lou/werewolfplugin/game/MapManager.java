package io.github.ph1lou.werewolfplugin.game;

import io.github.ph1lou.werewolfapi.MapManagerAPI;
import io.github.ph1lou.werewolfapi.versions.VersionUtils;
import io.github.ph1lou.werewolfplugin.worldloader.WorldFillTask;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MapManager implements MapManagerAPI {

    private final GameManager game;
    private World world;
    private WorldFillTask wft = null;

    public MapManager(GameManager game) {
        this.game = game;
    }


    public void generateMap(CommandSender sender, int mapRadius) {

        if (getWorld() == null) {
            createMap();
        }
        int chunksPerRun = 20;
        if (wft == null || wft.getPercentageCompleted() == 100) {
            wft = new WorldFillTask(game, chunksPerRun, mapRadius);
            wft.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(game.getMain(), wft, 1, 1));
            sender.sendMessage(game.translate("werewolf.commands.admin.generation.perform"));
        } else sender.sendMessage(game.translate("werewolf.commands.admin.generation.already_start"));
    }

    public void createMap() {
        WorldCreator wc = new WorldCreator("werewolf");
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
        this.world = wc.createWorld();
        setWorld();
    }

    @Override
    public void deleteMap() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        try {
            Bukkit.unloadWorld(world, false);
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + world.getName()));
            this.world = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWorld() {

        try {
            world.setWeatherDuration(0);
            world.setThundering(false);
            world.setTime(0);
            world.setPVP(false);
            VersionUtils.getVersionUtils().setGameRuleValue(world, "doFireTick", false);
            VersionUtils.getVersionUtils().setGameRuleValue(world, "reducedDebugInfo", true);
            VersionUtils.getVersionUtils().setGameRuleValue(world, "naturalRegeneration", false);
            VersionUtils.getVersionUtils().setGameRuleValue(world, "keepInventory", true);
            world.save();
            int x = world.getSpawnLocation().getBlockX();
            int z = world.getSpawnLocation().getBlockZ();
            try {
                world.getWorldBorder().reset();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (game.getMain().getConfig().getBoolean("autoRoofedMiddle")) {
                Location biome = VersionUtils.getVersionUtils().findBiome(world);
                x = biome.getBlockX();
                z = biome.getBlockZ();
            }
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
        } catch (Exception ignored) {
        }
    }

    @Override
    public void generateMap(int mapRadius) {
        generateMap(Bukkit.getConsoleSender(), mapRadius);
    }


    @Override
    public void transportation(UUID playerUUID, double d, String message) {

        Player player = Bukkit.getPlayer(playerUUID);

        if (player != null) {

            World world = player.getWorld();
            WorldBorder wb = world.getWorldBorder();

            int x = (int) (Math.round(wb.getSize() / 3 * Math.cos(d) + world.getSpawnLocation().getX()));
            int z = (int) (Math.round(wb.getSize() / 3 * Math.sin(d) + world.getSpawnLocation().getZ()));
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(message);
            player.removePotionEffect(PotionEffectType.WITHER);
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 400, -1, false, false));
            player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z));
        }
    }

    public World getWorld() {
        return this.world;
    }

    public WorldFillTask getWft() {
        return wft;
    }

    public void setWft(WorldFillTask wft) {
        this.wft = wft;
    }
}
