package dispacher;

import httpServlet.IHttpServlet;
import point.PointController;

public class ControllerFactory {
    
    public static IHttpServlet getPointController() {
	return new PointController();
    }

}
