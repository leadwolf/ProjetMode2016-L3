package ply.read.reader;

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
import ply.read.header.HeaderEntry;
import ply.result.MethodResult;
import ply.result.ReaderResult.ReaderResultEnum;

public abstract class Reader {

	/**
	 * The .ply file we are reading.
	 */
	protected File file;
	/**
	 * The default reader. Always used to read the header and the body if file is ascii format.
	 */
	protected BufferedReader asciiReader;

	protected Map<String, HeaderEntry> headerMap;
	protected List<HeaderEntry> headerList;
	protected Map<Integer, Point> vertexMap;
	protected Map<Integer, Face> faceMap;
	protected List<Point> vertexList;
	protected List<Face> faceList;

	protected Format format;

	protected MethodResult readResult;

	HeaderEntry currentHeaderEntry;
	boolean doneReading;

	/**
	 * Initializes a Reader and parses the whole file.
	 * 
	 * @param file
	 * @param readResult a {@link MethodResult} object in which we store the result of reading the file.
	 * @throws IOException bad file or error occurred while reading lines.
	 */
	public Reader(File file, MethodResult readResult) throws IOException {
		if (file == null) {
			readResult.setCode(ReaderResultEnum.FILE_DOES_NOT_EXIST);
			throw new NullPointerException("File must not be null.");
		}
		if (!file.exists()) {
			readResult.setCode(ReaderResultEnum.FILE_DOES_NOT_EXIST);
			throw new IOException("File " + file.toString() + " does not exist.");
		}
		String extension = file.toString();
		if (extension.lastIndexOf(".") == -1) {
			readResult.setCode(ReaderResultEnum.NO_EXTENSION);
			throw new IOException("Cannot read non .ply file");
		}
		extension = extension.substring(extension.lastIndexOf("."), extension.length());
		if (!".ply".equals(extension)) {
			readResult.setCode(ReaderResultEnum.BAD_EXTENSION);
			throw new IOException("Cannot read non .ply file");
		}
		this.file = file;
		this.readResult = readResult;
		initAtrtibutes();
	}

	/**
	 * Initializes a Reader and parses the {@link Path#toFile()}.
	 * 
	 * @param path the path to the .ply file.
	 * @param readResult a {@link MethodResult} object in which we store the result of reading the file.
	 * 
	 * @throws IOException bad file or error occurred while reading lines.
	 */
	public Reader(Path path, MethodResult readResult) throws IOException {
		this(path.toFile(), readResult);
	}

	/**
	 * @throws FileNotFoundException
	 * 
	 */
	private void initAtrtibutes() throws FileNotFoundException {
		format = null;
		headerMap = new HashMap<>();
		headerList = new ArrayList<>();
		vertexMap = new HashMap<>();
		faceMap = new HashMap<>();
		vertexList = new ArrayList<>();
		faceList = new ArrayList<>();

		asciiReader = new BufferedReader(new FileReader(file));
		currentHeaderEntry = null;
		doneReading = false;
	}

	/**
	 * @throws IOException error occurred while reading lines.
	 */
	public void parseFile() throws IOException {
		parseHeader();
		parseBody();
	}

	/**
	 * Reads the header with an ASCII charset.
	 * 
	 * @throws IOException error occurred while reading lines.
	 */
	private void parseHeader() throws IOException {
		verifyMagicNumber();
		parseAllHeaderLines();
		verifyFoundVertexAndFace();
	}

	/**
	 * Reads the first line of the file and verifies the magic number exists.
	 * 
	 * @throws IOException the magic number is not present.
	 */
	private void verifyMagicNumber() throws IOException {
		String magicNumber = asciiReader.readLine();
		if (!"ply".equals(magicNumber)) {
			readResult.setResult(ReaderResultEnum.MISSING_PLY_DECLARATION);
			throw new IOException("Invalid header : file does not start with \"ply\"");
		}
	}

	/**
	 * Parses the whole header.
	 * 
	 * @throws IOException
	 */
	private void parseAllHeaderLines() throws IOException {
		while (!doneReading) {
			parseOneLineNotAComment();
		}
	}

	/**
	 * @throws IOException did not find vertex or face element declarations in the header.
	 */
	private void verifyFoundVertexAndFace() throws IOException {
		if (headerMap.get("vertex") == null) {
			readResult.setResult(ReaderResultEnum.VERTEX_NOT_DECLARED);
			throw new IOException("Header did not contain vertex declaration.");
		} else if (headerMap.get("face") == null) {
			readResult.setResult(ReaderResultEnum.FACE_NOT_DECLARED);
			throw new IOException("Header did not contain face declaration.");
		}
	}

