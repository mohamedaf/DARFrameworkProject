package server;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dispacher.Dispacher;

public class HttpServerThread extends Thread {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerThread.class);
    private final ServerSocket welcomeSocket;
    private final Dispacher dispacher;
    private boolean run;

    public HttpServerThread(Dispacher dispacher) throws IOException {
	
	super();
	run = true;
	this.welcomeSocket = new ServerSocket(1024);
	this.dispacher = dispacher;
	
    }

    @Override
    public void run() {
	
	super.run();
	try {
	    while (run) {
		new SocketThread(welcomeSocket.accept(), dispacher).start();
		LOGGER.info("New connection");
	    }
	    welcomeSocket.close();
	} catch (IOException e) {
	    LOGGER.error("", e);
	    stopThread();
	}

    }

    public void stopThread() {
	this.run = false;
    }

}
