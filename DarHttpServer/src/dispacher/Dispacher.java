package dispacher;

import httpServlet.IHttpServlet;

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
import org.jdom.xpath.XPath;

public class Dispacher {

    private Document document = null;

    public Dispacher(Document document) {

	super();
	this.document = document;

    }

    @SuppressWarnings("rawtypes")
    public boolean isValidApplication(String name) {

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
	    System.out.println("Erreur JDOM " + e.getMessage());
	    e.printStackTrace();
	}

	return false;

    }

    @SuppressWarnings("rawtypes")
    public DispacherResult isValidPath(IHttpResponse resp,
	    HttpRequestMethod method, String path, Map<String, String> params) {

	Element rootNode = document.getRootElement();
	Element pathNode = null;
	String pathMethod = null;
	String pathValue = null;

	try {
	    XPath xpa = XPath.newInstance("//path");
	    Iterator iter = xpa.selectNodes(rootNode).iterator();

	    while (iter.hasNext()) {
		pathNode = (Element) iter.next();
		pathMethod = pathNode.getAttributeValue("method");
		pathValue = pathNode.getAttributeValue("value");

		if (pathMethod.equalsIgnoreCase(method.name()) && path.matches(pathValue)) {
		    return checkPathNode(resp, pathNode, params);
		}
	    }

	} catch (JDOMException e) {
	    System.out.println("Erreur JDOM " + e.getMessage());
	    e.printStackTrace();
	}

	HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	return null;

    }

    private DispacherResult checkPathNode(IHttpResponse resp, Element pathNode,
	    Map<String, String> params) {

	if (checkQueryString(pathNode, params)) {
	    String controllerName = pathNode.getParentElement().getAttributeValue("name");
	    IHttpServlet servlet = getController(controllerName);
	    String call = pathNode.getAttributeValue("call");
	    return new DispacherResult(servlet, call);
	} else {
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Bad_Request);
	    return null;
	}

    }

    @SuppressWarnings("unchecked")
    private boolean checkQueryString(Element pathNode, Map<String, String> params) {

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

	try {
	    switch (type.toLowerCase()) {
	    case "int":
		Integer.parseInt(value);
		return true;
	    case "double":
		Double.parseDouble(value);
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
	case "pointController":
	    return ControllerFactory.getPointController();
	default:
	    return null;
	}

    }

}
