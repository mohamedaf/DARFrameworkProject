package model;

import java.util.Map;

import model.header.HeaderField;

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
	this.headers = headers;
	this.cookies = cookies;
	this.body = body;
    }

    public HttpResponseStatus getStatus() {
	return status;
    }

    public String addHeaderValue(HeaderField field, String value) {
	return headers.put(field, value);
    }

    public String getHeaderValue(HeaderField field) {
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

}
