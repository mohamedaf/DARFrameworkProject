package model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String key;
    private final Map<String, String> variables;
    private Date lastRequest;
    // Millisecondes
    private int timeout;

    public HttpSession(String key, Date lastRequest, int timeout) {

	super();
	this.key = key;
	this.variables = new HashMap<String, String>();
	this.lastRequest = lastRequest;
	this.timeout = timeout;

    }
    
    public void addValue(String name, String value) {
	variables.put(name, value);
    }
    
    public boolean containsVariable(String name) {
	return variables.containsKey(name);
    }
    
    public String getValue(String name) {
	return variables.get(name);
    }

    public String getKey() {
	return key;
    }

    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
	this.timeout = timeout;
    }

    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.lastRequest = lastRequest;
    }
    
    public boolean exceededTimeout(Date current) {
	if((current.getTime() - lastRequest.getTime()) > timeout)
	    return true;
	return false;
    }
    
    /**
     * We don't create Session with null values in this Servern, then we didn't
     * make test for it
     */
    public boolean equals(HttpSession session) {
	if(key != null && session.getKey() != null)
	    return (key.equals(session.getKey()));
	return false;
    }

}
