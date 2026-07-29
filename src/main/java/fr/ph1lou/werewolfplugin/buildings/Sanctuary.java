package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.buildings.BuildingType;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Sanctuary extends Building {

    private final DyeColor dyeColor;
    private final Color particleColor;
    private final Set<UUID> visitedBy = new HashSet<>();

    public Sanctuary(World world, int baseX, int baseY, int baseZ, Location bannerLocation,
                     DyeColor dyeColor, Color particleColor) {
        super(BuildingType.SANCTUARY, world, baseX, baseY, baseZ, bannerLocation);
        this.dyeColor = dyeColor;
        this.particleColor = particleColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public Color getParticleColor() {
        return particleColor;
    }

    public boolean hasVisited(UUID player) {
        return visitedBy.contains(player);
    }

    public void markVisited(UUID player) {
        visitedBy.add(player);
    }

    public Location getCenter() {
        return new Location(getWorld(), getBaseX() + 2, getBaseY() + 1, getBaseZ() + 2);
    }
}
