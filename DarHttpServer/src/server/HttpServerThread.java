package server;

import java.io.IOException;
import java.net.ServerSocket;

import dispacher.Dispacher;

public class HttpServerThread extends Thread {
    
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
		System.out.println("New connection");
	    }
	    welcomeSocket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    stopThread();
	}

    }

    public void stopThread() {
	this.run = false;
    }

}
