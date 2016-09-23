import java.util.ArrayList;
import java.util.List;

public class Face {
	
	private int nbPoint;
	private List<Point> list;
	private List<Segment> segmentList;
	
	
	public Face() {
		list = new ArrayList<>();
		segmentList = new ArrayList<>();
	}
	
	public Face(int nbPoint, List<Point> list){
		this.nbPoint=nbPoint;
		this.list=list;
		segmentList = new ArrayList<>();
		for(int i=0;i<list.size()-1;i++){
			segmentList.add(new Segment(list.get(i),list.get(i+1)));
		}
		segmentList.add(new Segment(list.get(list.size()-1),list.get(0)));
		
	}
	
	public void addPoint(Point point) {
		list.add(point);
	}

	@Override
	public String toString() {
		String res = "";
		res += "Face [list=";
		if (list.size()>1) {
			for (Point pt : list) {
				res += "\n\t" + pt;
			}
			res += "] ";
			res += "Segment [list=";
			for (Segment st : segmentList) {
				res += "\n\t" + st;
			}
			res += "] ";
		} else {
			return "Face [list=" + list + "]";
		}
		return res;
	}
	
	public List<Point> getList() {
		return list;
	}
	
}
