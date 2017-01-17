package ply.result;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.read.reader.AsciiReader;
import ply.read.reader.Reader;

/**
 * Type d'erreur correspondant à une erreur de {@link LecteurAscii}
 * @author L3
 *
 */
public class ReaderResult extends MethodResult{

	/**
	 * Crée une {@link MethodResult} de ce type, {@link ReaderResult}.
	 * @param readerEror
	 */
	public ReaderResult(ReaderResultEnum readerError) {
		result = readerError;
	}
	
	public ReaderResult(FaceResultEnum parseError) {
		result = parseError;
	}

	public ReaderResult(PointResultEnum parseError) {
		result = parseError;
	}

	/**
	 * Error codes for {@link Face}.
	 * 
	 * @author Christopher Caroni
	 *
	 */
	public enum FaceResultEnum {
		/**
		 * Line does not match de pattern for faces.
		 */
		NO_MATCH_PATTERN,
		/**
		 * Could not convert the number of points to an Integer.
		 */
		CANNOT_PARSE_POINT_COUNT,
		/**
		 * Number of {@link Point} declared for this {@link Face} does not match the number of Point indexes found.
		 */
		INCORRECT_NUMBER_OF_POINTS,
		/**
		 * Could not convert the point index to an Integer.
		 */
		CANNOT_PARSE_POINT_INDEX,
		/**
		 * Face references a {@link Point} which cannot be found.
		 */
		REFERENCE_POINT_NON_EXISTING,
	}

	/**
	 * Error codes for {@link Point}.
	 * 
	 * @author Christopher Caroni
	 *
	 */
	public enum PointResultEnum {
		/**
		 * Line does not match de pattern for points.
		 */
		NO_MATCH_PATTERN,
		/**
		 * The number of coords found does not match the number of properties expected.
		 */
		INCORRECT_NUMBER_OF_COORDS,
		/**
		 * Could not convert the coordinate to a Double.
		 */
		CANNOT_PARSE_COORD
	}

	/**
	 * Error codes for {@link Reader} and {@link AsciiReader}.
	 * 
	 * @author L3
	 *
	 */
	public enum ReaderResultEnum {

		/**
		 * File does not have .ply extension.
		 */
		BAD_EXTENSION,
		/**
		 * File does not have an extension.
		 */
		NO_EXTENSION,
		/**
		 * File does not exist.
		 */
		FILE_DOES_NOT_EXIST,
		/**
		 * File does not declare "ply" on first line.
		 */
		MISSING_PLY_DECLARATION,
		/**
		 * Unexpectedly reached the end of the file while reading the header.
		 */
		UNEXPECTED_END_OF_FILE_IN_HEADER,
		/**
		 * Unexpectedly reached the end of the file while reading the body.
		 */
		UNEXPECTED_END_OF_FILE_IN_BODY,
		/**
		 * Line declares an format which has been previously declared.
		 */
		DUPLICATE_FORMAT,
		/**
		 * The line to parse does not follow the element declaration format which is "element &lt;element name&gt; &lt;count&gt; ".
		 */
		INCORRECT_ELEMENT_DECLARATION,
		/**
		 * Line declares an element which has been previously declared.
		 */
		DUPLICATE_ELEMENT_HEADER,
		/**
		 * Line declares a property but no element has been declared beforehand.
		 */
		PROPERTY_BEFORE_ELEMENT_DECLARATION,
		/**
		 * Vertex header element not found.
		 */
		VERTEX_NOT_DECLARED,
		/**
		 * Face header element not found.
		 */
		FACE_NOT_DECLARED,
		/**
		 * Read data when should have reached end of file.
		 */
		UNEXPECTED_DATA_AT_END
	}
}
