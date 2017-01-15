package mathTest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ply.math.Matrice;

/**
 * Test des méthodes de Matrices
 * 
 * @author L3
 *
 */
public class MatriceTest {

	/**
	 * Matrice 3x3 basique
	 */
	Matrice mat1;
	/**
	 *  Matrice 3x3 basique
	 */
	Matrice mat2;
	/**
	 *  Matrice 3x4 pour translation x, y, z
	 */
	Matrice mat3;

	/**
	 * Création des matrices
	 */
	@Before
	public void setupMatrices() {
		//	@formatter:off
		mat1 = new Matrice(new double[][] { 
			{ 1.0, 2.0, 5.0 },
			{ 5.0, 6.0, 7.0 },
			{ 9.0, 1.0, 3.0 } });

		mat2 = new Matrice(new double[][] {
			{ 5.0, 1.0, 3.0 },
			{ 8.0, 4.0, 5.0 },
			{ 7.0, 2.0, 4.0 } });
		
		mat3 = new Matrice(new double[][] { 
			{ 1.0, 2.0, 5.0 },
			{ 5.0, 6.0, 7.0 },
			{ 9.0, 1.0, 3.0 },
			{ 1.0, 1.0, 1.0 } });
		// 	@formatter:on
	}

	/**
	 * Test Matrice.add()
	 */
	@Test
	public void testAdd() {
		//	@formatter:off
		Matrice expected = new Matrice(new double[][] {
			{ 6.0, 3.0, 8.0 },
			{ 13.0, 10.0, 12.0 },
			{ 16.0, 3.0, 7.0 } });

		// 	@formatter:on
		Matrice result = new Matrice(Matrice.addMatrices(mat1.getMatrice(), mat2.getMatrice()));

		for (int i = 0; i < expected.getMatrice().length; i++) {
			for (int j = 0; j < expected.getMatrice()[0].length; j++) {
				assertEquals(expected.getMatrice()[i][j], result.getMatrice()[i][j], 0.001);
			}
		}

	}

	/**
	 * Test Matrice.zoom()
	 */
	@Test
	public void testScale() {
		//	@formatter:off
		Matrice expectedMat1 = new Matrice(new double[][] {
			{ 5.0, 10.0, 25.0 },
			{ 25.0, 30.0, 35.0 },
			{ 9.0, 1.0, 3.0 } });
		
		Matrice expectedMat2 = new Matrice(new double[][] {
			{ 250.0, 50.0, 150.0 },
			{ 400.0, 200.0, 250.0 },
			{ 7.0, 2.0, 4.0 } });

		// 	@formatter:on
		mat1.zoom(5);
		mat2.zoom(50);

		for (int i = 0; i < expectedMat1.getMatrice().length; i++) {
			for (int j = 0; j < expectedMat1.getMatrice()[0].length; j++) {
				assertEquals(expectedMat1.getMatrice()[i][j], mat1.getMatrice()[i][j], 0.001);
				assertEquals(expectedMat2.getMatrice()[i][j], mat2.getMatrice()[i][j], 0.001);
			}
		}
	}
	
	/**
	 * Test Matrice.translate()
	 */
	@Test
	public void testTranslate() {
		//	@formatter:off
		Matrice expectedMat1 = new Matrice(new double[][] {
			{ 79.0, 80.0, 83.0 },
			{ 29.0, 30.0, 31.0 },
			{ 15.0, 7.0, 9.0 },
			{ 1.0, 1.0, 1.0 }});

		// 	@formatter:on
		mat3.translateMatrix(78, 24, 6);

		for (int i = 0; i < expectedMat1.getMatrice().length; i++) {
			for (int j = 0; j < expectedMat1.getMatrice()[0].length; j++) {
				assertEquals(expectedMat1.getMatrice()[i][j], mat3.getMatrice()[i][j], 0.001);
			}
		}
	}

	/**
	 * Test Matice.multiply()
	 */
	@Test
	public void testMultiply() {
		//	@formatter:off
		Matrice expected = new Matrice(new double[][] {
			{ 56.0, 19.0, 33.0 },
			{ 122.0, 43.0, 73.0 },
			{ 74.0, 19.0, 44.0 } });

		// 	@formatter:on
		Matrice result = new Matrice(Matrice.multiply(mat1.getMatrice(), mat2.getMatrice()));

		for (int i = 0; i < expected.getMatrice().length; i++) {
			for (int j = 0; j < expected.getMatrice()[0].length; j++) {
				assertEquals(expected.getMatrice()[i][j], result.getMatrice()[i][j], 0.001);
			}
		}

	}

}
