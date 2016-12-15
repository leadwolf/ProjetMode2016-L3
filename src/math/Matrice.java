package math;

import java.util.List;

import ply.plyModel.modeles.FigureModel;
import ply.plyModel.modeles.Point;

public class Matrice {

	private double[][] matrice;

	public Matrice(int width, int height) {
		matrice = new double[height][width];
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < aColumns; j++) {
				this.matrice[i][j] = 0.0;
			}
		}
	}
	
	public Matrice(double[][] matrix) {
		this(matrix[0].length, matrix.length);
		copyFromMatrix(matrix);
	}

	/**
	 * Copie la matrice donnée dans cette matrice.<br>
	 * donne null si les colonnes de celle la et les lignes de la source ne correspondent pas
	 * @param matrixSource
	 */
	private void copyFromMatrix(double[][] matrixSource) {
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		int bRows = matrixSource.length;

		if (aRows != bRows) {
			this.matrice = null;
		} else {
			for (int i = 0; i < aRows; i++) {
				for (int j = 0; j < aColumns; j++) {
					this.matrice[i][j] = matrixSource[i][j];
				}
			}
		}
	}
	
	/**
	 * Multiplie la matrice de manière à avoir A.B
	 * @param A
	 * @param B
	 * @return la matrice multipliée
	 */
	public static double[][] multiply(double[][] A, double[][] B) {

		int aRows = A.length;
		int aColumns = A[0].length;
		int bRows = B.length;
		int bColumns = B[0].length;

		if (aColumns != bRows) {
			return null;
		}

		double[][] C = new double[aRows][bColumns];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				C[i][j] = 0.00000;
			}
		}

		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					if (j == 0) {
					}
					C[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return C;
	}
	
	/**
	 * Multiplie cette matrice de manière à avoir A.this.matrice
	 * @param A
	 */
	public void multiply(double[][] A) {

		int aRows = A.length;
		int aColumns = A[0].length;
		int bRows = this.matrice.length;
		int bColumns = this.matrice[0].length;

		if (aColumns != bRows) {
			this.matrice =  null;
		} else {
			double[][] C = new double[aRows][bColumns];
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					C[i][j] = 0.00000;
				}
			}

			for (int i = 0; i < aRows; i++) { // aRow
				for (int j = 0; j < bColumns; j++) { // bColumn
					for (int k = 0; k < aColumns; k++) { // aColumn
						if (j == 0) {
						}
						C[i][j] += A[i][k] * this.matrice[k][j];
					}
				}
			}
			copyFromMatrix(C);
		}
	}

	public static double[][] addMatrices(double[][] matrixA, double[][] matrixB) {
		
		int rows = matrixA.length;
		int cols = matrixA[0].length;

		double[][] sum = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i < 3 && j == 0) {
				}
				sum[i][j] = matrixA[i][j] + matrixB[i][j];
			}
		}
		return sum;
	}

	public double[][] getMatrice() {
		return matrice;
	}

	public void emptyMatrice(Matrice mat) {
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < aColumns; j++) {
				mat.getMatrice()[i][j] = 0.0;
			}
		}
	}

	/**
	 * Rotation autour de l'axe X
	 * 
	 * @param angle
	 */
	public void rotateX(FigureModel fig, double angle) {
		double rad = Math.toRadians(angle);

		//	@formatter:off
		double[][] xRotation = new double[][] { 
			{ 1.0, 0.0, 0.0, 0.0 }, 
			{ 0.0, Math.cos(rad), -Math.sin(rad), 0.0 }, 
			{ 0.0, Math.sin(rad), Math.cos(rad), 0.0 }, 
			{ 0.0, 0.0, 0.0, 1.0 } };
		//	@formatter:on

		translateMatrix(-fig.getCenter().getX(), -fig.getCenter().getY(), -fig.getCenter().getZ());
		this.matrice = multiply(xRotation, this.matrice);
		translateMatrix(fig.getCenter().getX(), fig.getCenter().getY(), fig.getCenter().getZ());
	}

	/**
	 * Rotation autout de l'axe Y
	 * 
	 * @param angle
	 */
	public void rotateY(FigureModel fig, double angle) {
		double rad = Math.toRadians(angle);

		//	@formatter:off
		double[][] yRotation = new double[][] { 
			{ Math.cos(rad), 0.0, Math.sin(rad), 0.0 }, 
			{ 0.0, 1.0, 0.0, 0.0 }, 
			{ -Math.sin(rad), 0.0, Math.cos(rad), 0.0 }, 
			{ 0.0, 0.0, 0.0, 1.0 } };
		//	@formatter:on

		translateMatrix(-fig.getCenter().getX(), -fig.getCenter().getY(), -fig.getCenter().getZ());
		this.matrice = multiply(yRotation, this.matrice);
		translateMatrix(fig.getCenter().getX(), fig.getCenter().getY(), fig.getCenter().getZ());
	}

	/**
	 * Rotation autout de l'axe Z
	 * 
	 * @param angle
	 */
	public void rotateZ(FigureModel fig, double angle) {
		double rad = Math.toRadians(angle);

		//	@formatter:off
		double[][] zRotation = new double[][] { 
			{ Math.cos(rad), -Math.sin(rad), 0.0, 0.0 }, 
			{ Math.sin(rad), Math.cos(rad), 0.0, 0.0 }, 
			{ 0.0, 0.0, 1.0, 0.0 }, 
			{ 0.0, 0.0, 0.0, 1.0 } };
		//	@formatter:on

		translateMatrix(-fig.getCenter().getX(), -fig.getCenter().getY(), -fig.getCenter().getZ());
		this.matrice = multiply(zRotation, this.matrice);
		translateMatrix(fig.getCenter().getX(), fig.getCenter().getY(), fig.getCenter().getZ());
	}
	
	/**
	 * Applique une homothétie à cette matrice selon le rapport donné
	 * @param zoomLevel
	 */
	public void zoom(double zoomLevel) {
	//	@formatter:off
		int lines = this.matrice.length;
		int cols = lines;
		
//		{ zoomLevel, 0.0, 0.0, 0.0}, 
//		{ 0.0, zoomLevel, 0.0, 0.0},
//		{ 0.0, 0.0, zoomLevel, 0.0}, 
//		{ 0.0, 0.0, 0.0, 1.0 } };
		double[][] zoom = new double[lines][cols];
		for (int i=0;i<lines;i++) {
			for (int j=0;j<cols;j++) {
				if (i==j && i<lines-1) {
					zoom[i][j] = zoomLevel;
				} else {
					zoom[i][j] = 0.0;
				}
			}
		}
		zoom[lines-1][cols-1] = 1.0;
		this.matrice = multiply(zoom, this.matrice);
	// 	@formatter:on
	}
		
	/**
	 * Applique une translation à cette matrice selon les paramètres données.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translateMatrix(double x, double y, double z) {
	//	@formatter:off
		double[][] translate = new double[][] { 
			{ 1, 0.0, 0.0, x}, 
			{ 0.0, 1, 0.0, y},
			{ 0.0, 0.0, 1, z}, 
			{ 0.0, 0.0, 0.0, 1.0 } };
	// 	@formatter:on
		this.matrice = multiply(translate, this.matrice);
	}
	
	/**
	 * Stocke des Point dans la matrice. <br>
	 * <br><b>ATTENTION</b> a toujours appliquer {@link #setHomogeneousCoords()} après.
	 * @param points
	 * @param nbCoords nombre de coordonnées à stocker
	 */
	public void importPoints(List<Point> points, int nbCoords) {
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;

		for (int i = 0; i < aColumns; i++) {
			for (int j = 0; j < 3; j++) {
				Point tmpPoint = points.get(i);
				this.matrice[j][i] = tmpPoint.getCoord(j);
			}
		}
	}

	/**
	 * Stocke la matrice dans une List de Point. <br>
	 * Stocke autant de coordonnées dans le Point que la matrice a de lignes jusqu'à 3 maximum.
	 * 
	 * @param points
	 */
	public void exportToPoints(List<Point> points) {
		int aColumns = this.matrice[0].length;

		for (int i = 0; i < aColumns; i++) {
			Point pt = points.get(i);
			pt.resetCoords();
			for (int j = 0; j < 3; j++) {
				pt.add(this.matrice[j][i]);
			}
		}
	}

	public String toStringMatrice() {
		String result = "";
		for (int i = 0; i < this.matrice.length; i++) {
			// for(int j = 0; j < m[i].length; j++) {
			for (int j = 0; j < 3; j++) {
				result += String.format("%11.2f", this.matrice[i][j]);
			}
			result += "\n";
		}
		return "\nMatrice =\n" + result;
	}
	
	public static String toStringMatrice(double[][] m) {
		String result = "";
		for (int i = 0; i < m.length; i++) {
			// for(int j = 0; j < m[i].length; j++) {
			for (int j = 0; j < 3; j++) {
				result += String.format("%11.2f", m[i][j]);
			}
			result += "\n";
		}
		return "\nMatrice =\n" + result;
	}
	
	/**
	 * Donne une visualisation de la matrice
	 * @param length le nombre de colonnes à afficher
	 * @return
	 */
	public String toStringMatrice(int length) {
		String result = "";
		for (int row = 0; row < this.matrice.length; row++) {
			// for(int j = 0; j < m[i].length; j++) {
			for (int j = 0; j < this.matrice[row].length && j < length; j++) {
				result += String.format("%11.2f", this.matrice[row][j]);
			}
			result += "\n";
		}
		return "\nMatrice =\n" + result;
	}

	/**
	 * Met la dernière ligne de la matrice à 1 pour avoir des coordonnées homogènes
	 */
	public void setHomogeneousCoords() {
		int lastRow = this.matrice.length - 1;
		int aColumns = this.matrice[0].length;
		for (int i = 0; i < aColumns; i++) {
			this.matrice[lastRow][i] = 1.0;
		}
	}

}
