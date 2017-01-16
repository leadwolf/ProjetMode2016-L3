package ply.plyModel.elements;

import java.io.IOException;
import java.util.regex.Matcher;

public class Point extends Element {

	private double x;
	private double y;
	private double z;
	private int iter;

	/**
	 * @param number
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point(int number, double x, double y, double z) {
		super(number);
		this.x = x;
		this.y = y;
		this.z = z;
		iter = -1;
	}

	public Point() {
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Gets the coordinate corresponding to an index.<br>
	 * 0 = x, 1 = y, 2 = z
	 * 
	 * @param index
	 * @return the coordinate at the index or -1 if invalid
	 */
	public double getCoord(int index) {
		if (index < 0 || index > 2) {
			throw new IllegalArgumentException("Unable to return a coordinate with index = " + index + ".");
		}
		if (index == 0) {
			return this.x;
		} else if (index == 1) {
			return this.y;
		} else if (index == 2) {
			return this.z;
		}
		return -1;
	}

	/**
	 * Adds coordinates to this point according to the iterator.
	 * 
	 * @param value the coordinate value to add.
	 */
	public void add(Double value) {
		if (iter == -1) {
			x = value;
			iter++;
		} else if (iter == 0) {
			y = value;
			iter++;
		} else if (iter == 1) {
			z = value;
			iter++;
		}
	}

	/**
	 * Resets the coordinates to 0. Use to modify this coordinates but preserve the object.
	 */
	public void resetCoords() {
		setCoords(0.0, 0.0, 0.0);
		iter = -1;
	}

	public static Point parse(int number, int nbCoordsExpected, String line) throws IOException {
		line = line.trim();
		Matcher matcher = POINT_PATTERN.matcher(line);
		if (!matcher.matches()) {
			throw new IOException("Cannot parse Point from line : \"" + line + "\".");
		}
		String[] pointDesc = line.split(" ");
		if (pointDesc.length != nbCoordsExpected) {
			throw new IOException("Line does not have only three coordinates :\"" + line + "\".");
		}
		double x = 0;
		double y = 0;
		double z = 0;
		try {
			x = Double.parseDouble(pointDesc[0]);
			y = Double.parseDouble(pointDesc[1]);
			z = Double.parseDouble(pointDesc[2]);
		} catch (NumberFormatException e) {
			throw new IOException("Could not parse the line to coordinate : \"" + line + "\".");
		}
		return new Point(number, x, y, z);
	}

}
