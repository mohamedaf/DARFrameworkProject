package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.request.HttpRequest;
import model.response.HttpResponse;
import model.response.HttpResponseStatus;

public class SocketThread extends Thread {
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private boolean run;

    public SocketThread(Socket socket) {
	super();
	try {
	    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    printWriter = new PrintWriter(socket.getOutputStream());
	    socket.setSoTimeout(100);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	run = true;
    }

    @Override
    public void run() {
	super.run();
	String request;
	while (run) {
	    request = readRequest();
	    if (request.length() > 0) {
		try {
		    HttpRequest rq = HttpRequest.parse(request);
		    HttpResponse rp = new HttpResponse(HttpResponseStatus.OK, rq);
		    System.out.println(rp.toString());
		    printWriter.write(rp.toString());
		    printWriter.flush();
		    printWriter.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private String readRequest() {
	String s;
	StringBuilder res = new StringBuilder();
	try {
	    while ((s = bufferedReader.readLine()) != null) {
		System.out.println(s);
		res.append(s + "\n");
	    }
	} catch (IOException e) {
	}
	return res.toString();
    }

    public void stopThread() {
	this.run = false;
    }

}
