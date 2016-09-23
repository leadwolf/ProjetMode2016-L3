import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
 
public class Panneau extends JPanel { 
	
	List<Point> points = new ArrayList<>();
	List<Face> faces = new ArrayList<>();

  public void paintComponent(Graphics g){
	           //x  //y
	  g.drawLine(0, 200, 400, 200);
	  g.drawLine(200, 0, 200, 400);
	  //g.drawOval(200 + x + 0.2* z, 200 + y + 0.5 * z, 2, 2);
	  g.drawOval(200+ 1 + 4 * -1, 200 -1 + 10 * -1, 2, 2);
  }
  
}