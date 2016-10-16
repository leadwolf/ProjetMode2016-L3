import java.util.List;

public class Matrice {

	private double[][] matrix;

	public Matrice(int width, int height) {
		matrix = new double[height][width];
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				this.matrix[i][j] = 0.0;
			}
		}
	}

    private static double[][] multiply(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) {
        	return null; // matrix multiplication is not possible
        }
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }
    
    private static double[][] rotateAroundX(double[][] m2, double radian) {
    	double[][] xRotation = new double[][] { { 1.0, 0.0, 0.0, 0.0 }, { 0.0, Math.cos(radian), -Math.sin(radian), 0.0 }, { 0.0, Math.sin(radian), Math.cos(radian), 0.0 },
    		{0.0, 0.0, 0.0, 1.0}};
        int m1ColLength = xRotation[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) {
        	return null; // matrix multiplication is not possible
        }
        int mRRowLength = xRotation.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += xRotation[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }
    
    private static double[][] rotateAroundY(double[][] m2, double radian) {
		double[][] yRotation = new double[][] { { Math.cos(radian), 0.0, Math.sin(radian), 0.0 }, { 0.0, 1.0, 0.0, 0.0 }, { -Math.sin(radian), 0.0, Math.cos(radian),0.0 },
		{0.0, 0.0, 0.0, 1.0} };
        int m1ColLength = yRotation[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if (m1ColLength != m2RowLength){
        	return null; // matrix multiplication is not possible
        }
        int mRRowLength = yRotation.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += yRotation[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }
    
    private static double[][] rotateAroundZ(double[][] m2, double radian) {
		double[][] zRotation = new double[][] { { Math.cos(radian), -Math.sin(radian), 0.0, 0.0 }, { Math.sin(radian), Math.cos(radian), 0.0, 0.0 }, { 0.0, 0.0, 1.0, 0.0 },
			{0.0, 0.0, 0.0, 1.0} };
        int m1ColLength = zRotation[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) {
        	return null; // matrix multiplication is not possible
        }
        int mRRowLength = zRotation.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += zRotation[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}

	public double[][] getMatrix() {
		return matrix;
	}
	
	public void emptyMatrix(Matrice mat) {
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				mat.getMatrix()[i][j] = 0.0;
			}
		}
	}

	/**
	 * CE QU'IL FAUT FAIRE ???
	 * Rotation autour du Point
	 * @param angle
	 */
	public void rotateXByPoint(Point centre, double angle) {
    	double rad = Math.toRadians(angle);
		// A = T-1 . R
		// B = A . T
		// result = B . matrix
		// T-1 . R . T
		double[][] transInv = new double[][]{{1.0, 0.0, 0.0, centre.getX()}, {0.0, 1.0, 0.0, centre.getY()}, {0.0, 0.0, 1.0, centre.getZ()},
    		{0.0, 0.0, 0.0, 1.0}};
		double[][] trans = new double[][]{{1.0, 0.0, 0.0, -centre.getX()}, {0.0, 1.0, 0.0, -centre.getY()}, {0.0, 0.0, 1.0, -centre.getZ()},
    		{0.0, 0.0, 0.0, 1.0}};
    	double[][] xRotation = new double[][] { { 1.0, 0.0, 0.0, 0.0 }, { 0.0, Math.cos(rad), -Math.sin(rad), 0.0 }, { 0.0, Math.sin(rad), Math.cos(rad), 0.0 },
    		{0.0, 0.0, 0.0, 1.0}};
		double[][] transInvByRot = multiply(transInv, xRotation);
		double[][] second = multiply(transInvByRot, trans);
		this.matrix = multiply(second, this.matrix);
	}
	
	/**
	 * Rotation autour de l'axe X
	 * @param angle
	 */
	public void rotateX(double angle) {
    	double rad = Math.toRadians(angle);
		this.matrix = rotateAroundX(this.matrix, rad);
	}

	/**
	 * Rotation autout de l'axe Y
	 * @param angle
	 */
	public void rotateY(double angle) {
    	double rad = Math.toRadians(angle);
		this.matrix = rotateAroundY(this.matrix, rad);
	}

	/**
	 * Rotation autout de l'axe Z
	 * @param angle
	 */
	public void rotateZ(double angle) {
    	double rad = Math.toRadians(angle);
		this.matrix = rotateAroundZ(this.matrix, rad);
	}
	
	/**
	 * Remplit la matrice de 0.0 et puis stocke des Point dans la matrice.
	 * <br>Stocke jusqu'a 3 coordonnées par point
	 * @param points
	 */
	public void importPoints(List<Point> points) {
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				this.matrix[i][j] = 0.0;
			}
		}
		
		for (int i=0;i<aColumns;i++) {
			for (int j=0;j<3;j++) {
				Point tmpPoint = points.get(i);
				this.matrix[j][i] = tmpPoint.getCoord(j);
			}
		}
	}
	
	/**
	 * Stocke la matrice dans une List de Point.
	 * <br>Stocke autant de coordonnées dans le Point que la matrice a de lignes.
	 * @param points
	 */
	public void exportToPoints(List<Point> points) {
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		
		for (int i=0;i<aColumns;i++) {
			Point pt = points.get(i);
			pt.resetCoords();
			for (int j=0;j<3;j++) {
				pt.add(this.matrix[j][i]);
			}
		}
	}
	
    public static String toStringMatrix(double[][] m) {
        String result = "";
        for(int i = 0; i < m.length; i++) {
            //for(int j = 0; j < m[i].length; j++) {
        	for(int j = 0; j < 3; j++) {
                result += String.format("%11.2f", m[i][j]);
            }
            result += "\n";
        }
        return "\nMatrix =\n" + result;
    }
    
    public void setHomogeneousCoords() {
    	int lastRow = this.matrix.length-1;
		int aColumns = this.matrix[0].length;
    	for (int i=0;i<aColumns;i++) {
    		this.matrix[lastRow][i] = 1.0;
    	}
    }
	
}
