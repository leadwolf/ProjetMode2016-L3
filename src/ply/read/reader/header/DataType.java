package ply.read.reader.header;

public enum DataType {

	CHAR("char"), UCHAR("uchar"), SHORT("short"), USHORT("ushort"), INT("int"), UINT("uint"), FLOAT("float"), DOUBLE("double"), INT8("int8"),
	UINT8("uint8"), INT16("int16"), UINT16("uint16"), INT32("int32"), UINT32("uint32"), FLOAT32("float32"), FLOAT64("float64"), LIST("list");

	private String stringRepresentation;

	DataType(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}

	@Override
	public String toString() {
		return stringRepresentation;
	}

	public static DataType getTypeFromString(String stringRepresenation) {
		for (DataType type : DataType.values()) {
			if (type.toString().equals(stringRepresenation)) {
				return type;
			}
		}
		return null;
	}

	public static boolean validateType(String stringRepresenation) {
		for (DataType type : DataType.values()) {
			if (type.toString().equals(stringRepresenation)) {
				return true;
			}
		}
		return false;
	}

}
