package fr.ph1lou.werewolfplugin;

import com.pieterdebot.biomemapping.Biome;
import com.pieterdebot.biomemapping.BiomeMappingAPI;

public class Replacer {

    public static void replaceBiomes(){
        BiomeMappingAPI biome = new BiomeMappingAPI();
        replaceBiome(biome, Biome.BEACH, Biome.FOREST);
        replaceBiome(biome, Biome.WOODED_HILLS, Biome.PLAINS);
        replaceBiome(biome, Biome.BAMBOO_JUNGLE, Biome.FOREST);
        replaceBiome(biome, Biome.BAMBOO_JUNGLE_HILLS, Biome.FOREST);
        replaceBiome(biome, Biome.JUNGLE, Biome.FOREST);
        replaceBiome(biome, Biome.JUNGLE_HILLS, Biome.FOREST);
        replaceBiome(biome, Biome.JUNGLE_EDGE, Biome.FOREST);
        replaceBiome(biome, Biome.MODIFIED_JUNGLE, Biome.FOREST);
        replaceBiome(biome, Biome.MODIFIED_JUNGLE_EDGE, Biome.FOREST);
        replaceBiome(biome, Biome.TAIGA_MOUNTAINS, Biome.FOREST);
        replaceBiome(biome, Biome.TAIGA_HILLS, Biome.FOREST);
        replaceBiome(biome, Biome.SNOWY_TAIGA_MOUNTAINS, Biome.FOREST);
        replaceBiome(biome, Biome.SNOWY_TAIGA_HILLS, Biome.FOREST);
        replaceBiome(biome, Biome.GRAVELLY_MOUNTAINS, Biome.FOREST);
        replaceBiome(biome, Biome.WOODED_MOUNTAINS, Biome.FOREST);
        replaceBiome(biome, Biome.DESERT, Biome.FOREST);
        replaceBiome(biome, Biome.DESERT_HILLS, Biome.FOREST);
        replaceBiome(biome, Biome.DESERT_LAKES, Biome.FOREST);
        replaceBiome(biome, Biome.COLD_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.WARM_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.LUKEWARM_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.FROZEN_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.DEEP_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.DEEP_WARM_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.DEEP_LUKEWARM_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.DEEP_FROZEN_OCEAN, Biome.FOREST);
        replaceBiome(biome, Biome.DEEP_COLD_OCEAN, Biome.FOREST);
    }

    private static void replaceBiome(BiomeMappingAPI biome, Biome initial, Biome finalBiome){
        try {
            if(biome.biomeSupported(initial)){
                biome.replaceBiomes(initial, finalBiome);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
