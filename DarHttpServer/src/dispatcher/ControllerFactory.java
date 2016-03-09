package dispatcher;

import httpServlet.IHttpServlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

public class ControllerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerFactory.class);

    private static IHttpServlet pointController = null;

    public static IHttpServlet getPointController() {

	if (pointController == null) {
	    JarClassLoader jcl = new JarClassLoader();
	    try {
		jcl.add(new FileInputStream(System.getProperty("user.dir") + "/apps/point.jar"));
	    } catch (FileNotFoundException e) {
		LOGGER.error("Application Jar not found {}", e);
		return null;
	    }
	    JclObjectFactory factory = JclObjectFactory.getInstance();
	    pointController = (IHttpServlet) factory.create(jcl, "point.PointController");
	}
	return pointController;

    }

}
