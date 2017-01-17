package ply.read.header.property;

import ply.read.reader.DataType;

public class FaceProperty extends Property {

	private DataType numberOfEntriesType;
	private DataType entryType;

	/**
	 * @param name
	 * @param type
	 * @param numberOfEntriesType
	 * @param entryType
	 */
	public FaceProperty(String name, DataType type, DataType numberOfEntriesType, DataType entryType) {
		super(name, type);
		this.numberOfEntriesType = numberOfEntriesType;
		this.entryType = entryType;
	}
}
