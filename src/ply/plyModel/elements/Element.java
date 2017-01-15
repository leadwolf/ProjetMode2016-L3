package ply.plyModel.elements;

import java.util.regex.Pattern;

public abstract class Element {

	int number;
	protected final static Pattern SPACES_AND_NUMBERS = Pattern.compile("(\\s+)*(?:\\d*\\.)?\\d+");

	/**
	 * @param number
	 */
	public Element(int number) {
		this.number = number;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

}
