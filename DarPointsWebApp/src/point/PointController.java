package point;

import httpServlet.IHttpServlet;

import java.net.URL;
import java.util.Map;

import model.request.IHttpRequest;
import model.response.HttpResponseError;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

public class PointController implements IHttpServlet {

    private Points points = new Points();

    @Override
    public void doGet(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "getpointlist":
	    getPointList(resp);
	case "getpoint":
	    getPoint(req, resp);
	case "getx":
	    getPointCoord(req, resp, "x");
	case "gety":
	    getPointCoord(req, resp, "y");
	default:
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    private void getPointList(IHttpResponse resp) {
	resp.setBody(points.toString());
    }

    private void getPoint(IHttpRequest req, IHttpResponse resp) {
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	
	if (p == null) {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	} else {
	    resp.setBody(p.toString());
	}
	
    }

    private void getPointCoord(IHttpRequest req, IHttpResponse resp, String coord) {
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	if (coord.equalsIgnoreCase("x")) {
	    String body = "x = " + p.getX();
	    resp.setBody(body);
	} else if (coord.equalsIgnoreCase("y")) {
	    String body = "y = " + p.getY();
	    resp.setBody(body);
	} else {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    @Override
    public void doPut(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "modifypoint":
	    modifyPoint(req, resp);
	default:
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    private void modifyPoint(IHttpRequest req, IHttpResponse resp) {
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	
	if (p == null) {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	} else {
	    Map<String, String> params = req.getParams();
	    p.setX(Integer.parseInt(params.get("x")));
	    p.setY(Integer.parseInt(params.get("y")));
	    String body = "This point has been modified, new value :" + p.toString();
	    resp.setBody(body);
	}
	
    }

    @Override
    public void doPost(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "addpoint":
	    addPoint(req, resp);
	default:
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }

    public void addPoint(IHttpRequest req, IHttpResponse resp) {
	
	if (req.getBody().isEmpty()) {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}
	
	String[] coords = req.getBody().split("&");
	
	if (coords.length < 2) {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	String[] coordX = coords[0].split("=");
	String[] coordY = coords[1].split("=");
	
	try {
	    int x = Integer.parseInt(coordX[1]);
	    int y = Integer.parseInt(coordY[1]);
	    Point p = points.addPoint(x, y);
	    String body = "new Point created : " + p.toString();
	    resp.setBody(body);
	} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	}
	
    }

    @Override
    public void doDelete(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "deletepoint":
	    deletePoint(req, resp);
	default:
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    private void deletePoint(IHttpRequest req, IHttpResponse resp) {
	
	String body = "Point : " + points.getPoints().toString() + " was removed";
	int ind = getInd(req);
	points.getPoints().remove(ind);
	resp.setBody(body);
	
    }
    
    private int getInd(IHttpRequest req) {
	
	URL url = req.getUrl();
	String path = url.getPath();
	String[] splittedPath = path.split("/");
	return Integer.parseInt(splittedPath[1]);
	
    }
    
    public String toString() {
	return points.toString();
    }

}
