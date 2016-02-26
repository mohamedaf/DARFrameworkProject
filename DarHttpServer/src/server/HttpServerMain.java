package server;

import java.io.IOException;

public class HttpServerMain {

    @SuppressWarnings("null")
    public static void main(String[] args) {

	HttpServerThread hsm = null;
	try {
	    hsm = new HttpServerThread();
	    hsm.start();
	} catch (IOException e) {
	    e.printStackTrace();
	    hsm.stopThread();
	}

    }

}
