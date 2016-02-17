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

	String response = "HTTP/1.1 "
		+ status.getStatus()
		+ " "
		+ status.name()
		+ "\nDate: Mon, 23 May 2005 22:38:34 GMT\nServer: Apache/1.3.3.7 "
		+ "(Unix) (Red-Hat/Linux)\nLast-Modified: Wed, 08 Jan 2003 23:11:55 "
		+ "GMT\nETag: \"3f80f-1b6-3e1cb03b\"\nContent-Type: text/html; "
		+ "charset=UTF-8\nContent-Length: " + body.length()
		+ "\nAccept-Ranges: bytes\n";

	for (HeaderField field : headers.keySet()) {
	    response += field.getName() + ": " + headers.get(field) + "\n";
	}

	if (!cookies.isEmpty()) {
	    response += "Cookie:";
	    for (String name : cookies.keySet()) {
		response += " " + name + "=" + cookies.get(name) + ";";
	    }
	    response += "\n";
	}

	response += "\n";

	if (body != null)
	    response += body;

	return response;
    }
}
