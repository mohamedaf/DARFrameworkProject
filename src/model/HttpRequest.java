package model;

import java.util.HashMap;
import java.util.Map;

import model.header.HeaderField;
import model.header.HeaderRequestField;
import model.header.HeaderResponseField;

public class HttpRequest {

    private final HttpRequestMethod method;
    private final Map<HeaderField, String> headers;
    private final Map<String, String> cookies;
    private final String body;

    public HttpRequest(HttpRequestMethod method,
	    Map<HeaderField, String> headers, Map<String, String> cookies,
	    String body) {
	super();
	this.method = method;
	this.headers = (headers != null) ? headers
		: new HashMap<HeaderField, String>();
	this.cookies = (cookies != null) ? cookies
		: new HashMap<String, String>();
	this.body = body;
    }

    public Map<HeaderField, String> getHeaders() {
	return headers;
    }

    public Map<String, String> getCookies() {
	return cookies;
    }

    public HttpRequestMethod getMethod() {
	return method;
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

    public String getBody() {
	return body;
    }

    public static HttpRequest parse(String httpRequest) throws Exception {
	HttpRequestMethod method;
	Map<HeaderField, String> headers = new HashMap<>();
	Map<String, String> cookies = new HashMap<>();
	String body = "";
	boolean reachedBody = false;

	String[] lines = httpRequest.split("\n");
	if (lines.length < 1) {
	    throw new Exception("Error ! Request can't be empty");
	}
	method = HttpRequestMethod.valueOf(lines[0].split(" ")[0]);

	HeaderField key;
	String value;
	String[] splitL, cookiePair;
	for (int i = 1; i < lines.length; i++) {
	    if (!reachedBody) {
		if (lines[i].length() > 0) {
		    splitL = lines[i].split(": ");
		    key = HeaderRequestField.getField(splitL[0]);
		    value = splitL[1];
		    if (key == HeaderRequestField.COOKIE) {
			for (String s : value.split(";")) {
			    cookiePair = s.split(":");
			    cookies.put(cookiePair[0], cookiePair[1]);
			}
		    } else if (key != null) {
			headers.put(key, value);
		    }
		} else {
		    reachedBody = true;
		}
	    } else {
		body += lines[i];
	    }
	}

	return new HttpRequest(method, headers, cookies, body);
    }

    @Override
    public String toString() {
	if (method == null)
	    return "";

	String request = method.name() + " / HTTP/1.1\n";

	for (HeaderField field : headers.keySet()) {
	    request += field.getName() + ": " + headers.get(field) + "\n";
	}

	if (!cookies.isEmpty()) {
	    request += "Cookie:";
	}

	for (String name : cookies.keySet()) {
	    request += " " + name + "=" + cookies.get(name) + ";";
	}

	request += "\n\n";
	if (body != null)
	    request += body;

	return request;
    }

    public String getTextResponse() {
	return this.toString();
    }

    public String getHtmlResponse() {
	String htmlRequest = "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\""
		+ headers.get(HeaderResponseField.CONTENT_ENCODING)
		+ "\">\n"
		+ "<title>Http Server Response</title>\n</head>\n<body>\n";
	htmlRequest += method.name() + " / HTTP/1.1\n";

	if (method != null) {
	    htmlRequest += "<table style=\"width:100%\">";

	    for (HeaderField field : headers.keySet()) {
		if (field.equals(HeaderResponseField.CONTENT_TYPE))
		    htmlRequest += "<tr><td>" + field.getName()
			    + ":</td><td>text/html</td></tr>\n";
		else
		    htmlRequest += "<tr><td>" + field.getName() + ":</td><td>"
			    + headers.get(field) + "</td></tr>\n";
	    }

	    if (!cookies.isEmpty()) {
		htmlRequest += "<tr><td>Cookie:</td></tr>";
	    }

	    for (String name : cookies.keySet()) {
		htmlRequest += "<tr><td>" + name + "=</td><td>"
			+ cookies.get(name) + "</td></tr>";
	    }

	    htmlRequest += "<tr><td>Body</td>";
	    if (body != null)
		htmlRequest += "<td>" + body + "</td></tr>";

	    htmlRequest += "</table>\n";
	}

	htmlRequest += "</body>\n</html>";
	return htmlRequest;
    }

    public String getJsonResponse() {
	String request = "{ 'response' : '" + method.name() + " / http/1.1',\n";

	if (method != null) {
	    for (HeaderField field : headers.keySet()) {
		if (field.equals(HeaderResponseField.CONTENT_TYPE))
		    request += "'" + field.getName() + "' : 'text/json',\n";
		else
		    request += "'" + field.getName() + "' : '"
			    + headers.get(field) + "',\n";
	    }

	    if (!cookies.isEmpty()) {
		request += "'Cookie' :";
	    }

	    for (String name : cookies.keySet()) {
		request += " '" + name + "=" + cookies.get(name) + "',\n";
	    }

	    if (body != null)
		request += "'body' : '" + body + "'";

	}

	request += "}";
	return request;
    }

}
