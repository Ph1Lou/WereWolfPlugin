package io.github.ph1lou.werewolfplugin.utils;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VersionUtils_1_13 extends VersionUtils {


    @Override
    public void setSkullOwner(SkullMeta skull, OfflinePlayer player, String name) {
        if (skull != null) {
            skull.setOwningPlayer(player);
        }
    }

    @Override
    public void setPlayerMaxHealth(Player player, double maxHealth) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;
        attribute.setBaseValue(maxHealth);
    }

    @Override
    public double getPlayerMaxHealth(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return 20;
        return attribute.getBaseValue();
    }

    @Override
    public void patchBiomes() {
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.FROZEN_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_FROZEN_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.WARM_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_WARM_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.LUKEWARM_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_LUKEWARM_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.COLD_OCEAN, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.DEEP_COLD_OCEAN, BiomeMapping.Biome.FOREST);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.JUNGLE, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.JUNGLE_EDGE, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.JUNGLE_HILLS, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.BAMBOO_JUNGLE, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.MODIFIED_JUNGLE, BiomeMapping.Biome.PLAINS);
        BiomeMapping.replaceBiomes(BiomeMapping.Biome.BAMBOO_JUNGLE_HILLS, BiomeMapping.Biome.PLAINS);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void setGameRuleValue(World world, String name, Object value) {
        GameRule gameRule = GameRule.getByName(name);

        if (gameRule == null) return;
        world.setGameRule(gameRule, value);
    }


    @Override
    public void setTeamNameTagVisibility(Team team, boolean value) {
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, value ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
    }

    @Override
    public void setItemUnbreakable(ItemMeta meta, boolean b) {
        meta.setUnbreakable(b);
    }


    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        player.sendTitle(title, subtitle, fadeInTime, showTime, fadeOutTime);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }

    @Override
    public void sendTabTitle(@NotNull Player player, @NotNull String header, @NotNull String footer) {
        player.setPlayerListHeaderFooter(header, footer + "Ph1Lou");
    }

    @Override
    public Location findBiome(World world) throws Exception {
        for (int i = -2000; i < 2000; i += 16) {
            for (int j = -2000; j < 2000; j += 16) {
                if (world.getBiome(i, 20, j) == Biome.DARK_FOREST) {
                    return new Location(world, i, 151, j);
                }
            }
        }
        throw new Exception("No roofed found");
    }

    @Override
    public int biomeSize(Location location, World world) {

        int i = 0;
        Biome biome = world.getBiome(location.getBlockX(), 20, location.getBlockZ());
        List<Location> locations = new ArrayList<>();
        List<Location> finalLocations = new ArrayList<>();
        locations.add(location);

        while (!locations.equals(finalLocations)) {

            Location location1 = locations.get(i);
            int x = location1.getBlockX();
            int z = location1.getBlockZ();

            for (int x1 = -1; x1 < 2; x1 += 2) {

                for (int z1 = -1; z1 < 2; z1 += 2) {

                    if (world.getBiome(x1 + x, 20, z1 + z) == biome) {
                        Location location2 = new Location(world, x1 + x, 0, z1 + z);
                        if (!finalLocations.contains(location2) && !locations.contains(location2)) {
                            locations.add(location2);
                        }
                    }
                }
            }
            finalLocations.add(location1);
            i++;
            if (i > 30000) {
                return 33333;
            }
        }

        return i;
    }

    @Override
    public ItemStack getItemInHand(@NotNull Player player) {
        return player.getInventory().getItemInMainHand();
    }


}

