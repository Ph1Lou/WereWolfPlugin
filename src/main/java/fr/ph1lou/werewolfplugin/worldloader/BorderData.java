package fr.ph1lou.werewolfplugin.worldloader;

import java.text.DecimalFormat;


public class BorderData {
    private final DecimalFormat format = new DecimalFormat("0.0");
    // the main data interacted with
    private double x = 0;
    private double z = 0;
    private int radiusX = 0;
    private int radiusZ = 0;
    private Boolean shapeRound = null;
    private boolean wrapping = false;
    // some extra data kept handy for faster border checks
    private double maxX;
    private double minX;
    private double maxZ;
    private double minZ;
    private double radiusXSquared;
    private double radiusZSquared;
    private double DefiniteRectangleX;
    private double DefiniteRectangleZ;
    private double radiusSquaredQuotient;

    public BorderData(double x, double z, int radiusX, int radiusZ, Boolean shapeRound, boolean wrap) {
        setData(x, z, radiusX, radiusZ, shapeRound, wrap);
    }

    public final void setData(double x, double z, int radiusX, int radiusZ, Boolean shapeRound, boolean wrap) {
        this.x = x;
        this.z = z;
        this.shapeRound = shapeRound;
        this.wrapping = wrap;
        this.setRadiusX(radiusX);
        this.setRadiusZ(radiusZ);
    }


    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public int getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
        this.maxX = x + radiusX;
        this.minX = x - radiusX;
        this.radiusXSquared = (double) radiusX * (double) radiusX;
        this.radiusSquaredQuotient = this.radiusXSquared / this.radiusZSquared;
        this.DefiniteRectangleX = Math.sqrt(.5 * this.radiusXSquared);
    }

    public int getRadiusZ() {
        return radiusZ;
    }

    public void setRadiusZ(int radiusZ) {
        this.radiusZ = radiusZ;
        this.maxZ = z + radiusZ;
        this.minZ = z - radiusZ;
        this.radiusZSquared = (double) radiusZ * (double) radiusZ;
        this.radiusSquaredQuotient = this.radiusXSquared / this.radiusZSquared;
        this.DefiniteRectangleZ = Math.sqrt(.5 * this.radiusZSquared);
    }

    @Override
    public String toString() {
        return "radius " + ((radiusX == radiusZ) ? radiusX : radiusX + "x" + radiusZ) + " at X: " + format.format(x) + " Z: " + format.format(z) + (shapeRound != null ? (" (shape override: elliptic/round)") : "") + (wrapping ? (" (wrapping)") : "");
    }

    // This algorithm of course needs to be fast, since it will be run very frequently
    public boolean insideBorder(double xLoc, double zLoc, boolean round) {
        // if this border has a shape override set, use it
        if (shapeRound != null)
            round = shapeRound;

        // square border
        if (!round)
            return !(xLoc < minX || xLoc > maxX || zLoc < minZ || zLoc > maxZ);

            // round border
        else {
            // elegant round border checking algorithm is from rBorder by Reil with almost no changes, all credit to him for it
            double X = Math.abs(x - xLoc);
            double Z = Math.abs(z - zLoc);

            if (X < DefiniteRectangleX && Z < DefiniteRectangleZ)
                return true;    // Definitely inside
            else // Apparently outside, then
                if (X >= radiusX || Z >= radiusZ)
                    return false;    // Definitely outside
                else
                    return X * X + Z * Z * radiusSquaredQuotient < radiusXSquared;    // After further calculation, inside
        }
    }

    public boolean insideBorder(double xLoc, double zLoc) {
        return insideBorder(xLoc, zLoc, true);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj == null || obj.getClass() != this.getClass())
            return false;

        BorderData test = (BorderData) obj;
        return test.x == this.x && test.z == this.z && test.radiusX == this.radiusX && test.radiusZ == this.radiusZ;
    }

    @Override
    public int hashCode() {
        return (((int) (this.x * 10) << 4) + (int) this.z + (this.radiusX << 2) + (this.radiusZ << 3));
    }
}