package server;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import dispacher.Dispacher;

public class HttpServerMain {

    @SuppressWarnings("unused")
    public static void main(String[] args) {

	HttpServerThread hsm = null;

	try {
	    File file = new File("webDispacher.xml");
	    SAXBuilder sxb = new SAXBuilder();
	    Document document = sxb.build(file);
	    Dispacher dispacher = new Dispacher(document);
	    hsm = new HttpServerThread(dispacher);
	    hsm.start();
	} catch (IOException e) {
	    if (hsm != null)
		hsm.stopThread();
	    System.out.println("Erreur lors de la lecture du webDispacher "
		    + e.getMessage());
	    e.printStackTrace();
	} catch (JDOMException e) {
	    System.out
		    .println("Erreur lors de la construction du fichier JDOM "
			    + e.getMessage());
	    e.printStackTrace();
	}

    }
}
