package dispatcher;

import httpServlet.IHttpServlet;
import point.PointController;

public class ControllerFactory {
    
    private static final PointController pointController = new PointController();
    
    public static IHttpServlet getPointController() {
	return pointController;
    }

}
