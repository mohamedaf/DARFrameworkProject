package model.response;

public class HttpResponseError {
    
    public static void setHttpResponseError(IHttpResponse resp, HttpResponseStatus status) {

	resp.setStatus(status);
	if(resp.getHeaderValue(HeaderResponseField.CONTENT_TYPE).startsWith("text/html")) {
	    String response = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/>"
	    	+ "<title>Http Error</title></head><body><span style=\"color:red;\">"
	    	+ "Error " + status.getStatus() + " " + status.name() + "</span></body></html>";
	    resp.setBody(response);
	}
	else {
	    resp.setBody("Error " + status.getStatus() + " " + status.name());
	}

    }

}
