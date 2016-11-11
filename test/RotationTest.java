import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import modele.Point;

public class RotationTest {

	@Test
	public void test() {
		Point topLeft = new Point(0.0, 0.0);
		Point topRight = new Point(1.0, 0.0);
		Point bottomLeft = new Point(0.0, 4.0);
		Point bottomRight = new Point(2.0, 4.0);
		Point midRight = new Point(2.0, 3.0);
		Point midMid = new Point(1.0, 3.0);
		
		List<Point> shapeL = new ArrayList<>();
		shapeL.add(topLeft);
		shapeL.add(topRight);
		shapeL.add(bottomLeft);
		shapeL.add(bottomRight);
		shapeL.add(midRight);
		shapeL.add(midMid);
		/*
<<<<<<< HEAD
		Calculations.rotateX(shapeL, 90); // 1 = 90°
=======
		Calculations.rotateX(shapeL, 1.5); // 1 = 90°
>>>>>>> 9a12a0e659142a31caaa45e1c4284417d6918c6a
		

		Point ExtopLeft = new Point(0.0, 0.0);
		Point ExtopRight = new Point(1.0, 0.0);
		Point ExbottomLeft = new Point(0.0, 4.0);
		Point ExbottomRight = new Point(2.0, 4.0);
		Point ExmidRight = new Point(2.0, 3.0);
		Point ExmidMid = new Point(1.0, 3.0);

		assertEquals(ExtopLeft, topLeft);
		assertEquals(ExtopRight, topRight);
		assertEquals(ExbottomLeft, bottomLeft);
		assertEquals(ExbottomRight, bottomRight);
		assertEquals(ExmidRight, midRight);
		assertEquals(ExmidMid, midMid);
		*/
		
	}

}
