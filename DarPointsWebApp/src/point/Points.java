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

    public String toString(String contentType, String contentEncoding) {

	if (contentType.startsWith("text/html")) {
	    return this.getHtmlResponse(contentEncoding);
	}
	if (contentType.startsWith("application/json")) {
	    return this.getJsonResponse();
	}
	else {
	    return this.getTextResponse();
	}
	
    }
    
    public String getHtmlResponse(String contentEncoding) {
	
	StringBuilder pointsStr = new StringBuilder(
		"<!DOCTYPE html>\n<html>\n"
			+ "<head> \n<meta charset=\""
			+ contentEncoding
			+ "\">\n"
			+ "<title>Liste de points</title>\n</head>\n<body>\n"
			+ "Liste de points:</br><ul>\n");
	
	for (Integer ind : points.keySet()) {
	    Point p = points.get(ind);
	    pointsStr.append("<li>" + p.toString("text/html") + "</li>");
	}
	
	pointsStr.append("</ul></body>\n</html>");
	return pointsStr.toString();
	
    }
    
    public String getJsonResponse() {
	
	StringBuilder pointsStr = new StringBuilder("{[");
	Point p;

	for (Integer ind : points.keySet()) {
	    p = points.get(ind);
	    pointsStr.append(p.toString("application/json") + ",");
	}
	
	if(!points.isEmpty())
	    pointsStr = pointsStr.delete(pointsStr.length()-1, pointsStr.length());
	pointsStr.append("]}");
	return pointsStr.toString();
	
    }
    
    public String getTextResponse() {
	
	StringBuilder pointsStr = new StringBuilder("Liste des points :\n ");
	Point p;

	for (Integer ind : points.keySet()) {
	    p = points.get(ind);
	    pointsStr.append(p.toString("text/plain") + " ");
	}
	return pointsStr.toString();
	
    }

}
