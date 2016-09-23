import java.util.ArrayList;
import java.util.List;

public class Face {
	
	private int nbPoint;
	private List<Point> pointList;
	private List<Segment> segmentList;
	
	
	public Face() {
		pointList = new ArrayList<>();
		segmentList = new ArrayList<>();
	}
	
	public Face(int nbPoint, List<Point> list){
		this.nbPoint=nbPoint;
		this.pointList=list;
		segmentList = new ArrayList<>();
		for(int i=0;i<list.size()-1;i++){
			segmentList.add(new Segment(list.get(i),list.get(i+1)));
		}
		segmentList.add(new Segment(list.get(list.size()-1),list.get(0)));
		
	}
	
	public void addPoint(Point point) {
		pointList.add(point);
	}

	@Override
	public String toString() {
		String res = "";
		res += "Face [list=";
		if (pointList.size()>1) {
			for (Point pt : pointList) {
				res += "\n\t" + pt;
			}
			res += "] ";
			res += "Segment [list=";
			for (Segment st : segmentList) {
				res += "\n\t" + st;
			}
			res += "] ";
		} else {
			return "Face [list=" + pointList + "]";
		}
		return res;
	}
	
	public List<Point> getList() {
		return pointList;
	}
	
	public double[] getListX() {
		double[] listX = new double[pointList.size()-1];
		for (int i=0;i<pointList.size()-1;i++) {
			listX[i] = pointList.get(i).getX();
		}
		return listX;
	}
	
	public double[] getListY() {
		double[] listY = new double[pointList.size()-1];
		for (int i=0;i<pointList.size()-1;i++) {
			listY[i] = pointList.get(i).getY();
		}
		return listY;
	}
	
}
