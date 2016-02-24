package httpServlet;

import model.request.IHttpRequest;
import model.response.IHttpResponse;

public interface IHttpServlet {
   
    public void doGet(IHttpRequest req, IHttpResponse resp);
    
    public void doPut(IHttpRequest req, IHttpResponse resp);
    
    public void doPost(IHttpRequest req, IHttpResponse resp);
    
    public void doDelete(IHttpRequest req, IHttpResponse resp);
    
}
