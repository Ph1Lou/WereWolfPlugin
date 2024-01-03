package fr.ph1lou.werewolfplugin.worldloader;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// image output stuff, for debugging method at bottom of this file


// by the way, this region file handler was created based on the divulged region file format: http://mojang.com/2011/02/16/minecraft-save-file-format-in-beta-1-3/

public class WorldFileData {
    private final File world;
    private final Map<CoordXZ, List<Boolean>> regionChunkExistence = Collections.synchronizedMap(new HashMap<>());
    private File regionFolder = null;
    private File[] regionFiles = null;

    // the constructor is private; use create() method above to create an instance of this class.
    private WorldFileData(World world) {
        this.world = world.getWorldFolder();
    }

    // Use this static method to create a new instance of this class. If null is returned, there was a problem so any process relying on this should be cancelled.
    public static WorldFileData create(World world) {
        WorldFileData newData = new WorldFileData(world);

        newData.regionFolder = new File(newData.world, "region");
        if (!newData.regionFolder.exists() || !newData.regionFolder.isDirectory()) {
            // check for region folder inside a DIM* folder (DIM-1 for nether, DIM1 for end, DIMwhatever for custom world types)
            File[] possibleDimFolders = newData.world.listFiles(new DimFolderFileFilter());
            if (possibleDimFolders != null) {
                for (File possibleDimFolder : possibleDimFolders) {
                    File possible = new File(newData.world, possibleDimFolder.getName() + File.separator + "region");
                    if (possible.exists() && possible.isDirectory()) {
                        newData.regionFolder = possible;
                        break;
                    }
                }
            }
            if (!newData.regionFolder.exists() || !newData.regionFolder.isDirectory()) {
                newData.sendMessage("Could not validate folder for world's region files. Looked in " + newData.world.getPath() + " for valid DIM* folder with a region folder in it.");
                return null;
            }
        }

        // Accepted region file formats: MCR is from late beta versions through 1.1, MCA is from 1.2+
        newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCA"));
        if (newData.regionFiles == null || newData.regionFiles.length == 0) {
            newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCR"));
            if (newData.regionFiles == null || newData.regionFiles.length == 0) {
                newData.sendMessage("Could not find any region files. Looked in: " + newData.regionFolder.getPath());
                return null;
            }
        }

        return newData;
    }

    // return a region file by index
    public File regionFile(int index) {
        if (regionFiles.length < index)
            return null;
        return regionFiles[index];
    }

    // get the X and Z world coordinates of the region from the filename
    public CoordXZ regionFileCoordinates(int index) {
        File regionFile = this.regionFile(index);
        String[] coords = regionFile.getName().split("\\.");
        int x, z;
        try {
            x = Integer.parseInt(coords[1]);
            z = Integer.parseInt(coords[2]);
            return new CoordXZ(x, z);
        } catch (Exception ex) {
            sendMessage("Error! Region file found with abnormal name: " + regionFile.getName());
            return null;
        }
    }


    // Find out if the chunk at the given coordinates exists.
    public boolean doesChunkExist(int x, int z) {
        CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
        List<Boolean> regionChunks = this.getRegionData(region);
//		Bukkit.getLogger().info("x: "+x+"  z: "+z+"  offset: "+coordToRegionOffset(x, z));
        return regionChunks.get(coordToRegionOffset(x, z));
    }

    // Find out if the chunk at the given coordinates has been fully generated.
    // Minecraft only fully generates a chunk when adjacent chunks are also loaded.
    public boolean isChunkFullyGenerated(int x, int z) {    // if all adjacent chunks exist, it should be a safe enough bet that this one is fully generated
        // For 1.13+, due to world gen changes, this is now effectively a 3 chunk radius requirement vs a 1 chunk radius
        for (int xx = x - 3; xx <= x + 3; xx++) {
            for (int zz = z - 3; zz <= z + 3; zz++) {
                if (!doesChunkExist(xx, zz))
                    return false;
            }
        }
        return true;
    }

