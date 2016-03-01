package server;

import java.io.IOException;
import java.net.ServerSocket;

import model.HttpSessionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dispatcher.Dispatcher;

public class HttpServerThread extends Thread {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerThread.class);
    private final ServerSocket welcomeSocket;
    private final Dispatcher dispatcher;
    private final HttpSessionProvider sessionProvider;
    private boolean run;

    public HttpServerThread(Dispatcher dispatcher, HttpSessionProvider sessionProvider) throws IOException {
	
	super();
	run = true;
	this.welcomeSocket = new ServerSocket(1024);
	this.dispatcher = dispatcher;
	this.sessionProvider = sessionProvider;
	
    }

    @Override
    public void run() {
	
	super.run();
	try {
	    while (run) {
		new SocketThread(welcomeSocket.accept(), dispatcher, sessionProvider).start();
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
