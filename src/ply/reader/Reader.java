package ply.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.reader.headers.HeaderEntry;

public abstract class Reader {

	/**
	 * The .ply file we are reading.
	 */
	protected File file;
	protected BufferedReader asciiReader;

	protected Map<String, HeaderEntry> headerMap;
	protected List<HeaderEntry> headerList;
	protected Map<Integer, Point> vertexMap;
	protected Map<Integer, Face> faceMap;
	protected List<Point> vertexList;
	protected List<Face> faceList;

	protected Format format;

	public Reader(File file) {
		this.file = file;
		initAtrtibutes();
	}
	
	public Reader(Path path) {
		this.file = path.toFile();
		initAtrtibutes();
	}
	
	private void initAtrtibutes() {
		format = null;
		headerMap = new HashMap<>();
		headerList = new ArrayList<>();
		vertexMap = new HashMap<>();
		faceMap = new HashMap<>();
		vertexList = new ArrayList<>();
		faceList = new ArrayList<>();
		readHeader();
		readBody();
	}

	/**
	 * Reads the header with an ascii charset.
	 */
	protected void readHeader() {
		try {
			asciiReader = new BufferedReader(new FileReader(file));
			String line = null;
			HeaderEntry currentHeaderEntry = null;
			boolean doneReading = false;

			line = asciiReader.readLine();
			if (!"ply".equals(line)) {
				throw new IOException("Invalid header : file does not start with \"ply\"");
			}

			while (!doneReading) {
				line = asciiReader.readLine();
				if (line == null) {
					throw new IOException("Unexpected end of file while reading header.");
				}
				// ignore all comments
				if (!line.startsWith("comment")) {
					if (line.startsWith("format")) {
						if (format != null) {
							throw new IOException("Format already declared.");
						} else {
							format = Format.parse(line);
						}
					} else if (line.startsWith("element")) {
						String[] elementDesc = line.split(" ");
						if (elementDesc.length != 3) {
							throw new IOException("Cannot parse element, expected element <element> <count> at line : \"" + line + "\".");
						}
						String name = elementDesc[1];
						if (headerMap.get(name) != null) {
							throw new IOException(name + " header already declared.");
						} else {
							currentHeaderEntry = HeaderEntry.parseHeaderEntry(line);
							headerMap.put(name, currentHeaderEntry);
							headerList.add(currentHeaderEntry);
						}
					} else if (line.startsWith("property")) {
						// we consider that the vertex properties are declared before face properties
						if (currentHeaderEntry == null) {
							throw new IOException("Could not parse property. Element not declared beforehand.");
						} else {
							currentHeaderEntry.addProperty(line);
						}
					} else if ("end_header".equals(line)) {
						doneReading = true;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO logger ? return a MethodResult ?
			e.printStackTrace();
		}
	}

	/**
	 * Reads the data of the .ply file either in ascii or binary little/big endian.
	 */
	protected abstract void readBody();

	public File getFile() {
		return file;
	}

	public List<Point> getVertexList() {
		return vertexList;
	}

	public List<Face> getFaceList() {
		return faceList;
	}

	public int getVertexCount() {
		return headerMap.get("vertex").getCount();
	}

	public int getFaceCount() {
		return headerMap.get("face").getCount();
	}

}
