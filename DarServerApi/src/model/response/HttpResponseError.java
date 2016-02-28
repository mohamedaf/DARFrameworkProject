package model.response;

public class HttpResponseError {
    
    public static void setHttpResponseError(IHttpResponse resp, HttpResponseStatus status) {

	resp.setStatus(status);
	resp.setBody("Error " + status.getStatus() + " " + status.name());

    }

}
