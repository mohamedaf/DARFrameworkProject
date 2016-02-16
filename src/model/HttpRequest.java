package model;

import java.util.HashMap;
import java.util.Map;

import model.header.HeaderField;

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

    public static HttpRequest parse(String httpRequest) {
	return null;
    }

    @Override
    public String toString() {
	if (method == null)
	    return "";

	String request = method.name() + " / http/1.1";

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

}
