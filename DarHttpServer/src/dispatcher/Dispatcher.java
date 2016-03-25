package dispatcher;

import httpServlet.IHttpServlet;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.request.HttpRequestMethod;
import model.response.HttpResponseError;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher {

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(Dispatcher.class);
    private Document document = null;

    public Dispatcher(Document document) {

	super();
	this.document = document;

    }

    @SuppressWarnings("rawtypes")
    public boolean isValidApplication(String name) {
	LOGGER.info("Verifying if the host correspond to an valid application");

	Element rootNode = document.getRootElement();
	Element currentNode = null;
	String applicationName = null;

	try {
	    XPath xpa = XPath.newInstance("//application");
	    Iterator iter = xpa.selectNodes(rootNode).iterator();

	    while (iter.hasNext()) {
		currentNode = (Element) iter.next();
		applicationName = currentNode.getAttributeValue("name");
		if (applicationName != null && applicationName.equalsIgnoreCase(name))
		    return true;
	    }
	} catch (JDOMException e) {
	    LOGGER.error("Erreur JDOM ", e);
	}
	return false;

    }

    @SuppressWarnings("rawtypes")
    public DispatcherResult isValidPath(IHttpResponse resp,
	    HttpRequestMethod method, String appName, String path,
	    Map<String, String> params) throws JDOMException, IOException {
	LOGGER.info("Verifying if the path is valid");

	URL url = new URL("jar:file:" + System.getProperty("user.dir") + "/apps/" + appName + ".jar!/dispatcher.xml");
	SAXBuilder sxb = new SAXBuilder();
	Document appDocument = sxb.build(url.openStream());
	XPath xpa = XPath.newInstance("//path");

	Element rootNode = appDocument.getRootElement();
	Element pathNode = null;
	String pathMethod = null;
	String pathValue = null;

	Iterator iter = xpa.selectNodes(rootNode).iterator();

	while (iter.hasNext()) {
	    pathNode = (Element) iter.next();
	    pathMethod = pathNode.getAttributeValue("method");
	    pathValue = pathNode.getAttributeValue("value");

	    if (pathMethod.equalsIgnoreCase(method.name()) && path.toLowerCase().matches(pathValue)) {
		return checkPathNode(resp, pathNode, params);
	    }
	}

	LOGGER.warn("Http Not found");
	HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	return null;

    }

    private DispatcherResult checkPathNode(IHttpResponse resp,
	    Element pathNode, Map<String, String> params) {
	LOGGER.info("Checking path node");
	
	String call = pathNode.getAttributeValue("call");
	if(call.matches(".+\\.js")) {
	    return new DispatcherResult(null, call);
	}
	if(call.matches(".+\\.css")) {
	    return new DispatcherResult(null, call);
	}

	if (checkQueryString(pathNode, params)) {
	    String controllerName = pathNode.getParentElement().getAttributeValue("name");
	    IHttpServlet servlet = getController(controllerName);
	    if (servlet == null) {
		LOGGER.warn("Http Internal Server Error");
		HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Internal_Server_Error);
		return null;
	    }
	    return new DispatcherResult(servlet, call);
	} else {
	    LOGGER.warn("Http Bad request");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return null;
	}

    }

    @SuppressWarnings("unchecked")
    private boolean checkQueryString(Element pathNode, Map<String, String> params) {
	LOGGER.info("Checking query string");

	Element paramsNode = pathNode.getChild("params");
	if (paramsNode == null)
	    return true;

	Boolean required;
	String name, type, value;
	List<Element> paramNode = paramsNode.getChildren();

	for (Element e : paramNode) {
	    type = e.getAttributeValue("type");
	    name = e.getAttributeValue("name");
	    required = Boolean.parseBoolean(e.getAttributeValue("required"));

	    if (params.containsKey(name) && params.get(name) != null) {
		value = params.get(name);
		if (checkType(type, value))
		    return true;
	    } else if (!required) {
		continue;
	    } else {
		return false;
	    }
	}

	return true;

    }

    private boolean checkType(String type, String value) {
	LOGGER.info("Checking param type");

	try {
	    switch (type.toLowerCase()) {
	    case "int":
		Integer.parseInt(value);
		return true;
	    case "double":
		Double.parseDouble(value);
		return true;
	    case "boolean":
		Boolean.parseBoolean(value);
		return true;
	    case "string":
		return true;
	    default:
		return false;
	    }
	} catch (NumberFormatException e) {
	    return false;
	}

    }

    private IHttpServlet getController(String name) {

	if (name == null)
	    return null;

	switch (name) {
	case "PointController":
	    LOGGER.info("Getting PointController");
	    return ControllerFactory.getPointController();
	case "GoodDealController":
	    LOGGER.info("Getting GoodDealController");
	    return ControllerFactory.getGoodDealController();
	default:
	    LOGGER.warn("No corresponding controller found");
	    return null;
	}

    }

}
