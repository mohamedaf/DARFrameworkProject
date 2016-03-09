package model.response;

import java.util.List;


public interface IHttpResponse {
    
    public void setContentLength(int length);

    public void setStatus(HttpResponseStatus status);
    
    public HttpResponseStatus getStatus();

    public String addHeaderValue(HeaderResponseField field, String value);
    
    public String getHeaderValue(HeaderResponseField field);
    
    public boolean haveCookie(String key);

    public void addCookie(String key, String value);

    public String getCookie(String key);

    public void setBody(String body);
    
    public String getBody();
    
    public void addStringViewAttribute(String attribute, String value);
    
    public void addListViewAttribute(String attribute, List<String> values);
    
    public String setViewContent(String filePath);
}
