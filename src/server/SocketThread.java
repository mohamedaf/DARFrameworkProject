package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.HttpRequest;
import model.HttpResponse;
import model.HttpResponseStatus;

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
		    HttpResponse rp = new HttpResponse(HttpResponseStatus.OK,
			    rq.getHeaders(), rq.getCookies(), rq.getHtmlResponse());
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
	String s, res = "";
	try {
	    while ((s = bufferedReader.readLine()) != null) {
		System.out.println(s);
		res += s + "\n";
	    }
	} catch (IOException e) {
	}
	return res;
    }

    public void stopThread() {
	this.run = false;
    }

}
