package server;

import java.io.File;
import java.io.IOException;

import model.HttpSessionProvider;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dispatcher.Dispatcher;

public class HttpServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerMain.class);
    
    @SuppressWarnings("unused")
    public static void main(String[] args) {

	HttpServerThread hsm = null;

	try {
	    File file = new File("webDispatcher.xml");
	    SAXBuilder sxb = new SAXBuilder();
	    Document document = sxb.build(file);
	    Dispatcher dispatcher = new Dispatcher(document);
	    HttpSessionProvider sessionProvider = new HttpSessionProvider();
	    hsm = new HttpServerThread(dispatcher, sessionProvider);
	    hsm.start();
	} catch (IOException e) {
	    if (hsm != null)
		hsm.stopThread();
	    LOGGER.error("Erreur lors de la lecture du webDispatcher ", e);
	} catch (JDOMException e) {
	    LOGGER.error("Erreur lors de la construction du fichier JDOM ", e);
	}

    }
}
