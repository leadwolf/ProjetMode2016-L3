import java.util.Arrays;
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

    public static double[][] rotateAroundX(double[][] m2, double angle) {
    	double[][] xRotation = new double[][] { { 1.0, 0.0, 0.0 }, { 0.0, Math.cos(angle), -Math.sin(angle) }, { 0.0, Math.sin(angle), Math.cos(angle) } };
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
    
    public static double[][] rotateAroundY(double[][] m2, double angle) {
		double[][] yRotation = new double[][] { { Math.cos(angle), 0.0, Math.sin(angle) }, { 0.0, 1.0, 0.0 }, { -Math.sin(angle), 0.0, Math.cos(angle) } };
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
    
    public static double[][] rotateAroundZ(double[][] m2, double angle) {
		double[][] zRotation = new double[][] { { Math.cos(angle), -Math.sin(angle), 0.0 }, { Math.sin(angle), Math.cos(angle), 0.0 }, { 0.0, 0.0, 1.0 } };
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


	public void rotateX(double angle) {
		this.matrix = rotateAroundX(this.matrix, angle);
	}

	
	public void rotateY(double angle) {
		this.matrix = rotateAroundY(this.matrix, angle);
	}

	
	public void rotateZ(double angle) {
		this.matrix = rotateAroundZ(this.matrix, angle);
	}
	
	public void importPoints(List<Point> points) {
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		for (int i=0;i<aRows;i++) {
			for (int j=0;j<aColumns;j++) {
				this.matrix[i][j] = 0.0;
			}
		}
		
		for (int i=0;i<aColumns;i++) {
			for (int j=0;j<aRows;j++) {
				Point tmpPoint = points.get(i);
				this.matrix[j][i] = tmpPoint.getCoord(j);
			}
		}
	}
	
	public void exportToPoints(List<Point> points) {
		int aRows = this.matrix.length;
		int aColumns = this.matrix[0].length;
		
		for (int i=0;i<aColumns;i++) {
			Point pt = points.get(i);
			pt.resetCoords();
			for (int j=0;j<aRows;j++) {
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
	
}
