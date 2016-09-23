import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
 
public class Panneau extends JPanel { 

	List<Point> points = new ArrayList<>();
	List<Point> pointsTransformes = new ArrayList<>();
	List<Face> faces = new ArrayList<>();

  public void paintComponent(Graphics gg){
	  
	  Graphics2D g = (Graphics2D) gg;
	  g.drawLine(0, 200, 400, 200);
	  g.drawLine(200, 0, 200, 400);
	  
	  for (Point pt : pointsTransformes) {
		  Ellipse2D.Double shape = new Ellipse2D.Double( getWidth()/2 + pt.x, getHeight()/2 + pt.y, 7, 7);
		  System.out.println("drawing point =" + shape.x + " " +  shape.y);
		  g.fill(shape);
	  }
	  g.drawOval(200+ 1 + 4 * -1, 200 -1 + 10 * -1, 2, 2);
  }
  
  private double transformeX(Point pt) {
	  return ( (pt.x * 20)  + (10* pt.z));
  }
  
  private double transformeY(Point pt) {
	  return ((pt.y * 20) + (10* pt.z));
  }
  
  private void transformePoints() {
	  for (Point pt : points) {
		  pointsTransformes.add(new Point());
		  Point tmp = pointsTransformes.get(pointsTransformes.size()-1);
		  tmp.add(transformeX(pt));
		  tmp.add(transformeY(pt));
	  }
  }
  
  public void setPoints(List<Point> points) {
	  this.points = points;
	  transformePoints();
  }
  
}