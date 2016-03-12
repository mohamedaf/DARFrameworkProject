package model.request;

import java.net.URL;
import java.util.Map;

import model.HeaderField;
import model.HttpSession;

public interface IHttpRequest {

    public HttpRequestMethod getMethod();

    public URL getUrl();

    public Map<String, String> getParams();

    public Map<HeaderField, String> getHeaders();

    public Map<String, String> getCookies();

    public String addHeaderValue(HeaderField field, String value);

    public String getHeaderValue(HeaderField field);

    public boolean haveCookie(String key);

    public String addCookie(String key, String value);

    public String getCookie(String key);

    public String getBody();
    
    public HttpSession getSession();

    public String getContentType();

    public String getTextResponse();

    public String getHtmlResponse();

    public String getJsonResponse();

}
