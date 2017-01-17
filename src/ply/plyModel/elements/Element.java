package ply.plyModel.elements;

import java.util.regex.Pattern;

import ply.result.MethodResult;

/**
 * An Element is a type of data declared in a .ply file. (Ex: Point, Face);
 * 
 * @author Christopher Caroni
 *
 */
public abstract class Element {

	/**
	 * The index of this Element, its position as read in the .ply file.
	 */
	int indexNumber;
	/**
	 * Pattern to match space, sign, number, exponent and exponent value.
	 */
	protected final static Pattern POINT_PATTERN = Pattern.compile("([\\s]?[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)+");
	/**
	 * Pattern to match space and number.
	 */
	protected final static Pattern FACE_PATTERN = Pattern.compile("([\\s]?[0-9]+)+");

	protected MethodResult parseResult;

	
	/**
	 * @param number
	 */
	public Element(int number) {
		this.indexNumber = number;
	}

	/**
	 * @return the index number of this element from its position in the .ply file.
	 */
	public int getNumber() {
		return indexNumber;
	}

	public Enum getParseResultCode() {
		return parseResult.getCode();
	}

}