    // Method to let us know a chunk has been generated, to update our region map.
    public void chunkExistsNow(int x, int z) {
        CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
        List<Boolean> regionChunks = this.getRegionData(region);
        regionChunks.set(coordToRegionOffset(x, z), true);
    }


    // region is 32 * 32 chunks; chunk pointers are stored in region file at position: x + z*32 (32 * 32 chunks = 1024)
    // input x and z values can be world-based chunk coordinates or local-to-region chunk coordinates either one
    private int coordToRegionOffset(int x, int z) {
        // "%" modulus is used to convert potential world coordinates to definitely be local region coordinates
        x = x % 32;
        z = z % 32;
        // similarly, for local coordinates, we need to wrap negative values around
        if (x < 0) x += 32;
        if (z < 0) z += 32;
        // return offset position for the now definitely local x and z values
        return (x + (z * 32));
    }

    private List<Boolean> getRegionData(CoordXZ region) {
        List<Boolean> data = regionChunkExistence.get(region);
        if (data != null)
            return data;

        // data for the specified region isn't loaded yet, so init it as empty and try to find the file and load the data
        data = new ArrayList<>(1024);
        for (int i = 0; i < 1024; i++) {
            data.add(Boolean.FALSE);
        }

        for (int i = 0; i < regionFiles.length; i++) {
            CoordXZ coord = regionFileCoordinates(i);
            // is this region file the one we're looking for?
            if (!coord.equals(region))
                continue;

            try {
                RandomAccessFile regionData = new RandomAccessFile(this.regionFile(i), "r");

                // Use of ByteBuffer+IntBuffer for reading file headers to improve performance, as suggested by aikar, reference:
                // https://github.com/PaperMC/Paper/blob/b62dfa0bf95ac27ba0fbb3fae18c064e4bb61d50/Spigot-Server-Patches/0086-Reduce-IO-ops-opening-a-new-region-file.patch
                ByteBuffer header = ByteBuffer.allocate(8192);
                while (header.hasRemaining()) {
                    if (regionData.getChannel().read(header) == -1)
                        throw new EOFException();
                }
                ((Buffer) header).clear(); //cast usefull else java 8 java.lang.NoSuchMethodError: java.nio.ByteBuffer.clear()Ljava/nio/ByteBuffer;
                IntBuffer headerAsInts = header.asIntBuffer();

                // first 4096 bytes of region file consists of 4-byte int pointers to chunk data in the file (32*32 chunks = 1024; 1024 chunks * 4 bytes each = 4096)
                for (int j = 0; j < 1024; j++) {
                    // if chunk pointer data is 0, chunk doesn't exist yet; otherwise, it does
                    if (headerAsInts.get() != 0)
                        data.set(j, true);
                }
                // Read timestamps
                for (int j = 0; j < 1024; j++) {
                    // if timestamp is zero, it is protochunk (ignore it)
                    if ((headerAsInts.get() == 0) && data.get(j))
                        data.set(j, false);
                }
                regionData.close();
            } catch (FileNotFoundException ex) {
                sendMessage("Error! Could not open region file to find generated chunks: " + this.regionFile(i).getName());
            } catch (IOException ex) {
                sendMessage("Error! Could not read region file to find generated chunks: " + this.regionFile(i).getName());
            }
        }
        regionChunkExistence.put(region, data);
//		testImage(region, data);
        return data;
    }

    // send a message to the server console/log and possibly to an in-game player
    private void sendMessage(String text) {
        Bukkit.getLogger().info("[WorldData] " + text);
    }

    // file filter used for region files
    private static class ExtFileFilter implements FileFilter {
        final String ext;

        public ExtFileFilter(String extension) {
            this.ext = extension.toLowerCase();
        }

        @Override
        public boolean accept(File file) {
            return (
                    file.exists()
                            && file.isFile()
                            && file.getName().toLowerCase().endsWith(ext)
            );
        }
    }

    // file filter used for DIM* folders (for nether, End, and custom world types)
    private static class DimFolderFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return (
                    file.exists()
                            && file.isDirectory()
                            && file.getName().toLowerCase().startsWith("dim")
            );
        }
    }

}