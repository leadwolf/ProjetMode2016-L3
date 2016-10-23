import java.util.List;

public class Matrice {

	private double[][] matrice;

	public Matrice(int width, int height) {
		matrice = new double[height][width];
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				this.matrice[i][j] = 0.0;
			}
		}
	}

    private static double[][] multiply(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) {
        	return null; // matrice multiplication is not possible
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
        
	public void setMatrice(double[][] matrice) {
		this.matrice = matrice;
	}

	public double[][] getMatrice() {
		return matrice;
	}
	
	public void emptyMatrice(Matrice mat) {
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				mat.getMatrice()[i][j] = 0.0;
			}
		}
	}

	
	/**
	 * Rotation autour de l'axe X
	 * @param angle
	 */
	public void rotateX(double angle) {
    	double rad = Math.toRadians(angle);
    	double[][] xRotation = new double[][] { { 1.0, 0.0, 0.0, 0.0 }, { 0.0, Math.cos(rad), -Math.sin(rad), 0.0 }, { 0.0, Math.sin(rad), Math.cos(rad), 0.0 },
    		{0.0, 0.0, 0.0, 1.0}};
		this.matrice = multiply(xRotation, this.matrice);
	}

	/**
	 * Rotation autout de l'axe Y
	 * @param angle
	 */
	public void rotateY(double angle) {
    	double rad = Math.toRadians(angle);
		double[][] yRotation = new double[][] { { Math.cos(rad), 0.0, Math.sin(rad), 0.0 }, { 0.0, 1.0, 0.0, 0.0 }, { -Math.sin(rad), 0.0, Math.cos(rad),0.0 },
		{0.0, 0.0, 0.0, 1.0} };
		this.matrice = multiply(yRotation, this.matrice);
	}

	/**
	 * Rotation autout de l'axe Z
	 * @param angle
	 */
	public void rotateZ(double angle) {
    	double rad = Math.toRadians(angle);
		double[][] zRotation = new double[][] { { Math.cos(rad), -Math.sin(rad), 0.0, 0.0 }, { Math.sin(rad), Math.cos(rad), 0.0, 0.0 }, { 0.0, 0.0, 1.0, 0.0 },
			{0.0, 0.0, 0.0, 1.0} };
		this.matrice = multiply(zRotation, this.matrice);
	}
	
	/**
	 * Remplit la matrice de 0.0 et puis stocke des Point dans la matrice.
	 * <br>Stocke jusqu'a 3 coordonnées par point
	 * @param points
	 */
	public void importPoints(List<Point> points) {
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				this.matrice[i][j] = 0.0;
			}
		}
		
		for (int i=0;i<aColumns;i++) {
			for (int j=0;j<3;j++) {
				Point tmpPoint = points.get(i);
				this.matrice[j][i] = tmpPoint.getCoord(j);
			}
		}
	}
	
	/**
	 * Stocke la matrice dans une List de Point.
	 * <br>Stocke autant de coordonnées dans le Point que la matrice a de lignes.
	 * @param points
	 */
	public void exportToPoints(List<Point> points) {
		int aRows = this.matrice.length;
		int aColumns = this.matrice[0].length;
		
		for (int i=0;i<aColumns;i++) {
			Point pt = points.get(i);
			pt.resetCoords();
			for (int j=0;j<3;j++) {
				pt.add(this.matrice[j][i]);
			}
		}
	}
	
    public static String toStringMatrice(double[][] m) {
        String result = "";
        for(int i = 0; i < m.length; i++) {
            //for(int j = 0; j < m[i].length; j++) {
        	for(int j = 0; j < 3; j++) {
                result += String.format("%11.2f", m[i][j]);
            }
            result += "\n";
        }
        return "\nMatrice =\n" + result;
    }
    
    public void setHomogeneousCoords() {
    	int lastRow = this.matrice.length-1;
		int aColumns = this.matrice[0].length;
    	for (int i=0;i<aColumns;i++) {
    		this.matrice[lastRow][i] = 1.0;
    	}
    }
	
}
