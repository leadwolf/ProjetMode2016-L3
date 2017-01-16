package ply.plyModel.elements;

import java.util.regex.Pattern;

public abstract class Element {

	int number;
	/**
	 * Pattern to match space, sign, number, exponent and exponent value.
	 */
	protected final static Pattern POINT_PATTERN = Pattern.compile("([\\s]?[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)+");

	public Element() {
		
	}
	
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
