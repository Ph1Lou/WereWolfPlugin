package io.github.ph1lou.werewolfplugin.worldloader;

import java.text.DecimalFormat;

public class BorderData {
    // the main data interacted with
    private final double x;
    private final double z;
    private int radiusX = 0;
    private int radiusZ = 0;

    // some extra data kept handy for faster border checks
    private double maxX;
    private double minX;
    private double maxZ;
    private double minZ;



	public BorderData(double x, double z, int radiusX, int radiusZ) {
		this.x = x;
		this.z = z;
		this.setRadiusX(radiusX);
		this.setRadiusZ(radiusZ);
	}

	public double getX()
	{
		return x;
	}

	public double getZ()
	{
		return z;
	}

	public int getRadiusX()
	{
		return radiusX;
	}
	public int getRadiusZ()
	{
		return radiusZ;
	}
	public void setRadiusX(int radiusX) {
		this.radiusX = radiusX;
		this.maxX = x + radiusX;
		this.minX = x - radiusX;
	}

	public void setRadiusZ(int radiusZ) {
		this.radiusZ = radiusZ;
		this.maxZ = z + radiusZ;
		this.minZ = z - radiusZ;
	}

	public void setRadius(int radius) {
		setRadiusX(radius);
		setRadiusZ(radius);
	}

	@Override
	public String toString() {
		return "radius " + ((radiusX == radiusZ) ? radiusX : radiusX + "x" + radiusZ) + " at X: " + new DecimalFormat("0.0").format(x) + " Z: " + new DecimalFormat("0.0").format(z);
	}

	// This algorithm of course needs to be fast, since it will be run very frequently
	public boolean insideBorder(double xLoc, double zLoc) {
		return !(xLoc < minX || xLoc > maxX || zLoc < minZ || zLoc > maxZ);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null || obj.getClass() != this.getClass())
			return false;

		BorderData test = (BorderData)obj;
		return test.x == this.x && test.z == this.z && test.radiusX == this.radiusX && test.radiusZ == this.radiusZ;
	}

	@Override
	public int hashCode()
	{
		return (((int)(this.x * 10) << 4) + (int)this.z + (this.radiusX << 2) + (this.radiusZ << 3));
	}
}
