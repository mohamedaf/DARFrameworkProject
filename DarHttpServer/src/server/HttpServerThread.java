package server;

import java.io.IOException;
import java.net.ServerSocket;

public class HttpServerThread extends Thread {
    
    private final ServerSocket welcomeSocket;
    private boolean run;

    public HttpServerThread() throws IOException {
	
	super();
	run = true;
	this.welcomeSocket = new ServerSocket(1024);
	
    }

    @Override
    public void run() {
	
	super.run();
	try {
	    while (run) {
		new SocketThread(welcomeSocket.accept()).start();
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
