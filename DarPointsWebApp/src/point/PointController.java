package point;

import httpServlet.IHttpServlet;

import java.net.URL;
import java.util.Map;

import model.request.IHttpRequest;
import model.response.HeaderResponseField;
import model.response.HttpResponseError;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointController implements IHttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(PointController.class);
    private Points points = new Points();

    @Override
    public void doGet(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "getpointlist":
	    getPointList(resp);
	    break;
	case "getpoint":
	    getPoint(req, resp);
	    break;
	case "getx":
	    getPointCoord(req, resp, "x");
	    break;
	case "gety":
	    getPointCoord(req, resp, "y");
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    private void getPointList(IHttpResponse resp) {
	LOGGER.info("GET getPointList");
	String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	resp.setBody(toString(contentType, contentEncoding));
	LOGGER.info(toString(contentType, contentEncoding));
    }

    private void getPoint(IHttpRequest req, IHttpResponse resp) {
	LOGGER.info("GET getPoint");
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	
	if (p == null) {
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	} else {
	    String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	    resp.setBody(p.toString(contentType));
	    LOGGER.info(p.toString(contentType));
	}
	
    }

    private void getPointCoord(IHttpRequest req, IHttpResponse resp, String coord) {
	LOGGER.info("GET getPointCoord {}", coord);
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	if (coord.equalsIgnoreCase("x")) {
	    String body = "x = " + p.getX();
	    resp.setBody(body);
	    LOGGER.info(body);
	} else if (coord.equalsIgnoreCase("y")) {
	    String body = "y = " + p.getY();
	    resp.setBody(body);
	    LOGGER.info(body);
	} else {
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    @Override
    public void doPut(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "modifypoint":
	    modifyPoint(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    private void modifyPoint(IHttpRequest req, IHttpResponse resp) {
	LOGGER.info("PUT modify point");
	
	int ind = getInd(req);
	Point p = points.getPoint(ind);
	
	if (p == null) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	} else {
	    Map<String, String> params = req.getParams();
	    p.setX(Integer.parseInt(params.get("x")));
	    p.setY(Integer.parseInt(params.get("y")));
	    String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	    String body = "This point has been modified, new value :" + p.toString(contentType);
	    resp.setBody(body);
	    LOGGER.info(body);
	}
	
    }

    @Override
    public void doPost(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "addpoint":
	    addPoint(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}

    }

    public void addPoint(IHttpRequest req, IHttpResponse resp) {
	LOGGER.info("POST addPoint");
	
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
	    String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	    String body = "new Point created : " + p.toString(contentType);
	    resp.setBody(body);
	    LOGGER.info(body);
	} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	}
	
    }

    @Override
    public void doDelete(IHttpRequest req, IHttpResponse resp, String call) {
	
	switch (call.toLowerCase()) {
	case "deletepoint":
	    deletePoint(req, resp);
	    break;
	default:
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	}
	
    }

    private void deletePoint(IHttpRequest req, IHttpResponse resp) {
	LOGGER.info("DELETE deletePoint");
	
	int ind = getInd(req);
	Point p = points.getPoints().get(ind);
	String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	String body = "Point : " + p.toString(contentType) + " was removed";
	points.getPoints().remove(p);
	resp.setBody(body);
	LOGGER.info(body);
	
    }
    
    private int getInd(IHttpRequest req) {
	
	URL url = req.getUrl();
	String path = url.getPath();
	String[] splittedPath = path.split("/");
	return Integer.parseInt(splittedPath[1]);
	
    }
    
    public String toString(String contentType, String contentEncoding) {
	return points.toString(contentType, contentEncoding);
    }

}
