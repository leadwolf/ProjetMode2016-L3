package ply.read.reader.body;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.read.reader.header.HeaderReader;
import ply.result.MethodResult;

public abstract class BodyReader {

	protected Map<Integer, Point> vertexMap;
	protected Map<Integer, Face> faceMap;
	protected List<Point> vertexList;
	protected List<Face> faceList;

	protected HeaderReader headerReader;
	protected MethodResult readResult;

	protected BodyReader(HeaderReader headerReader, MethodResult readResult) throws IOException {
		vertexMap = new HashMap<>();
		faceMap = new HashMap<>();
		vertexList = new ArrayList<>();
		faceList = new ArrayList<>();

		this.headerReader = headerReader;
		this.readResult = readResult;
	}

	public static BodyReader getBodyReader(HeaderReader headerReader, MethodResult readResult) throws IOException {
		switch (headerReader.getFormat()) {
		case ASCII:
			return new AsciiReader(headerReader, readResult);
		case BINARY_BIG_ENDIAN:
			return new BinaryReader(headerReader, readResult);
		case BINARY_LITTLE_ENDIAN:
			return new BinaryReader(headerReader, readResult);
		default:
			throw new IOException("Could not get the format of the file");
		}
	}

	/**
	 * Reads the data of the .ply file either in ASCII or binary little/big endian.
	 * 
	 * @throws IOException error occurred while reading lines.
	 */
	protected abstract void parseBody() throws IOException;

	@SuppressWarnings("javadoc")
	public List<Point> getVertexList() {
		return vertexList;
	}

	@SuppressWarnings("javadoc")
	public List<Face> getFaceList() {
		return faceList;
	}

	/**
	 * @param elementName
	 * @return the count for the elementName.
	 */
	public int getElementCount(String elementName) {
		return headerReader.getElementCount(elementName);
	}

	/**
	 * @param elementName
	 * @return the property count for the elementName.
	 */
	public int getPropertyCountForElement(String elementName) {
		return headerReader.getPropertyCountForElement(elementName);
	}

	@SuppressWarnings("javadoc")
	public int getTotalElementCount() {
		return headerReader.getTotalElementCount();
	}

	public File getFile() {
		return headerReader.getFile();
	}
}
