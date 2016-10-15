
public class Matrice {

	private double[][] matrix;

	public Matrice(int width, int height) {
		matrix = new double[height][width];
	}

	public static double[][] multiply(double[][] A, double[][] B) {

		int aRows = A.length;
		int aColumns = A[0].length;
		int bRows = B.length;
		int bColumns = B[0].length;

		if (aColumns != bRows) {
			return null;
		} else {
			double[][] C = new double[aRows][bColumns];
			for (int i = 0; i < aRows; i++) {
				for (int j = 0; j < bColumns; j++) {
					for (int k = 0; k < aColumns; k++) {
						C[i][j] += A[i][k] * B[k][j];
					}
				}
			}
			return C;
		}
	}

	public boolean multiply(double[][] B) {
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		int bRows = B.length;
		int bColumns = B[0].length;

		if (aColumns == bRows) {
			double[][] C = new double[aRows][bColumns];
			for (int i = 0; i < aRows; i++) {
				for (int j = 0; j < bColumns; j++) {
					for (int k = 0; k < aColumns; k++) {
						C[i][j] += this.matrix[i][k] * B[k][j];
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static void rotateX(double[][] matrix, double angle) {
		double[][] xRotation = new double[][] { { 1.0, 0.0, 0.0 }, { 0.0, Math.cos(angle), -Math.sin(angle) }, { 0.0, Math.sin(angle), Math.cos(angle) } };
		matrix = multiply(matrix, xRotation);
	}

	public void rotateX(double angle) {
		double[][] xRotation = new double[][] { { 1.0, 0.0, 0.0 }, { 0.0, Math.cos(angle), -Math.sin(angle) }, { 0.0, Math.sin(angle), Math.cos(angle) } };
		multiply(xRotation);
	}

	public static void rotateY(double[][] matrix, double angle) {
		double[][] xRotation = new double[][] { { Math.cos(angle), 0.0, Math.sin(angle) }, { 0.0, 1.0, 0.0 }, { -Math.sin(angle), 0.0, Math.cos(angle) } };
		matrix = multiply(matrix, xRotation);
	}
	
	public void rotateY(double angle) {
		double[][] YRotation = new double[][] { { Math.cos(angle), 0.0, Math.sin(angle) }, { 0.0, 1.0, 0.0 }, { -Math.sin(angle), 0.0, Math.cos(angle) } };
		multiply(YRotation);
	}

	public static void rotateZ(double[][] matrix, double angle) {
		double[][] xRotation = new double[][] { { Math.cos(angle), -Math.sin(angle), 0.0 }, { Math.sin(angle), Math.cos(angle), 0.0 }, { 0.0, 0.0, 1.0 } };
		matrix = multiply(matrix, xRotation);
	}
	
	public void rotateZ(double angle) {
		double[][] ZRotation = new double[][] { { Math.cos(angle), -Math.sin(angle), 0.0 }, { Math.sin(angle), Math.cos(angle), 0.0 }, { 0.0, 0.0, 1.0 } };
		multiply(ZRotation);
	}
}
