package ply.read.reader.body;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.read.header.HeaderEntry;
import ply.read.header.property.FaceProperty;
import ply.read.header.property.Property;
import ply.read.reader.header.DataType;
import ply.read.reader.header.HeaderReader;
import ply.result.MethodResult;

public class BinaryReader extends BodyReader {

	private FileInputStream fileInputStream;
	private BufferedReader bufferedReader;
	private ByteArrayOutputStream byteBufferOutput;
	private ByteBuffer buffer;

	public BinaryReader(HeaderReader headerReader, MethodResult readResult) throws IOException {
		super(headerReader, readResult);

		fileInputStream = new FileInputStream(headerReader.getFile());
		bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		byteBufferOutput = new ByteArrayOutputStream();

		parseBody();
	}

	@Override
	protected void parseBody() throws IOException {
		storeBinary();
		storeElements();
	}

	private void storeBinary() throws IOException {
		String line = "";
		int nChars = 0;
		boolean end = false;
		while ((line = bufferedReader.readLine()) != null) {
			nChars += (line.length() + 1);
			if ("end_header".equals(line.toLowerCase())) {
				break;
			}
		}

		int nBytesRead = 0;
		byte[] readBuffer = new byte[1024];
		// dont need to skip since bufferReader also skips in fileInputStream
		// fileInputStream.skip(nChars);
		System.out.println("skipping " + nChars + " chars");
		while ((nBytesRead = fileInputStream.read(readBuffer)) != -1) {
			System.out.println(new String(readBuffer, 0, nBytesRead));
			byteBufferOutput.write(readBuffer);
		}
		buffer = ByteBuffer.wrap(byteBufferOutput.toByteArray());
	}

	private void storeElements() throws IOException {
		for (HeaderEntry entry : headerReader.getHeaderList()) {
			if (entry.getName().equals("vertex")) {
				for (int vertexCount = 0; vertexCount < entry.getCount(); vertexCount++) {
					double[] coords = new double[3];
					for (int j = 0; j < coords.length; j++) {
						Property prop = entry.getPropertyList().get(j);
						coords[j] = readByte(prop.getType());
						// System.out.println("Read coord type " + prop.getType() + " : " + coords[j]);
					}
					Point point = new Point(vertexCount, coords[0], coords[1], coords[2]);
					vertexList.add(point);
					vertexMap.put(vertexCount, point);
				}
			} else if (entry.getName().equals("face")) {
				FaceProperty faceProp = (FaceProperty) entry.getProperty("vertex_indices");
				for (int faceCount = 0; faceCount < entry.getCount(); faceCount++) {
					double nPointsInFace = readByte(faceProp.getNumberOfEntriesType());
					Face face = new Face(faceCount);
					for (int i = 0; i < nPointsInFace; i++) {
						double pointIndex = readByte(faceProp.getEntryType());
						System.out.println("Getting point at index = " + pointIndex);
						face.addPoint(vertexMap.get(pointIndex));
					}
					faceMap.put(faceCount, face);
					faceList.add(face);
				}
			}
		}
	}

	private double readByte(DataType dataType) throws IOException {
		switch (dataType) {
		case CHAR:
			return buffer.get();
		case UCHAR:
			return ((int) buffer.get()) & 0x000000FF;
		case SHORT:
			return buffer.getShort();
		case USHORT:
			return ((int) buffer.getShort()) & 0x0000FFFF;
		case INT:
			return buffer.getInt();
		case UINT:
			return ((long) buffer.getShort()) & 0x00000000FFFFFFFF;
		case FLOAT:
			return buffer.getFloat();
		case DOUBLE:
			return buffer.getDouble();
		default:
			throw new IOException("Cannot read bytes for unexpected data type");
		}
	}

}
