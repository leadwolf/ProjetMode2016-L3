package ply.read.reader.body;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ply.read.reader.header.HeaderReader;
import ply.result.MethodResult;

public class BinaryReader extends BodyReader {


	private FileInputStream fileInputStream;
	private BufferedReader bufferedReader;

	public BinaryReader(HeaderReader headerReader, MethodResult readResult) throws IOException {
		super(headerReader, readResult);
		fileInputStream = new FileInputStream(headerReader.getFile());
		bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		parseBody();
	}

	@Override
	protected void parseBody() throws IOException {
		String line = "";
		int nChars = 0;
		boolean end = false;
		while ((line = bufferedReader.readLine()) != null) {
			nChars += line.length();
			if ("end_header".equals(line.toLowerCase())) {
				fileInputStream.mark(nChars);
				break;
			}
		}

		int nBytesRead = 0;
		byte[] buffer = new byte[1024];
		fileInputStream.reset();
		while ((nBytesRead = fileInputStream.read(buffer)) != -1) {

		}
	}

}
