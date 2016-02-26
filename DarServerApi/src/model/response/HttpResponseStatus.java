package model.response;

public enum HttpResponseStatus {
    OK("200"),
    Created("201"),
    Accepted("202"),
    Non_Authoritative_Information("203"),
    No_Content("204"),
    Reset_Content("205"),
    Partial_Content("206"),
    Multi_Status("207"),
    Already_Reported("208"),
    IM_Used("226"),
    Bad_Request("400"),
    Unauthorized("401"),
    Payment_Required("402"),
    Forbidden("403"),
    Not_Found("404"),
    Method_Not_Allowed("405"),
    Not_Acceptable("406"),
    Proxy_Authentication_Required("407"),
    Payload_Too_Large("413"),
    URI_Too_Long("414"),
    Locked("423"),
    Internal_Server_Error("500"),
    Not_Implemented("501"),
    HTTP_Version_Not_Supported("505");

    private String status;

    private HttpResponseStatus(String status) {
	this.status = status;
    }

    public String getStatus() {
	return status;
    }

    public static HttpResponseStatus getStatus(String code) {
	
	for (HttpResponseStatus status : HttpResponseStatus.values()) {
	    if (status.getStatus().equalsIgnoreCase(code))
		return status;
	}
	return null;
	
    }
}
