package httpServlet;

import model.request.IHttpRequest;
import model.response.IHttpResponse;

public interface IHttpServlet {
   
    public void doGet(IHttpRequest req, IHttpResponse resp, String call);
    
    public void doPut(IHttpRequest req, IHttpResponse resp, String call);
    
    public void doPost(IHttpRequest req, IHttpResponse resp, String call);
    
    public void doDelete(IHttpRequest req, IHttpResponse resp, String call);
    
}
