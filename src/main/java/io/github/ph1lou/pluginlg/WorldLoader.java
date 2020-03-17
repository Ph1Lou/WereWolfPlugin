package io.github.ph1lou.pluginlg;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ResourceBundle;

public class WorldLoader implements Runnable {

    final ConsoleCommandSender sender = Bukkit.getConsoleSender();
    private final World world;
    private final int width;
    private final int depth;
    private int x;
    private int z;
    private int loaded;
    private final int area;
    private long sprint;
    private final int offset;
    private boolean pause;

    public WorldLoader(final World world, final int width, final int depth) {
        this(world, width, depth, 0);
    }

    public WorldLoader(final World world, final int width, final int depth, final int offset) {
        this.pause = false;
        this.world = world;
        this.width = width;
        this.depth = depth;
        this.x = (int) (-this.width+world.getSpawnLocation().getX());
        this.z = (int) (-this.depth + +world.getSpawnLocation().getZ());
        this.loaded = 0;
        this.sprint = System.currentTimeMillis();
        this.area = (this.width >> 4) * 2 * ((this.depth >> 4) * 2);
        this.offset = offset;
    }

    @Override
    public void run() {

        Thread thread;
        (thread = new Thread()).start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (thread) {
            this.sprint = System.currentTimeMillis();
            this.setPause(false);
            while (this.z < this.depth+world.getSpawnLocation().getZ()) {
                this.x = (int) (-this.width+world.getSpawnLocation().getX());
                while (this.x < this.width +world.getSpawnLocation().getX()) {
                    if (System.currentTimeMillis() - this.sprint > 8000L) {
                        this.setPause(true);
                    }
                    if (this.isPause()) {
                        return;
                    }

                    final Chunk chunk = this.world.getChunkAt(this.offset + this.x >> 4, this.z >> 4);
                    chunk.load(true);
                    chunk.unload(true, true);
                    ++this.loaded;
                    if (this.loaded % 100 == 0) {
                       sender.sendMessage(this.loaded + "/" + this.area + " chunks | Free Memory: " + Runtime.getRuntime().freeMemory() / 1024L + " MB | X: " + this.x + " | Z: " + this.z);
                    }

                    if (this.loaded % 5000 == 0) {
                        try {
                            this.world.save();
                            Chunk[] chunks;
                            for (int j = (chunks = this.world.getLoadedChunks()).length, i = 0; i < j; ++i) {
                                final Chunk c = chunks[i];
                                c.unload(true, true);
                            }
                        }
                        catch (Exception ignored) {}

                        float ramPercent;
                        while ((ramPercent = Runtime.getRuntime().freeMemory() / (float)Runtime.getRuntime().maxMemory()) > 0.4f) {
                            try {
                                ResourceBundle.clearCache();
                            }
                            catch (Exception ignored) {}
                            System.gc();
                            sender.sendMessage("Memory usage is too high at " + ramPercent + "%! Clearing Memory");
                            try {
                                Thread.sleep(5000L);
                            }
                            catch (Exception ignored) {}
                        }
                    }
                    this.x += 16;
                }
                this.z += 16;
            }
        }
    }

    public boolean isPause() {
        return this.pause;
    }

    public void setPause(final boolean pause) {
        this.pause = pause;
    }
}
