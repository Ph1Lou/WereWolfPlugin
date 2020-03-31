package io.github.ph1lou.pluginlg;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class WorldLoader implements Runnable {

    final MainLG main;
    private final World world;
    private final int size;
    private int x;
    private int z;
    private int loaded;
    private final int area;
    private long sprint;

    public WorldLoader(World world,int size, MainLG main) {
        this.world = world;
        this.size=size;
        this.x = (int) (-this.size+world.getSpawnLocation().getX());
        this.z = (int) (-this.size + +world.getSpawnLocation().getZ());
        this.loaded = 0;
        this.sprint = System.currentTimeMillis();
        this.area = (size*size) >> 6;
        this.main=main;
    }

    @Override
    public void run() {

        synchronized (this) {
            sprint = System.currentTimeMillis();
            while (z < size+world.getSpawnLocation().getZ()) {
               x = (int) (-size+world.getSpawnLocation().getX());
                while (x < size +world.getSpawnLocation().getX()) {
                    if (System.currentTimeMillis() - sprint > 8000L) {
                        return;
                    }
                    Chunk chunk = world.getChunkAt(x , z);
                    chunk.load(true);
                    chunk.unload(true,true);
                    loaded++;
                    if (loaded % 100 == 0) {
                        System.out.println("[pluginLG] PreGeneration "+loaded + "/" + area + " chunks | X: " + x + " | Z: " + z);
                    }
                    if (loaded % 1000 == 0) {
                        Bukkit.broadcastMessage(String.format(main.text.getText(222),loaded/(float) area*100 ));
                    }
                   x += 16;
                }
                z += 16;
            }
        }
    }

}
