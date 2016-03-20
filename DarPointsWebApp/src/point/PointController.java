package point;

import httpServlet.IHttpServlet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
	    getPointList(resp, req.getUrl().getHost());
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

    private void getPointList(IHttpResponse resp, String appName) {

	LOGGER.info("GET getPointList");
	String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	if(contentType.startsWith("text/html")) {
	    resp.addStringViewAttribute("contentEncoding", contentEncoding);
	    resp.addStringViewAttribute("text", "Liste de points:");
	    Map<Integer, Point> mapPoints = points.getPoints();
	    List<String> listPoints = new ArrayList<String>();
	    for(Integer id : mapPoints.keySet()) {
		listPoints.add(mapPoints.get(id).toString("text/html"));
	    }
	    resp.addListViewAttribute("list", listPoints);
	    LOGGER.info(resp.setViewContent("view/listePoint.jspr", appName));
	    return;
	}
	resp.setBody(toString(contentType, contentEncoding));
	LOGGER.info(toString(contentType, contentEncoding));

    }

    private void getPoint(IHttpRequest req, IHttpResponse resp) {

	LOGGER.info("GET getPoint");
	int ind = getInd(req);
	Point p = points.getPoint(ind);

	if (p == null) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	} else {
	    String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	    if(contentType.startsWith("text/html")) {
		String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
		callPointView(resp, contentEncoding, req.getUrl().getHost(), "Point:", p);
		return;
	    }
	    resp.setBody(p.toString(contentType));
	    LOGGER.info(p.toString(contentType));
	}

    }

    private void getPointCoord(IHttpRequest req, IHttpResponse resp, String coord) {
	LOGGER.info("GET getPointCoord {}", coord);

	int ind = getInd(req);
	Point p = points.getPoint(ind);
	String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);

	if (p == null && (coord.equalsIgnoreCase("x") || coord.equalsIgnoreCase("y"))) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	if(p == null) {
	    LOGGER.info("Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    return;
	}

	if (coord.equalsIgnoreCase("x")) {
	    if(contentType.startsWith("text/html")) {
		callPointView(resp, contentEncoding, req.getUrl().getHost(), "x =", p);
		return;
	    }
	    String body = "x = " + p.getX();
	    resp.setBody(body);
	    LOGGER.info(body);
	} else if (coord.equalsIgnoreCase("y")) {
	    if(contentType.startsWith("text/html")) {
		callPointView(resp, contentEncoding, req.getUrl().getHost(), "y =", p);
		return;
	    }
	    String body = "y = " + p.getY();
	    resp.setBody(body);
	    LOGGER.info(body);
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
	String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);

	if (p == null) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	Map<String, String> params = req.getParams();
	p.setX(Integer.parseInt(params.get("x")));
	p.setY(Integer.parseInt(params.get("y")));
	if(contentType.startsWith("text/html")) {
	    String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	    callPointView(resp, contentEncoding, req.getUrl().getHost(),
		    "This point has been modified, new value :", p);
	    return;
	}
	String body = "This point has been modified, new value : " + p.toString(contentType);
	resp.setBody(body);
	LOGGER.info(body);

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
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	Map<String, String> reqParams = req.getParams();
	String coordX = reqParams.get("x");
	String coordY = reqParams.get("y");

	if (coordX == null || coordY == null) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	try {
	    int x = Integer.parseInt(coordX);
	    int y = Integer.parseInt(coordY);
	    Point p = points.addPoint(x, y);
	    String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	    if(contentType.startsWith("text/html")) {
		String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
		callPointView(resp, contentEncoding, req.getUrl().getHost(), "new Point created :", p);
		return;
	    }
	    String body = "new Point created : " + p.toString(contentType);
	    resp.setBody(body);
	    LOGGER.info(body);
	} catch (NumberFormatException e) {
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

	if (p == null) {
	    LOGGER.info("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return;
	}

	String contentType = resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE);
	if(contentType.startsWith("text/html")) {
	    String contentEncoding = resp.getHeaderValue(HeaderResponseField.CONTENT_ENCODING);
	    callPointView(resp, contentEncoding, req.getUrl().getHost(), "Removed point :", p);
	    return;
	}
	String body = "Removed point : " + p.toString(contentType);
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

    private void callPointView(IHttpResponse resp, String contentEncoding,
	    String appName, String text, Point p){
	resp.addStringViewAttribute("contentEncoding", contentEncoding);
	resp.addStringViewAttribute("text", text);
	resp.addStringViewAttribute("point", p.toString("text/html"));
	LOGGER.info(resp.setViewContent("view/point.jspr", appName));
    }

    public String toString(String contentType, String contentEncoding) {
	return points.toString(contentType, contentEncoding);
    }

}
