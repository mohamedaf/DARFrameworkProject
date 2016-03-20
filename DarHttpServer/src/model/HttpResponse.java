package model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.request.IHttpRequest;
import model.response.HeaderResponseField;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse implements IHttpResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private HttpResponseStatus status;
    private final ViewProvider viewProvider;
    private final Map<HeaderField, String> headers;
    private final Map<String, String> cookies;
    private String body;

    public HttpResponse(HttpResponseStatus status, String contentType, String body) {

	super();
	this.status = status;
	this.viewProvider = new ViewProvider();
	this.headers = new HashMap<HeaderField, String>();
	this.cookies = new HashMap<String, String>();
	this.body = body;
	initHeaders(contentType);

    }

    public HttpResponse(HttpResponseStatus status, IHttpRequest request) {

	super();
	this.status = status;
	this.viewProvider = new ViewProvider();
	this.headers = new HashMap<HeaderField, String>();
	this.cookies = (request.getCookies() != null) ? request.getCookies() : new HashMap<String, String>();
	this.body = new String();
	initHeaders(request.getContentType());

    }

    private void initHeaders(String contentType) {
	LOGGER.info("Initiating first http response headers");
	
	Date date = new Date();
	SimpleDateFormat form = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	headers.put(HeaderResponseField.DATE, form.format(date));
	headers.put(HeaderResponseField.SERVER, "Macbook de Mohamed Amin");
	headers.put(HeaderResponseField.CONTENT_ENCODING, "UTF-8");
	headers.put(HeaderResponseField.CONTENT_LENGTH, String.valueOf(body.length()));
	headers.put(HeaderResponseField.CONTENT_TYPE, contentType);

    }

    public void setContentLength(int length) {
	headers.put(HeaderResponseField.CONTENT_LENGTH, String.valueOf(length));
    }

    public void setStatus(HttpResponseStatus status) {
	this.status = status;
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

    public boolean haveCookie(String key) {
	return cookies.containsKey(key);
    }

    public void addCookie(String key, String value) {
	try {
	    String safeCookieName = URLEncoder.encode(key, "UTF-8");
	    String safeCookieValue = URLEncoder.encode(value, "UTF-8");
	    cookies.put(safeCookieName, safeCookieValue);
	} catch (UnsupportedEncodingException e) {
	    LOGGER.error("Error while adding new cookie {}", e);
	}
    }

    public String getCookie(String key) {
	return cookies.get(key);
    }

    public void setBody(String body) {

	this.body = body;
	if (body != null)
	    setContentLength(body.length());
	
    }

    public String getBody() {
	return body;
    }
    
    public void addStringViewAttribute(String attribute, String value) {
	viewProvider.addStringAttribute(attribute, value);
    }
    
    public void addListViewAttribute(String attribute, List<String> values) {
	viewProvider.addListAttribute(attribute, values);
    }
    
    public String setViewContent(String filePath, String appName) {
	
	String b = viewProvider.getViewContent(filePath, appName);
	this.setBody(b);
	return b;
	
    }

    @Override
    public String toString() {

	if (status == null) {
	    return new String();
	}

	StringBuilder response = new StringBuilder("HTTP/1.1 "
		+ status.getStatus() + " " + status.name() + "\n");

	for (HeaderField field : headers.keySet()) {
	    response.append(field.getName() + ": " + headers.get(field) + "\n");
	}

	if (!cookies.isEmpty()) {
	    for (String name : cookies.keySet()) {
		response.append("Set-Cookie: " + name + "=" + cookies.get(name) + "\n");
	    }
	}
	response.append("\n");

	if (body != null)
	    response.append(body);

	return response.toString();

    }
}
