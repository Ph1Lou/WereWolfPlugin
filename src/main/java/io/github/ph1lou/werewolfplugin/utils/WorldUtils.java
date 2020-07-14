package io.github.ph1lou.werewolfplugin.utils;

import net.minecraft.server.v1_16_R1.BiomeBase;
import net.minecraft.server.v1_16_R1.IRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldUtils {

    public static void patchBiomes() {

        Map<String, BiomeBase> biomeBackup= new HashMap<>();

        final List<BiomeBase> base = IRegistry.BIOME.e().collect(Collectors.toList());
        for (final BiomeBase b : base) {
            biomeBackup.put(IRegistry.BIOME.getKey(b).getKey(), b);
        }

        WorldUtils.swap(BiomeData.BAMBOO_JUNGLE,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.BAMBOO_JUNGLE_HILLS,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.BEACHES,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.COLD_BEACH,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.COLD_OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.DEEP_COLD_OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.DEEP_FROZEN_OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.EXTREME_HILLS,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.DEEP_OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.WARM_OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.SMALLER_EXTREME_HILLS,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.FROZEN_OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.JUNGLE,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.JUNGLE_EDGE,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.JUNGLE_HILLS,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.OCEAN,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.MUTATED_JUNGLE,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.MUTATED_JUNGLE_EDGE,BiomeData.PLAINS, biomeBackup);
        WorldUtils.swap(BiomeData.MUTATED_EXTREME_HILLS_PLUS,BiomeData.PLAINS, biomeBackup);
    }


    public static void swap(final BiomeData from, final BiomeData to, Map<String, BiomeBase> biomeBackup)  {
        IRegistry.a(IRegistry.BIOME, from.getId(), to.getKey_1_13().toLowerCase(), biomeBackup.get(to.getKey_1_13().toLowerCase()));
    }

    public static Location findBiome(Biome biome, World world, int max) throws Exception {
        for (int i = -max; i < max; i+=16) {
            for (int j = -max; j < max; j+=16) {
                if (world.getBiome(i,10, j) == biome) {
                    return new Location(world, i, 151, j);
                }
            }
        }
        throw new Exception("No roofed found");
    }

    public static int biomeSize(Location location, World world){

        int i=0;
        Biome biome = world.getBiome(location.getBlockX(),10,location.getBlockZ());
        List<Location> locations = new ArrayList<>();
        List<Location> finalLocations = new ArrayList<>();
        locations.add(location);

        while(!locations.equals(finalLocations)){

            Location location1 = locations.get(i);
            int x = location1.getBlockX();
            int z = location1.getBlockZ();

            for(int x1=-1;x1<2;x1+=2){

                for(int z1=-1;z1<2;z1+=2){

                    if(world.getBiome(x1+x, 10,z1+z) == biome){
                        Location location2 = new Location(world,x1+x,0,z1+z);
                        if(!finalLocations.contains(location2) && !locations.contains(location2)){
                            locations.add(location2);
                        }
                    }
                }
            }
            finalLocations.add(location1);
            i++;
            if(i>30000){
                return 33333;
            }
        }

        return i;
    }
}
