package point;

import java.util.HashMap;
import java.util.Map;

public class Points {

    private static int id = 0;
    private final Map<Integer, Point> points;

    public Points() {
	this.points = new HashMap<Integer, Point>();
    }

    public Point addPoint(int x, int y) {
	
	Point p = new Point(id, x, y);
	points.put(id, p);
	id++;
	return p;
	
    }

    public Point getPoint(int id) {
	return points.get(id);
    }

    public Map<Integer, Point> getPoints() {
	return points;
    }

    @Override
    public String toString() {
	
	StringBuilder pointsStr = new StringBuilder("{ ");
	Point p;

	for (Integer ind : points.keySet()) {
	    p = points.get(ind);
	    pointsStr.append(p.toString() + " ");
	}

	pointsStr.append("}");
	return pointsStr.toString();
	
    }

}
