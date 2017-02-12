package ply.read.header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ply.read.header.property.FaceProperty;
import ply.read.header.property.Property;
import ply.read.header.property.VertexProperty;
import ply.read.reader.header.DataType;

public class HeaderEntry {

	private String name;
	private int count;
	private Map<String, Property> propertyMap;
	private List<Property> propertyList;

	/**
	 * @param name
	 * @param count
	 */
	public HeaderEntry(String name, int count) {
		this.name = name;
		this.count = count;
		propertyMap = new HashMap<>();
		propertyList = new ArrayList<>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return how many elements of this type there are in the file.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param name
	 * @return the property of this element as defined by its name in the header.
	 */
	public Property getProperty(String name) {
		return propertyMap.get(name);
	}

	/**
	 * Creates an entry from the line given.
	 * 
	 * @param line the line to parse.
	 * @return a new {@link HeaderEntry} corresponding to the data in line.
	 * @throws IOException if the header line has an invalid format.
	 */
	public static HeaderEntry parseHeaderEntry(String line) throws IOException {
		line.trim();
		if (!line.startsWith("element")) {
			throw new IOException("Cannot create Element from \"" + line + "\". Expected \"element\" at start of line.");
		}
		String[] elementDesc = line.split(" +");
		if (elementDesc.length != 3) {
			throw new IOException("Unable to create element. Expected three parts : element <element_type> <count>");
		}
		String name = elementDesc[1];
		int count = 0;
		try {
			count = Integer.parseInt(elementDesc[2]);
		} catch (NumberFormatException e) {
			throw new IOException("Invalid element entry. Not an integer: \"" + elementDesc[1] + "\".");
		}
		return new HeaderEntry(name, count);
	}

	/**
	 * Adds a {@link VertexProperty} or {@link FaceProperty}, according to the line, to this entry.
	 * 
	 * @param line the String from which to parse the property.
	 * @throws IOException if the property line has an invalid format.
	 */
	public void addProperty(String line) throws IOException {
		String[] propertyDesc = line.split(" +");
		verifyPropertyDeclaration(propertyDesc);

		if (propertyDesc.length == 3) {
			validateProperyType(propertyDesc[1]);
			addSimpleProperty(propertyDesc);
		} else if (propertyDesc.length == 5) {
			for (int i = 1; i <= 3; i++) {
				validateProperyType(propertyDesc[i]);
			}
			addComplexProperty(propertyDesc);
		}
	}

	/**
	 * Creates a simple property (i.e., : a vertex has its declaration in 3 parts) from the line and adds to the list.
	 * 
	 * @param propertyDesc
	 * @throws IOException
	 */
	private void addSimpleProperty(String[] propertyDesc) throws IOException {
		String name = propertyDesc[2];
		DataType type = DataType.getTypeFromString(propertyDesc[1]);
		VertexProperty vertexProperty = new VertexProperty(name, type);
		propertyMap.put(name, vertexProperty);
		propertyList.add(vertexProperty);
	}

	/**
	 * Creates a complex property (i.e., : a Face has its declaration in 5 parts) from the line and adds to the list.
	 * 
	 * @param propertyDesc
	 * @throws IOException
	 */
	private void addComplexProperty(String[] propertyDesc) throws IOException {
		String name = propertyDesc[4];
		DataType type = DataType.getTypeFromString(propertyDesc[1]);
		DataType numberOfEntriesType = DataType.getTypeFromString(propertyDesc[2]);
		DataType entryType = DataType.getTypeFromString(propertyDesc[3]);
		FaceProperty faceProperty = new FaceProperty(name, type, numberOfEntriesType, entryType);
		propertyMap.put(name, faceProperty);
		propertyList.add(faceProperty);
	}

	/**
	 * @param propertyType
	 * @return true if correct.
	 * @throws IOException if type incorrect.
	 */
	private boolean validateProperyType(String propertyType) throws IOException {
		if (!DataType.validateType(propertyType)) {
			throw new IOException("Invalid property entry. Not a valid data type: \"" + propertyType + "\".");
		}
		return true;
	}

	/**
	 * Verifies the syntax for the line split into an array.
	 * 
	 * @param lineArray
	 * @throws IOException
	 */
	private void verifyPropertyDeclaration(String[] lineArray) throws IOException {
		if (!lineArray[0].startsWith("property")) {
			throw new IOException("Cannot create HeaderProperty from \"" + lineArray + "\". Expected \"property\" at start of line.");
		}
		if (lineArray.length != 3 && lineArray.length != 5) {
			throw new IOException("Unable to create element. Expected three or five parts : " + "\nproperty <entry_type> <property_type>"
					+ "\nproperty list <number_of_entries_type> <entry_type> <property_type>");
		}
	}

	/**
	 * @return how many properties this element has.
	 */
	public int getPropertyCount() {
		return propertyMap.size();
	}

	/**
	 * @return the propertyList
	 */
	public List<Property> getPropertyList() {
		return propertyList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HeaderEntry [name=" + name + ", count=" + count + ", propertyList=" + propertyList + "]";
	}

}
