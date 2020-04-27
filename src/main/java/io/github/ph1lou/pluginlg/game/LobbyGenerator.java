package io.github.ph1lou.pluginlg.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import io.github.ph1lou.pluginlg.MainLG;
import org.bukkit.World;

import java.io.File;

public class LobbyGenerator {

    final MainLG main;
    final GameManager game;

    LobbyGenerator(MainLG main, GameManager game) {

        this.main = main;
        this.game = game;
        World world = game.getWorld();


        try {
            File dir = new File(main.getDataFolder(), File.separator + "schematics" + File.separator + "ww.schematic");
            EditSession editSession = new EditSession(new BukkitWorld(world), 999999999);
            editSession.enableQueue();
            SchematicFormat schematic = SchematicFormat.getFormat(dir);
            schematic.load(dir).paste(editSession, BukkitUtil.toVector(world.getSpawnLocation()), true);
            editSession.flushQueue();
        } catch (Exception ignored) {

        }
    }
}
