package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSessionProvider {

    private Map<String, List<HttpSession>> appSessions;
    
    public HttpSessionProvider() {
	appSessions = new HashMap<String, List<HttpSession>>();
    }
    
    public void checkSessions() {
	
	for(String appName : appSessions.keySet()) {
	    List<HttpSession> sessionList = appSessions.getOrDefault(appName, new ArrayList<HttpSession>(0));
	    for(int i=0; i<sessionList.size(); i++) {
		if(sessionList.get(i).exceededTimeout(new Date())) {
		    appSessions.get(appName).remove(i);
		    i--;
		}
	    }
	}
	
    }
    
    public void addSession(String appName, String key, int timeout) {
	
	if(appName != null && key != null) {
	    addSession(appName, new HttpSession(key, new Date(), timeout));
	}
	
    }
    
    public void addSession(String appName, String key) {
	
	if(appName != null && key != null) {
	    addSession(appName, key, 1200000);
	}
	
    }
    
    private void addSession(String appName, HttpSession session) {
	
	if(!appSessions.containsKey(appName)) {
	    appSessions.put(appName, new ArrayList<HttpSession>());
	}
	
	List<HttpSession> sessionList = appSessions.get(appName);
	if(sessionList.contains(session)) {
	    /**
	     * We have to replace this session, equals method compare between keys, 
	     * then maybe it's the same session but with different values (variables, timeout ..)
	     */
	    for(HttpSession s : sessionList) {
		if(s.equals(session)) {
		    sessionList.remove(s);
		}
	    }
	}
	sessionList.add(session);
	
    }
    
    public List<HttpSession> getSessionList(String appName) {
	return appSessions.get(appName);
    }
    
    public HttpSession getSession(String appName, String key) {
	
	for(HttpSession session : appSessions.getOrDefault(appName, new ArrayList<HttpSession>(0))) {
	    if(session.getKey().equals(key))
		return session;
	}
	return null;
	
    }
    
}
