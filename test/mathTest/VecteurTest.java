package mathTest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import math.Vecteur;
import ply.modeles.Point;

public class VecteurTest {

	Vecteur vec1, vec2;
	
	@Before
	public void initVecteur() {
		Point pt1 = new Point(5.0, 5.0, 5.0);
		Point pt2 = new Point(15.0, 15.0, 15.0);
		Point pt3 = new Point(5.0, 8.0, 4.0);
		Point pt4 = new Point(5.0, 15.0, 9.0);
		vec1 = new Vecteur(pt1, pt2);
		vec2 = new Vecteur(pt3, pt4);
	}
	
	@Test
	public void testVecteur() {
		double[] expected = new double[]{10.0, 10.0, 10.0};
		for (int i=0;i<3;i++) {
			assertEquals(expected[i], vec1.getVecteur()[i], 0.001);
		}
	}
	
	@Test
	public void testNorme() {
		assertEquals(Math.sqrt(300), vec1.getNorme(), 0.001);
		assertEquals(Math.sqrt(74), vec2.getNorme(), 0.001);
	}
	
	@Test
	public void testProdScalaire() {
		assertEquals(120, Vecteur.prodScalaire(vec1, vec2), 0.001);
	}
	
	@Test
	public void testProdVectoriel() {
		Vecteur expected = new Vecteur(new double[]{-20.0, -50.0, 70.0});
		assertTrue(expected.equals(Vecteur.prodVectoriel(vec1, vec2)));
	}

}
