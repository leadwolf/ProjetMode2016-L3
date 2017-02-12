package ply.read.reader.header;

import java.io.IOException;

public enum Format {

	ASCII, BINARY_LITTLE_ENDIAN, BINARY_BIG_ENDIAN;

	/**
	 * @param line the line containing the definition of the file format
	 * @return the enum corresponding to the format of the file.
	 * @throws IOException invalid format header
	 */
	public static Format parse(String line) throws IOException {
		line = line.trim();
		String[] formatType = line.split(" +");
		if (!"format".equals(formatType[0])) {
			throw new IOException("Ply file does not declare \"format\" on second line.");
		}
		switch (formatType[1].toUpperCase()) {
		case "ASCII":
			if (!"1.0".equals(formatType[2])) {
				throw new IOException("ASCII version not equal to 1.0. Only rev 1.0 is supported.");
			}
			return ASCII;
		case "BINARY_LITTLE_ENDIAN":
			if (!"1.0".equals(formatType[2])) {
				throw new IOException("binary_little_endian version not equal to 1.0. Only rev 1.0 is supported.");
			}
			return BINARY_LITTLE_ENDIAN;
		case "BINARY_BIG_ENDIAN":
			if (!"1.0".equals(formatType[2])) {
				throw new IOException("binary_big_endian version not equal to 1.0. Only rev 1.0 is supported.");
			}
			return BINARY_BIG_ENDIAN;
		default:
			throw new IOException("Format type \"" + formatType[1] + "\" is unsupported.");
		}
	}

}
