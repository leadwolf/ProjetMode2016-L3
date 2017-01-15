package ply.plyModel.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class Face extends Element {

	private Map<Integer, Element> elementMap;

	/**
	 * @param number
	 * @param elements
	 */
	public Face(int number) {
		super(number);
		elementMap = new HashMap<>();
	}
	
	public void addPoint(Point point) {
		elementMap.put(point.getNumber(), point);
	}

	public Element getPoint(int number) {
		return elementMap.get(number);
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

	public static int[] getPointIndexes(int elementCount, String line) throws IOException {
		line.trim();
		Matcher matcher = SPACES_AND_NUMBERS.matcher(line);
		if (!matcher.matches()) {
			throw new IOException("Cannot parse Face from line : \"" + line + "\".");
		}
		int nbExpected = 0;
		String[] faceDesc = line.split(" ");
		try {
			nbExpected = Integer.parseInt(faceDesc[0]);
		} catch (NumberFormatException e) {
			throw new IOException("Could not parse the number of points in this face : \"" + line + "\".");
		}
		int[] pointIndexes = new int[nbExpected];
		for (int i = 1; i < faceDesc.length; i++) {
			int number = 0;
			try {
				number = Integer.parseInt(faceDesc[i]);
			} catch (NumberFormatException e) {
				throw new IOException("Could not parse the number of the point in this face : \"" + line + "\".");
			}
			pointIndexes[i - 1] = number;
		}
		return pointIndexes;
	}

}
