package ply.read.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.result.MethodResult;
import ply.result.ReaderResult.FaceResultEnum;
import ply.result.ReaderResult.ReaderResultEnum;

public class AsciiReader extends Reader {

	/**
	 * @param file
	 * @param readResult a {@link MethodResult} object in which we store the result of reading the file.
	 * @throws IOException bad file or error occurred while reading lines.
	 */
	public AsciiReader(File file, MethodResult readResult) throws IOException {
		super(file, readResult);
	}

	/**
	 * @param path
	 * @param readResult a {@link MethodResult} object in which we store the result of reading the file.
	 * @throws IOException bad file or error occurred while reading lines.
	 */
	public AsciiReader(Path path, MethodResult readResult) throws IOException {
		super(path, readResult);
	}

	@Override
	protected void readBody() throws IOException {
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
					point.parseLine(getPropertyCount("vertex"), line, readResult);
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