	/**
	 * Parses one line of the header if not a comment.
	 * 
	 * @throws IOException unexpected end of file or error while parsing line.
	 */
	private void parseOneLineNotAComment() throws IOException {
		String currentLine = asciiReader.readLine();
		if (currentLine == null) {
			readResult.setResult(ReaderResultEnum.UNEXPECTED_END_OF_FILE_IN_HEADER);
			throw new IOException("Unexpected end of file while reading header.");
		}
		if (!currentLine.startsWith("comment")) {
			parseOneLine(currentLine);
		}
	}

	/**
	 * Calls the appropriate method for the line to be parsed.
	 * 
	 * @param line
	 * @throws IOException an error while parsing the line.
	 */
	private void parseOneLine(String line) throws IOException {
		if (line.startsWith("format")) {
			setFormatFromLine(line);
		} else if (line.startsWith("element")) {
			parseElementFromLine(line);
		} else if (line.startsWith("property")) {
			parsePropertyFromLine(line);
		} else if ("end_header".equals(line)) {
			doneReading = true;
		}
	}

	/**
	 * Sets the format from the line given.
	 * 
	 * @param line
	 * @throws IOException if a format has already been declared or if the line does not properly correspond to a format declaration.
	 */
	private void setFormatFromLine(String line) throws IOException {
		if (format != null) {
			readResult.setResult(ReaderResultEnum.DUPLICATE_FORMAT);
			throw new IOException("Format already declared.");
		} else {
			format = Format.parse(line);
		}
	}

	/**
	 * Adds a new element to the list from the line given.
	 * 
	 * @param line
	 * @throws IOException if the line does not properly correspond to an element declaration.
	 */
	private void parseElementFromLine(String line) throws IOException {
		String[] elementDesc = line.split(" ");
		if (elementDesc.length != 3) {
			readResult.setResult(ReaderResultEnum.INCORRECT_ELEMENT_DECLARATION);
			throw new IOException("Cannot parse element, expected element <element name> <count> at line : \"" + line + "\".");
		}
		String name = elementDesc[1];
		if (headerMap.get(name) != null) {
			readResult.setResult(ReaderResultEnum.DUPLICATE_ELEMENT_HEADER);
			throw new IOException(name + " header already declared.");
		} else {
			currentHeaderEntry = HeaderEntry.parseHeaderEntry(line);
			headerMap.put(name, currentHeaderEntry);
			headerList.add(currentHeaderEntry);
		}
	}

	/**
	 * Adds a new property to the current header (currentHeader) from the line given.
	 * 
	 * @param line
	 * @throws IOException if no header has been declared or error parsing the line.
	 */
	private void parsePropertyFromLine(String line) throws IOException {
		if (currentHeaderEntry == null) {
			readResult.setResult(ReaderResultEnum.PROPERTY_BEFORE_ELEMENT_DECLARATION);
			throw new IOException("Could not parse property. Element not declared beforehand.");
		} else {
			currentHeaderEntry.addProperty(line);
		}
	}

	/**
	 * Reads the data of the .ply file either in ASCII or binary little/big endian.
	 * 
	 * @throws IOException error occurred while reading lines.
	 */
	protected abstract void parseBody() throws IOException;

	/**
	 * @param elementName
	 * @return the count for the elementName.
	 */
	public int getElementCount(String elementName) {
		if (elementName == null || (elementName != null && elementName.matches("\\s*"))) {
			throw new NullPointerException("Cannot get element count for null parameter.");
		}
		if (headerMap.get(elementName) == null) {
			throw new NullPointerException("Element " + elementName + " does not exist in header.");
		}
		return headerMap.get(elementName).getCount();
	}

	/**
	 * @param elementName
	 * @return the property count for the elementName.
	 */
	public int getPropertyCountForElement(String elementName) {
		if (elementName == null || (elementName != null && elementName.matches("\\s*"))) {
			throw new NullPointerException("Cannot get element count for null parameter.");
		}
		if (headerMap.get(elementName) == null) {
			throw new NullPointerException("Element " + elementName + " does not exist in header.");
		}
		return headerMap.get(elementName).getPropertyCount();
	}

	@SuppressWarnings("javadoc")
	public int getTotalElementCount() {
		int result = 0;
		for (HeaderEntry entry : headerList) {
			result += entry.getCount();
		}
		return result;
	}

	@SuppressWarnings("javadoc")
	public File getFile() {
		return file;
	}

	@SuppressWarnings("javadoc")
	public List<Point> getVertexList() {
		return vertexList;
	}

	@SuppressWarnings("javadoc")
	public List<Face> getFaceList() {
		return faceList;
	}

	@SuppressWarnings("javadoc")
	public Enum<?> getReadResultCode() {
		return readResult.getCode();
	}
}
