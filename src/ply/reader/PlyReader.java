package ply.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.reader.headers.HeaderEntry;

public class PlyReader extends Reader {

	private BufferedReader reader;

	public PlyReader(File file) {
		super(file);
	}

	@Override
	public void readHeader() {
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			boolean doneReading = false;

			line = reader.readLine();
			if (!"ply".equals(line)) {
				throw new IOException("Invalid header : file does not start with \"ply\"");
			}

			while (!doneReading) {
				line = reader.readLine();
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
					} else if (line.startsWith("element vertex")) {
						if (vertexHeader != null) {
							throw new IOException("Vertex header already declared.");
						} else {
							vertexHeader = HeaderEntry.parseHeaderEntry(line);
						}
					} else if (line.startsWith("element face")) {
						if (faceHeader != null) {
							throw new IOException("Face header already declared.");
						} else {
							faceHeader = HeaderEntry.parseHeaderEntry(line);
						}
					} else if (line.startsWith("property")) {
						// we consider that the vertex properties are declared before face properties
						if (vertexHeader != null && faceHeader == null) {
							vertexHeader.addProperty(line);
							// make sure that both have already been instantiated otherwise there is no HeaderEntry for there to have properties
						} else if (faceHeader != null && vertexHeader != null) {
							faceHeader.addProperty(line);
						} else {
							throw new IOException("Incorrect use of property at line \"" + line + "\".");
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

	@Override
	public void readBody() {
		try {
			boolean doneReading = false;
			String line = null;
			int totalCount = 0;
			int elementCount = 0;
			int numberOfCoordsToPoint = vertexHeader.getPropertyCount();

			while (!doneReading) {
				line = reader.readLine();
				if (line == null) {
					throw new IOException("Unexpected end of file while reading header.");
				}
				// ignore all comments
				if (!line.startsWith("comment")) {
					if (totalCount < vertexHeader.getCount()) {
						Point point = Point.parse(elementCount, numberOfCoordsToPoint, line);
						vertexMap.put(elementCount, point);
					} else {
						int[] pointIndexes = Face.getPointIndexes(elementCount, line);
						Face face = new Face(elementCount);
						for (int i=0;i<pointIndexes.length;i++) {
							Point point = vertexMap.get(pointIndexes[i]);
							face.addPoint(point);
						}
						faceMap.put(elementCount, face);
					}
				}
				elementCount++;
				totalCount++;
				if (totalCount == vertexHeader.getCount() + faceHeader.getCount()) {
					doneReading = true;
				}
			}
		} catch (IOException e) {
			// TODO logger ? return a MethodResult ?
			e.printStackTrace();
		}
	}

}
