package ply.reader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.reader.headers.HeaderEntry;

public abstract class Reader {

	/**
	 * The .ply file we are reading.
	 */
	protected File file;

	/**
	 * The {@link HeaderEntry} corresponding to the vertexes of the model.
	 */
	protected HeaderEntry vertexHeader;
	/**
	 * The {@link HeaderEntry} corresponding to the faces of the model.
	 */
	protected HeaderEntry faceHeader;

	protected Map<Integer, Point> vertexMap;
	protected Map<Integer, Face> faceMap;

	protected Format format;

	public Reader(File file) {
		this.file = file;
		format = null;
		vertexHeader = null;
		faceHeader = null;
		vertexMap = new HashMap<>();
		faceMap = new HashMap<>();
		readHeader();
		readBody();
	}

	public abstract void readHeader();

	public abstract void readBody();

	public int getVertexCount() {
		return vertexHeader.getCount();
	}

	public int getFaceCount() {
		return faceHeader.getCount();
	}

}
