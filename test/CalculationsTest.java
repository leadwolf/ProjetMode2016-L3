import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
	
	@Test
	public void testPointTranslation() {
		List<Point> points = new ArrayList<>();
		Point point1 = new Point(1.0, 0.0);
		Point point2 = new Point(0.0, 2.0);
		Point point3 = new Point(2.0, 2.0);
		points.add(point1);
		points.add(point2);
		points.add(point3);
		
		List<Point> expected = new ArrayList<>();
		Point epoint1 = new Point(11.0, 10.0);
		Point epoint2 = new Point(10.0, 12.0);
		Point epoint3 = new Point(12.0, 12.0);
		expected.add(epoint1);
		expected.add(epoint2);
		expected.add(epoint3);
		int moveX = 10;
		int moveY = 10;
		
		Calculations.translatePoints(points, moveX, moveX);
		
		for (int i=0;i<points.size();i++) {
			assertEquals(points.get(i), expected.get(i));
		}
	}
}
