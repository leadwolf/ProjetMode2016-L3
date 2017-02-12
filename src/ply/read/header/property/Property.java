package ply.read.header.property;

import ply.read.reader.header.DataType;

public abstract class Property {

	private String name;
	private DataType type;

	/**
	 * @param name
	 * @param type
	 */
	public Property(String name, DataType type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public DataType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Property [name=" + name + ", type=" + type + "]";
	}

}
