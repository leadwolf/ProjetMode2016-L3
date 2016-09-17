import java.util.ArrayList;
import java.util.List;

public class Face {
	
	private int nbPoint;
	private List<Point> list;
	
	
	public Face() {
		list = new ArrayList<>();
	}
	
	public Face(int nbPoint, List<Point> list){
		this.nbPoint=nbPoint;
		this.list=list;
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
			res += "]";
		} else {
			return "Face [list=" + list + "]";
		}
		return res;
	}
	
	public List<Point> getList() {
		return list;
	}
	
}
