package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.buildings.BuildingType;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class Building {

    private final BuildingType type;
    private final World world;
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final Location bannerLocation;

    
    protected Building(BuildingType type, World world, int baseX, int baseY, int baseZ, Location bannerLocation) {
        this.type = type;
        this.world = world;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseZ = baseZ;
        this.bannerLocation = bannerLocation;
    }

    public BuildingType getType() {
        return type;
    }

    public World getWorld() {
        return world;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public int getBaseZ() {
        return baseZ;
    }

    public Location getBannerLocation() {
        return bannerLocation;
    }
}
