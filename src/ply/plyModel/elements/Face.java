package ply.plyModel.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import ply.result.MethodResult;
import ply.result.ReaderResult.FaceResultEnum;

public class Face extends Element implements Comparable<Face> {

	private Map<Integer, Element> elementMap;
	private List<Point> vertexList;

	/**
	 * @param indexNumber
	 * @param elements
	 */
	public Face(int indexNumber) {
		super(indexNumber);
		elementMap = new HashMap<>();
		vertexList = new ArrayList<>();
	}

	/**
	 * Adds a {@link Point} to this face.
	 * 
	 * @param point
	 */
	public void addPoint(Point point) {
		elementMap.put(point.getNumber(), point);
		vertexList.add(point);
	}

	/**
	 * @param number
	 * @return gets the Point in this face by its index number (from .ply file).
	 */
	public Element getPoint(int number) {
		return elementMap.get(number);
	}

	/**
	 * @return the vertexList
	 */
	public List<Point> getVertexList() {
		return vertexList;
	}

	/**
	 * @return the elementMap
	 */
	public Map<Integer, Element> getElementMap() {
		return elementMap;
	}

	public static Element parse(String line) {
		return null;

	}

	/**
	 * Gives indexes of the points of the face corresponding to this line. Cannot directly create a face because we need the list of points.
	 * 
	 * @param line the line describing a face.
	 * @param readResult a {@link MethodResult} object in which we store the result of parsing the line and getting the Point indexes.
	 * @return the indexes of the points in this face.
	 * @throws IOException
	 */
	public int[] getPointIndexes(String line, MethodResult parseResult) throws IOException {
		line = line.trim();
		Matcher matcher = FACE_PATTERN.matcher(line);
		if (!matcher.matches()) {
			parseResult.setCode(FaceResultEnum.NO_MATCH_PATTERN);
			throw new IOException("Cannot parse Face from line : \"" + line + "\".");
		}

		// verify number of properties
		String[] faceDesc = line.split(" ");
		int nbExpected = 0;
		try {
			nbExpected = Integer.parseInt(faceDesc[0]);
		} catch (NumberFormatException e) {
			parseResult.setCode(FaceResultEnum.CANNOT_PARSE_POINT_COUNT);
			throw new IOException("Could not parse the number of points in this face : \"" + line + "\".");
		}
		if (nbExpected != faceDesc.length - 1) {
			parseResult.setCode(FaceResultEnum.INCORRECT_NUMBER_OF_POINTS);
			throw new IOException("Line declares " + nbExpected + " coordinates but found " + (faceDesc.length - 1) + " :\"" + line + "\".");
		}

		// get point indexes
		int[] pointIndexes = new int[nbExpected];
		for (int i = 1; i < faceDesc.length; i++) {
			int number = 0;
			try {
				number = Integer.parseInt(faceDesc[i]);
			} catch (NumberFormatException e) {
				parseResult.setCode(FaceResultEnum.CANNOT_PARSE_POINT_INDEX);
				throw new IOException("Could not parse the number of the point in this face : \"" + line + "\".");
			}
			pointIndexes[i - 1] = number;
		}
		return pointIndexes;
	}

	/**
	 * Compare by average Z index.
	 */
	@Override
	public int compareTo(Face o) {
		double zO1 = 0.0, zO2 = 0.0;
		for (Point pt : getVertexList()) {
			zO1 += pt.getZ();
		}
		zO1 /= this.getVertexList().size();

		for (Point pt : o.getVertexList()) {
			zO2 += pt.getZ();
		}
		zO2 /= o.getVertexList().size();
		
		if (zO1 > zO2) {
			return 1;
		} else if (zO1 < zO2) {
			return -1;
		} else {
			return 0;
		}
	}

}
