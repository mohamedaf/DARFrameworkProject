package dispatcher;

import httpServlet.IHttpServlet;

public class DispatcherResult {

    private final IHttpServlet servlet;
    private final String call;

    public DispatcherResult(IHttpServlet servlet, String call) {
	
	super();
	this.servlet = servlet;
	this.call = call;
	
    }

    public IHttpServlet getServlet() {
	return servlet;
    }

    public String getCall() {
	return call;
    }

}
