package ply.plyModel.elements;

import java.io.IOException;
import java.util.regex.Matcher;

import ply.result.MethodResult;
import ply.result.ReaderResult.PointResultEnum;

/**
 * A Point is a type of {@link Element}. It has at maximum 3 coordinates.
 * 
 * @author Christopher Caroni
 *
 */
public class Point extends Element {

	private double x;
	private double y;
	private double z;
	private int iter;

	/**
	 * @param indexNumber
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point(int indexNumber, double x, double y, double z) {
		super(indexNumber);
		this.x = x;
		this.y = y;
		this.z = z;
		iter = -1;
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

	/**
	 * Parses line and sets this Points' coordinates to those found in the line.
	 * 
	 * @param indexNumber the index number with which to create the Point.
	 * @param nbCoordsExpected how many properties this Point is expected to parse.
	 * @param line the line to parse.
	 * @param readResult a {@link MethodResult} object in which we store the result of parsing the line and setting the Point coordinates.
	 * @throws IOException unexpected data or error parsing the line.
	 */
	public void parseLine(int nbCoordsExpected, String line, MethodResult parseResult) throws IOException {
		line = line.trim();
		Matcher matcher = POINT_PATTERN.matcher(line);
		if (!matcher.matches()) {
			parseResult.setCode(PointResultEnum.NO_MATCH_PATTERN);
			throw new IOException("Cannot parse Point from line : \"" + line + "\".");
		}
		String[] pointDesc = line.split(" ");
		if (pointDesc.length != nbCoordsExpected) {
			parseResult.setCode(PointResultEnum.INCORRECT_NUMBER_OF_COORDS);
			throw new IOException("Line does not have correct number of coordinates :\"" + line + "\".");
		}
		double x = 0;
		double y = 0;
		double z = 0;
		try {
			x = Double.parseDouble(pointDesc[0]);
			y = Double.parseDouble(pointDesc[1]);
			z = Double.parseDouble(pointDesc[2]);
		} catch (NumberFormatException e) {
			parseResult.setCode(PointResultEnum.CANNOT_PARSE_COORD);
			throw new IOException("Could not parse the line to coordinate : \"" + line + "\".");
		}
		setCoords(x, y, z);
	}

}
