package model.response;


public interface IHttpResponse {
    
    public void setContentLength(int length);

    public void setStatus(HttpResponseStatus status);
    
    public HttpResponseStatus getStatus();

    public String addHeaderValue(HeaderResponseField field, String value);
    
    public String getHeaderValue(HeaderResponseField field);

    public void setBody(String body);
    
    public String getBody();

    public boolean haveCookie(String key);

    public String addCookie(String key, String value);

    public String getCookie(String key);
}
