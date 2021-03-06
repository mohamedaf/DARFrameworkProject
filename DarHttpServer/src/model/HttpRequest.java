package model;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import model.request.HeaderRequestField;
import model.request.HttpRequestMethod;
import model.request.IHttpRequest;
import model.response.HeaderResponseField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest implements IHttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);
    private final HttpRequestMethod method;
    private final URL url;
    private final Map<String, String> params;
    private final Map<HeaderField, String> headers;
    private final Map<String, String> cookies;
    private final HttpSessionProvider sessionProvider;
    private final String body;
    private HttpSession session;

    private HttpRequest(HttpRequestMethod method, URL url,
	    Map<String, String> params, Map<HeaderField, String> headers,
	    Map<String, String> cookies, HttpSessionProvider sessionProvider, String body) {

	super();
	this.method = method;
	this.url = url;
	this.params = (params != null) ? params : new HashMap<String, String>();
	this.headers = (headers != null) ? headers : new HashMap<HeaderField, String>();
	this.cookies = (cookies != null) ? cookies : new HashMap<String, String>();
	this.sessionProvider = sessionProvider;
	this.body = body;
	this.session = null;

    }

    public HttpRequestMethod getMethod() {
	return method;
    }

    public URL getUrl() {
	return url;
    }

    public Map<String, String> getParams() {
	return params;
    }

    public Map<HeaderField, String> getHeaders() {
	return headers;
    }

    public Map<String, String> getCookies() {
	return cookies;
    }

    public String addHeaderValue(HeaderField field, String value) {
	return headers.put(field, value);
    }

    public String getHeaderValue(HeaderField field) {
	return headers.get(field);
    }

    public boolean haveCookie(String key) {
	return cookies.containsKey(key);
    }

    public String addCookie(String key, String value) {
	return cookies.put(key, value);
    }

    public String getCookie(String key) {
	return cookies.get(key);
    }

    public HttpSessionProvider getSessionProvider() {
        return sessionProvider;
    }

    public String getBody() {
	return body;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
    
    public HttpSession getSession() {
        return session;
    }

    public static HttpRequest parse(String httpRequest, HttpSessionProvider sessionProvider)
	    throws HttpRequestParseException, MalformedURLException, UnsupportedEncodingException {
	LOGGER.info("HTTP request parsing");
	
	Map<HeaderField, String> headers = new HashMap<HeaderField, String>();
	Map<String, String> cookies = new HashMap<String, String>();
	StringBuilder body = new StringBuilder();
	int i = 1;

	String[] lines = httpRequest.split("\n");
	if (lines.length < 1) {
	    LOGGER.warn("Error ! Request can't be empty");
	    throw new HttpRequestParseException("Error ! Request can't be empty");
	}

	String[] firstLine = lines[0].split(" ");

	if (firstLine.length < 3) {
	    LOGGER.warn("Error ! Request first line is not correct");
	    throw new HttpRequestParseException("Error ! Request first line is not correct");
	}

	HttpRequestMethod method = HttpRequestMethod.valueOf(firstLine[0]);
	URL url = new URL("http:/" + firstLine[1]);

	LOGGER.info("Parsing header ");
	while (i < lines.length) {
	    if (lines[i].length() > 0) {
		parseHeader(lines, cookies, headers, i);
	    } else {
		break;
	    }
	    i++;
	}

	while (i < lines.length) {
	    body = body.append(lines[i]);
	    i++;
	}

	Map<String, String> params;
	if(method.equals(HttpRequestMethod.POST)) {
	    params = parseQueryString(body.toString());
	} else {
	    params = splitQuery(url);
	}
	return new HttpRequest(method, url, params, headers, cookies, 
		sessionProvider, body.toString());

    }

    private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
	LOGGER.info("Splitting http request query");
	
	Map<String, String> query_pairs = new HashMap<String, String>();

	if (url == null || url.getQuery() == null)
	    return query_pairs;
	
	return parseQueryString(url.getQuery());

    }
    
    private static Map<String, String> parseQueryString(String query) throws UnsupportedEncodingException {
	
	Map<String, String> query_pairs = new HashMap<String, String>();
	String[] pairs = query.split("&");
	for (String pair : pairs) {
	    int idx = pair.indexOf("=");
	    try {
		query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
				URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    } catch (StringIndexOutOfBoundsException e) {
		LOGGER.warn("Query string format is not good");
		continue;
	    }
	}

	return query_pairs;
	
    }

    private static void parseHeader(String[] lines, Map<String, String> cookies,
	    Map<HeaderField, String> headers, int i) {
	
	String[] splitL = lines[i].split(": ");
	HeaderField key = HeaderRequestField.getField(splitL[0]);
	String value = splitL[1];

	if (key == HeaderRequestField.COOKIE) {
	    for (String s : value.split(";")) {
		String[] cookiePair = s.split("=");
		cookies.put(cookiePair[0], cookiePair[1]);
	    }
	} else if (key != null) {
	    headers.put(key, value);
	} else if (!headers.containsKey(HeaderRequestField.UNKNOWN)) {
	    headers.put(HeaderRequestField.UNKNOWN, value);
	} else {
	    value = headers.get(HeaderRequestField.UNKNOWN) + " ; " + value;
	    headers.put(HeaderRequestField.UNKNOWN, value);
	}

    }
    
    public String getContentType() {

	String contentType = headers.get(HeaderRequestField.ACCEPT);
	if (contentType != null
		&& (contentType.startsWith("text/plain") || 
			contentType.startsWith("application/json"))) {
	    return headers.get(HeaderRequestField.ACCEPT);
	}
	return "text/html";

    }

    @Override
    public String toString() {

	if (headers.containsKey(HeaderRequestField.ACCEPT)) {
	    if (getContentType().equals("text/plain")) {
		LOGGER.info("building text/plain request echo");
		return this.getTextResponse();
	    }
	    if (getContentType().equals("application/json")) {
		LOGGER.info("building application/json request echo");
		return this.getJsonResponse();
	    }
	}
	LOGGER.info("building text/html request echo");
	return this.getHtmlResponse();

    }

    public String getTextResponse() {

	if (method == null)
	    return new String();

	StringBuilder textRequest = new StringBuilder(method.name()
		+ " / HTTP/1.1\n");

	for (HeaderField field : headers.keySet()) {
	    textRequest.append(field.getName() + ": " + headers.get(field) + "\n");
	}

	if (!cookies.isEmpty()) {
	    textRequest.append("Cookie:");
	}

	for (String name : cookies.keySet()) {
	    textRequest.append(" " + name + "=" + cookies.get(name) + ";");
	}
	textRequest = textRequest.delete(textRequest.length()-1, textRequest.length());

	textRequest.append("\n\n");
	if (body != null)
	    textRequest.append(body);

	return textRequest.toString();

    }

    public String getHtmlResponse() {

	StringBuilder htmlRequest = new StringBuilder(
		"<!DOCTYPE html>\n<html>\n"
			+ "<head> \n<meta charset=\""
			+ headers.get(HeaderResponseField.CONTENT_ENCODING)
			+ "\">\n"
			+ "<title>Http Server Response</title>\n</head>\n<body>\n");

	if (method != null) {
	    htmlRequest
		    .append("<table rules=\"all\" style=\"width:100%; "
		    	    	+ "border:solid 1px black;\">"
		    	    	+ "<caption>"
		    	    	+ method.name()
		    	    	+ " / HTTP/1.1</caption>\n");

	    for (HeaderField field : headers.keySet()) {
		if (field.equals(HeaderResponseField.CONTENT_TYPE))
		    htmlRequest.append("<tr><td>" + field.getName()
			    + ":</td><td>text/html</td></tr>\n");
		else
		    htmlRequest.append("<tr><td>" + field.getName()
			    + ":</td><td>" + headers.get(field)
			    + "</td></tr>\n");
	    }

	    if (!cookies.isEmpty()) {
		htmlRequest.append("<tr><td>Cookie:</td></tr>");
	    }

	    for (String name : cookies.keySet()) {
		htmlRequest.append("<tr><td>" + name + "=</td><td>"
			+ cookies.get(name) + "</td></tr>");
	    }

	    htmlRequest.append("<tr><td>Body</td>");
	    if (body != null)
		htmlRequest.append("<td>" + body + "</td></tr>");

	    htmlRequest.append("</table>\n");
	}

	htmlRequest.append("</body>\n</html>");
	return htmlRequest.toString();

    }

    public String getJsonResponse() {

	StringBuilder jsonRequest = new StringBuilder("{");
	if (method != null) {
	    jsonRequest.append("\"response\" : \"" + method.name()
		    + " / HTTP/1.1\",\n");
	    for (HeaderField field : headers.keySet()) {
		jsonRequest.append("\"" + field.getName() + "\" : \""
			+ headers.get(field) + "\",\n");
	    }

	    if (!cookies.isEmpty()) {
		jsonRequest.append("\"Cookie\" :");
	    }

	    for (String name : cookies.keySet()) {
		jsonRequest.append(" \"" + name + "=" + cookies.get(name) + "\",\n");
	    }

	    if (body != null)
		jsonRequest.append("\"body\" : \"" + body + "\"");
	}
	jsonRequest.append("}");
	return jsonRequest.toString();

    }

}
