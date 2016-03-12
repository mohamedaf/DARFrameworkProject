package goodDeal.model;

import java.util.ArrayList;

public class UsersProvider {

    private static final ArrayList<User> users = new ArrayList<User>();

    public boolean addUser(User user) {
	
	if(user != null && user.getUsername() != null && user.getPassword() != null) {
	    users.add(user);
	    return true;
	}
	return false;
	
    }
    
    public User getUser(String username, String password) {
	
	String userN, passW;
	
	for(User user : users) {
	    userN = user.getUsername();
	    passW = user.getPassword();
	    if(userN.equals(username) && passW.equals(password))
		return user;
	}
	return null;
	
    }
    
    public boolean isUser(String username, String password) {
	return getUser(username, password) != null;
    }
    
}
