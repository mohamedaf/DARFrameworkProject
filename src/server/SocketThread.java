package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketThread extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private boolean run;

    public SocketThread(Socket socket) {
	super();
	this.socket = socket;
	try {
	    bufferedReader = new BufferedReader(new InputStreamReader(
		    socket.getInputStream()));
	    printWriter = new PrintWriter(socket.getOutputStream());
	} catch (IOException e) {
	    e.printStackTrace();
	}
	run = true;
    }

    @Override
    public void run() {
	super.run();
	String s;
	try {
	    while (run) {

		s = bufferedReader.readLine();
		if (s != null) {
		    System.out.println("Received : " + s);
		    printWriter.println(s);
		    printWriter.flush();
		}
	    }
	    socket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    run = false;
	}
    }

    public void sotpThread() {
	this.run = false;
    }

}
