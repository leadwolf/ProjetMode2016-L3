package ply.modeles;
import java.util.ArrayList;
import java.util.List;

public class Face implements Comparable<Face>{
	
	private int nbPoint;
	private List<Point> pointList;
	
	
	public Face() {
		pointList = new ArrayList<>();
	}
	
	/**
	 * Cree une face � partir d'un nombre de points et d'une liste de points
	 * @param nbPoint
	 * @param list
	 */
	public Face(int nbPoint, List<Point> list){
		this.nbPoint=nbPoint;
		this.pointList=list;
		
	}
	
	/**
	 * Ajoute le point à la liste des points de la face
	 * @param point
	 */
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
			res += "] ";
		} else {
			return "Face [list=" + pointList + "]";
		}
		return res;
	}
	
	public List<Point> getList() {
		return pointList;
	}
	
	/**
	 * Compare by average Z
	 */
	@Override
	public int compareTo(Face o) {
		double zO1 = 0.0, zO2 = 0.0;
		for (Point pt : this.getList()) {
			zO1 += pt.getZ();
		}
		zO1 /= this.getList().size();

		for (Point pt : o.getList()) {
			zO2 += pt.getZ();
		}
		zO2 /= o.getList().size();
		
		if (zO1 > zO2) {
			return 1;
		} else if (zO1 < zO2) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
