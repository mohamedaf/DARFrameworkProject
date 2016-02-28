package dispacher;

import httpServlet.IHttpServlet;

public class DispacherResult {

    private final IHttpServlet servlet;
    private final String call;

    public DispacherResult(IHttpServlet servlet, String call) {
	
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
