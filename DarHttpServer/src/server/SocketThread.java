package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.HttpRequest;
import model.HttpResponse;
import model.request.HttpRequestMethod;
import model.response.HttpResponseStatus;
import point.PointController;

public class SocketThread extends Thread {
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private boolean run;
    private final PointController controller;

    public SocketThread(Socket socket) {
	super();
	controller = new PointController();
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
		    //request += "x=2;y=7";
		    HttpRequest req = HttpRequest.parse(request);
		    HttpResponse resp = new HttpResponse(HttpResponseStatus.OK, req);
		    
		    if(req.getMethod().equals(HttpRequestMethod.GET)){
			controller.doGet(req, resp);
		    } else if(req.getMethod().equals(HttpRequestMethod.POST)){
			controller.doPost(req, resp);
		    } else if(req.getMethod().equals(HttpRequestMethod.PUT)){
			controller.doPut(req, resp);
		    } else if(req.getMethod().equals(HttpRequestMethod.DELETE)){
			controller.doDelete(req, resp);
		    }
		    
		    System.out.println(resp.toString());
		    System.out.println(controller.toString());
		    printWriter.write(resp.toString());
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
