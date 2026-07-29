package fr.ph1lou.werewolfplugin.buildings;

import fr.ph1lou.werewolfapi.buildings.BuildingType;
import fr.ph1lou.werewolfapi.buildings.ICouncilChamber;
import org.bukkit.Location;
import org.bukkit.World;

public class CouncilChamber extends Building implements ICouncilChamber {

    private final Location plate1;
    private final Location plate2;
    private final Location glass;

    public CouncilChamber(World world, int baseX, int baseY, int baseZ, Location bannerLocation,
                          Location plate1, Location plate2, Location glass) {
        super(BuildingType.COUNCIL_CHAMBER, world, baseX, baseY, baseZ, bannerLocation);
        this.plate1 = plate1;
        this.plate2 = plate2;
        this.glass = glass;
    }

    public Location getPlate1() {
        return plate1;
    }

    public Location getPlate2() {
        return plate2;
    }

    public Location getGlass() {
        return glass;
    }
}
