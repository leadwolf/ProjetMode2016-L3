package ply.read.reader.body;

import java.io.BufferedReader;
import java.io.IOException;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.read.reader.header.HeaderReader;
import ply.result.MethodResult;
import ply.result.ReaderResult.FaceResultEnum;
import ply.result.ReaderResult.ReaderResultEnum;

public class AsciiReader extends BodyReader {

	private BufferedReader asciiReader;

	public AsciiReader(HeaderReader headerReader, MethodResult readResult) throws IOException {
		super(headerReader, readResult);
		this.asciiReader = headerReader.getAsciiReader();

		parseBody();
	}

	@Override
	protected void parseBody() throws IOException {
		boolean doneReading = false;
		String line = null;
		int totalElementCount = 0;
		int elementIndex = 0;

		while (!doneReading) {
			line = asciiReader.readLine();
			if (line == null) {
				readResult.setCode(ReaderResultEnum.UNEXPECTED_END_OF_FILE_IN_BODY);
				throw new IOException("Unexpected end of file while reading body.");
			}
			// ignore all comments
			if (!line.startsWith("comment")) {
				if (totalElementCount < getElementCount("vertex")) {
					Point point = new Point(elementIndex, -1, -1, -1);
					point.parseLine(getPropertyCountForElement("vertex"), line, readResult);
					vertexMap.put(elementIndex, point);
					vertexList.add(point);
				} else {
					Face face = new Face(elementIndex);
					int[] pointIndexes = face.getPointIndexes(line, readResult);
					for (int i = 0; i < pointIndexes.length; i++) {
						if (vertexMap.get(pointIndexes[i]) == null) {
							readResult.setCode(FaceResultEnum.REFERENCE_POINT_NON_EXISTING);
							throw new IOException("Faces references point index " + pointIndexes[i] + " but point does not exist.");
						}
						Point point = vertexMap.get(pointIndexes[i]);
						face.addPoint(point);
					}
					faceMap.put(elementIndex, face);
					faceList.add(face);
				}
			}
			elementIndex++;
			totalElementCount++;
			if (totalElementCount == getTotalElementCount()) {
				doneReading = true;
			}
		}
		while ((line = asciiReader.readLine()) != null) {
			if (!line.startsWith("comment")) {
				readResult.setCode(ReaderResultEnum.UNEXPECTED_DATA_AT_END);
				throw new IOException("Unexpected data read when should have reached end of file : \"" + line + "\".");
			}
		}
	}

}
