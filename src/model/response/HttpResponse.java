package model.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.HeaderField;
import model.request.HttpRequest;

public class HttpResponse {

    private final HttpResponseStatus status;
    private final Map<HeaderField, String> headers;
    private final Map<String, String> cookies;
    private final String body;

    public HttpResponse(HttpResponseStatus status, HttpRequest request) {
	super();
	this.status = status;
	this.headers = (request.getHeaders() != null) ? request.getHeaders()
		: new HashMap<HeaderField, String>();
	this.cookies = (request.getCookies() != null) ? request.getCookies()
		: new HashMap<String, String>();
	this.body = request.toString();
	initHeaders(request.getContentType());
    }
    
    private void initHeaders(String contentType){
	Date date = new Date( );
	SimpleDateFormat form = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	headers.put(HeaderResponseField.DATE, form.format(date));
	headers.put(HeaderResponseField.SERVER, "Apache/1.3.3.7 (Unix) (Red-Hat/Linux)");
	headers.put(HeaderResponseField.CONTENT_ENCODING, "UTF-8");
	headers.put(HeaderResponseField.CONTENT_LENGTH, String.valueOf(body.length()));
	headers.put(HeaderResponseField.CONTENT_TYPE, contentType);
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
	    return new String();
	}

	StringBuilder response = new StringBuilder("HTTP/1.1 " + status.getStatus()
		+ " " + status.name() + "\n");

	for (HeaderField field : headers.keySet()) {
	    response.append(field.getName() + ": " + headers.get(field) + "\n");
	}

	if (!cookies.isEmpty()) {
	    response.append("Cookie:");
	    for (String name : cookies.keySet()) {
		response.append(" " + name + "=" + cookies.get(name) + ";");
	    }
	    response.append("\n");
	}

	response.append("\n");

	if (body != null)
	    response.append(body);

	return response.toString();
    }
}
