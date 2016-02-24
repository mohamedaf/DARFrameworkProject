package server;

import java.io.IOException;

public class HttpServerMain {

    public static void main(String[] args) {

	HttpServerThread hsm;

	try {
	    hsm = new HttpServerThread();
	    hsm.start();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
