package ply.read.reader.body;

import java.io.FileInputStream;
import java.io.IOException;

import ply.read.reader.header.HeaderReader;
import ply.result.MethodResult;

public class BinaryReader extends BodyReader {


	private FileInputStream inputStream;

	public BinaryReader(HeaderReader headerReader, MethodResult readResult) throws IOException {
		super(headerReader, readResult);

		parseBody();
	}

	@Override
	protected void parseBody() throws IOException {
		boolean doneReading = false;
		int totalElementCount = 0;
		int elementIndex = 0;

		byte[] buffer = new byte[1024];
		int nRead = 0;
        while((nRead = inputStream.read(buffer)) != -1) {
			System.out.println(new String(buffer));
        }

	}

}
