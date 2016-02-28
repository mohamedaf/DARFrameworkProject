package point;

import httpServlet.IHttpServlet;

import java.net.URL;
import java.util.Map;

import model.request.IHttpRequest;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

public class PointController implements IHttpServlet {

    private Points points = new Points();

    private void setHttpResponseError(IHttpResponse resp, HttpResponseStatus status) {
	
	resp.setStatus(status);
	resp.setBody("Error " + status.getStatus() + " " + status.name());
	
    }

    @Override
    public void doGet(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "getPointList":
	    getPointList(resp);
	    break;
	case "getPoint":
	    getPoint(req, resp);
	    break;
	case "getX":
	    getPointCoord(req, resp, "x");
	case "getY":
	    getPointCoord(req, resp, "y");
	default:
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    break;
	}
	
    }

    private void getPointList(IHttpResponse resp) {
	
	resp.setBody(points.toString());
	resp.setContentLength(points.toString().length());
	
    }

    private void getPoint(IHttpRequest req, IHttpResponse resp) {
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	
	if (p == null) {
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	} else {
	    resp.setBody(p.toString());
	    resp.setContentLength(p.toString().length());
	}
	
    }

    private void getPointCoord(IHttpRequest req, IHttpResponse resp, String coord) {
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	if (coord.equalsIgnoreCase("x")) {
	    String body = "x = " + p.getX();
	    resp.setBody(body);
	    resp.setContentLength(body.length());
	} else if (coord.equalsIgnoreCase("y")) {
	    String body = "y = " + p.getY();
	    resp.setBody(body);
	    resp.setContentLength(body.length());
	} else {
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }
    
    private int getInd(IHttpRequest req) {
	
	URL url = req.getUrl();
	String path = url.getPath();
	String[] splittedPath = path.split("/");
	return Integer.parseInt(splittedPath[1]);
	
    }

    @Override
    public void doPut(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "modifyPoint":
	    modifyPoint(req, resp);
	    break;
	default:
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    break;
	}
	
    }

    private void modifyPoint(IHttpRequest req, IHttpResponse resp) {
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	
	if (p == null) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	} else {
	    Map<String, String> params = req.getParams();
	    p.setX(Integer.parseInt(params.get("x")));
	    p.setY(Integer.parseInt(params.get("y")));
	    String body = "The point has been modified, new value :" + p.toString();
	    resp.setBody(body);
	    resp.setContentLength(body.length());
	}
	
    }

    @Override
    public void doPost(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "addPoint":
	    addPoint(req, resp);
	    break;
	default:
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    break;
	}

    }

    public void addPoint(IHttpRequest req, IHttpResponse resp) {
	
	if (req.getBody().isEmpty()) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}
	
	String[] coords = req.getBody().split("&");
	
	if (coords.length < 2) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	String[] coordX = coords[0].split("=");
	String[] coordY = coords[1].split("=");
	int x = Integer.parseInt(coordX[1]);
	int y = Integer.parseInt(coordY[1]);
	Point p = points.addPoint(x, y);
	String body = "new Point created : " + p.toString();
	resp.setBody(body);
	resp.setContentLength(body.length());
	
    }

    @Override
    public void doDelete(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "deletePoint":
	    deletePoint(req, resp);
	    break;
	default:
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    break;
	}
	
    }

    private void deletePoint(IHttpRequest req, IHttpResponse resp) {
	
	String body = points.getPoints().toString() + " Removed";
	int ind = getInd(req);
	points.getPoints().remove(ind);
	resp.setBody(body);
	resp.setContentLength(body.length());
	
    }

    public String toString() {
	return points.toString();
    }

}
