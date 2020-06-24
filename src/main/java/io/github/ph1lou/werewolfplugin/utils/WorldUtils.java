package io.github.ph1lou.werewolfplugin.utils;

import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class WorldUtils {

    public static void patchBiomes() {

        Field biomesField;
        try {
            biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(biomesField, biomesField.getModifiers() & ~Modifier.FINAL);

            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.COLD_BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.STONE_BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.EXTREME_HILLS_PLUS.id] = BiomeBase.PLAINS;
                biomesField.set(null, biomes);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Location findBiome(Biome biome, World world, int max) throws Exception {
        for (int i = -max; i < max; i+=16) {
            for (int j = -max; j < max; j+=16) {
                if (world.getBiome(i, j) == biome) {
                    return new Location(world, i, 151, j);
                }
            }
        }
        throw new Exception("No roofed found");
    }

    public static int biomeSize(Location location, World world){

        int i=0;
        Biome biome = world.getBiome(location.getBlockX(),location.getBlockZ());
        List<Location> locations = new ArrayList<>();
        List<Location> finalLocations = new ArrayList<>();
        locations.add(location);

        while(!locations.equals(finalLocations)){

            Location location1 = locations.get(i);
            int x = location1.getBlockX();
            int z = location1.getBlockZ();

            for(int x1=-1;x1<2;x1+=2){

                for(int z1=-1;z1<2;z1+=2){

                    if(world.getBiome(x1+x, z1+z) == biome){
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
