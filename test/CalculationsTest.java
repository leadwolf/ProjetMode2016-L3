import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */

/**
 * @author L3
 *
 */
public class CalculationsTest {

	@Test
	public void testPointTransformation1() {
		Point initial = new Point(10.0, 10.0, 10.0);
		Point expected = new Point(100.0, -300.0);
		Point result = new Point();
		Calculations.transformePoint(initial, result);
		assertEquals(expected, result);
		
	}
	@Test
	public void testPointTransformation2() {
		Point initial = new Point(5.0, 5.0, 10.0);
		Point expected = new Point(100.0, -300.0);
		Point result = new Point();
		Calculations.transformePoint(initial, result);
		assertFalse(null, expected.equals(result));
	}

}
