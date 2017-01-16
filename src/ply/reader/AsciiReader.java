package ply.reader;

import java.io.File;
import java.io.IOException;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;

public class AsciiReader extends Reader {

	public AsciiReader(File file) {
		super(file);
	}

	@Override
	protected void readBody() {
		try {
			boolean doneReading = false;
			String line = null;
			int totalCount = 0;
			int elementCount = 0;
			int numberOfCoordsToPoint = headerMap.get("vertex").getPropertyCount();

			while (!doneReading) {
				line = asciiReader.readLine();
				if (line == null) {
					throw new IOException("Unexpected end of file while reading header.");
				}
				// ignore all comments
				if (!line.startsWith("comment")) {
					if (totalCount < getVertexCount()-1) {
						Point point = Point.parse(elementCount, numberOfCoordsToPoint, line);
						vertexMap.put(elementCount, point);
						vertexList.add(point);
					} else {
						int[] pointIndexes = Face.getPointIndexes(elementCount, line);
						Face face = new Face(elementCount);
						for (int i=0;i<pointIndexes.length;i++) {
							Point point = vertexMap.get(pointIndexes[i]);
							face.addPoint(point);
						}
						faceMap.put(elementCount, face);
						faceList.add(face);
					}
				}
				elementCount++;
				totalCount++;
				if (totalCount == getVertexCount() + getFaceCount()) {
					doneReading = true;
				}
			}
		} catch (IOException e) {
			// TODO logger ? return a MethodResult ?
			e.printStackTrace();
		}
	}

}
