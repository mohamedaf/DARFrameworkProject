package model;

import java.util.HashMap;
import java.util.Map;

import model.header.HeaderField;
import model.header.HeaderResponseField;

public class HttpResponse {

    private final HttpResponseStatus status;
    private final Map<HeaderField, String> headers;
    private final Map<String, String> cookies;
    private final String body;

    public HttpResponse(HttpResponseStatus status,
	    Map<HeaderField, String> headers, Map<String, String> cookies,
	    String body) {
	super();
	this.status = status;
	this.headers = (headers != null) ? headers
		: new HashMap<HeaderField, String>();
	this.cookies = (cookies != null) ? cookies
		: new HashMap<String, String>();
	this.body = body;
    }

    public HttpResponseStatus getStatus() {
	return status;
    }

    public String addHeaderValue(HeaderResponseField field, String value) {
	return headers.put(field, value);
    }

    public String getHeaderValue(HeaderResponseField field) {
	return headers.get(field);
    }

    public String getBody() {
	return body;
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

    @Override
    public String toString() {
	if (status == null) {
	    return "";
	}

	String response = "HTTP/1.0 " + status.getStatus() + " "
		+ status.name() + "\n";

	for (HeaderField field : headers.keySet()) {
	    response += field.getName() + ": " + headers.get(field) + "\n";
	}

	if (!cookies.isEmpty()) {
	    response += "Cookie:";
	}

	for (String name : cookies.keySet()) {
	    response += " " + name + "=" + cookies.get(name) + ";";
	}

	response += "\n\n";
	if (body != null)
	    response += body;

	return response;
    }

    public String getTextResponse() {
	return this.toString();
    }

    public String getHtmlResponse() {
	String htmlResponse = "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\""
		+ headers.get(HeaderResponseField.CONTENT_ENCODING)
		+ "\">\n"
		+ "<title>Http Server Response</title>\n</head>\n<body>\n";
	htmlResponse += this.toString().replaceAll("\n", "<br>");
	htmlResponse += "\n</body>\n</html>";
	return htmlResponse;
    }

    public String getJsonResponse() {
	return "{ 'response' : '" + this.toString() + "' }";
    }
}
