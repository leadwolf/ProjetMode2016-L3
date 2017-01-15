package ply.reader.headers.property;

import ply.reader.DataType;

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
}
