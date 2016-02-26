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
    public void doGet(IHttpRequest req, IHttpResponse resp) {
	
	URL url = req.getUrl();
	String host = url.getHost();
	String path = url.getPath();

	if (host.isEmpty()) {
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    return;
	}

	if (host.equalsIgnoreCase("list") && path.isEmpty()) {
	    getPointList(resp);
	    return;
	} else if (host.equalsIgnoreCase("p") && !path.isEmpty()) {
	    String[] splittedPath = path.split("/");
	    try {
		if (splittedPath.length == 2) {
		    getPoint(resp, Integer.parseInt(splittedPath[1]));
		    return;
		} else if (splittedPath.length == 3) {
		    int ind = Integer.parseInt(splittedPath[1]);
		    getPointCoord(resp, ind, splittedPath[2]);
		    return;
		}
	    } catch (NumberFormatException e) {
		setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		return;
	    }
	}

	setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	
    }

    private void getPointList(IHttpResponse resp) {
	
	resp.setBody(points.toString());
	resp.setContentLength(points.toString().length());
	
    }

    private void getPoint(IHttpResponse resp, int ind) {
	
	Point p = points.getPoint(ind);
	if (p == null) {
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	} else {
	    resp.setBody(p.toString());
	    resp.setContentLength(p.toString().length());
	}
	
    }

    private void getPointCoord(IHttpResponse resp, int ind, String coord) {
	
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

    @Override
    public void doPut(IHttpRequest req, IHttpResponse resp) {
	
	URL url = req.getUrl();
	String host = url.getHost();
	String path = url.getPath();

	if (host.isEmpty() || path.isEmpty()) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	if (host.equalsIgnoreCase("p")) {
	    String[] splittedPath = path.split("/");
	    if (splittedPath.length != 2) {
		setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		return;
	    }

	    try {
		int ind = Integer.parseInt(splittedPath[1]);
		Map<String, String> params = req.getParams();
		if (params.get("x") == null || params.get("y") == null) {
		    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		    return;
		}
		int x = Integer.parseInt(params.get("x"));
		int y = Integer.parseInt(params.get("y"));
		modifyPoint(resp, ind, x, y);
		return;
	    } catch (NumberFormatException e) {
		setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		return;
	    }
	}

	setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	
    }

    private void modifyPoint(IHttpResponse resp, int ind, int x, int y) {
	
	Point p = points.getPoint(ind);
	if (p == null) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	} else {
	    p.setX(x);
	    p.setY(y);
	    String body = "The point has been modified, new value :" + p.toString();
	    resp.setBody(body);
	    resp.setContentLength(body.length());
	}
	
    }

    @Override
    public void doPost(IHttpRequest req, IHttpResponse resp) {
	
	URL url = req.getUrl();
	String host = url.getHost();
	String path = url.getPath();

	if (!host.equalsIgnoreCase("p") || !path.isEmpty()) {
	    setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    return;
	}

	if (req.getBody().isEmpty()) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	String[] coords = req.getBody().split(";");

	if (coords.length < 2) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	String[] coordX = coords[0].split("=");
	String[] coordY = coords[1].split("=");

	try {
	    int x = Integer.parseInt(coordX[1]);
	    int y = Integer.parseInt(coordY[1]);
	    addPoint(resp, x, y);
	    return;
	} catch (NumberFormatException e) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

    }

    public void addPoint(IHttpResponse resp, int x, int y) {
	
	Point p = points.addPoint(x, y);
	String body = "new Point created : " + p.toString();
	resp.setBody(body);
	resp.setContentLength(body.length());
	
    }

    @Override
    public void doDelete(IHttpRequest req, IHttpResponse resp) {
	
	URL url = req.getUrl();
	String host = url.getHost();
	String path = url.getPath();

	if (!host.equalsIgnoreCase("p") || path.isEmpty()) {
	    setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	String[] splittedPath = path.split("/");
	if (splittedPath.length == 2) {
	    try {
		int ind = Integer.parseInt(splittedPath[1]);
		deletePoint(resp, ind);
		return;
	    } catch (NumberFormatException e) {
		setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
		return;
	    }
	}

	setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	
    }

    private void deletePoint(IHttpResponse resp, int ind) {
	
	String body = points.getPoints().toString() + " Removed";
	points.getPoints().remove(ind);
	resp.setBody(body);
	resp.setContentLength(body.length());
	
    }

    public String toString() {
	return points.toString();
    }

}
