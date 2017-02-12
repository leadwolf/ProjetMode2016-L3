package ply.read.header.property;

import ply.read.reader.header.DataType;

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

	/**
	 * @return the numberOfEntriesType
	 */
	public DataType getNumberOfEntriesType() {
		return numberOfEntriesType;
	}

	/**
	 * @return the entryType of what composes the Face
	 */
	public DataType getEntryType() {
		return entryType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FaceProperty [numberOfEntriesType=" + numberOfEntriesType + ", entryType=" + entryType + ", toString()=" + super.toString() + "]";
	}

}
