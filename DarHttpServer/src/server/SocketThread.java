package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;

import model.HttpRequest;
import model.HttpResponse;
import model.request.HttpRequestMethod;
import model.response.HttpResponseStatus;
import point.PointController;

public class SocketThread extends Thread {

    private static PointController pointController;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public SocketThread(Socket socket) {

	super();
	System.out.println("new Socket Thread");
	if (SocketThread.pointController == null) {
	    SocketThread.pointController = new PointController();
	}

	try {
	    bufferedReader = new BufferedReader(new InputStreamReader(
		    socket.getInputStream()));
	    printWriter = new PrintWriter(socket.getOutputStream());
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void run() {

	super.run();
	try {
	    HttpRequest req;
	    HttpResponse resp;
	    String request = readRequest();
	    System.out.println(request);

	    if (request.length() > 0) {
		req = HttpRequest.parse(request);
		resp = new HttpResponse(HttpResponseStatus.OK, req);
		pointControllerDispacher(req, resp);
	    } else {
		resp = new HttpResponse(HttpResponseStatus.Bad_Request,
			"text/plain", "Error 400 Bad Request");
	    }

	    printWriter.write(resp.toString());
	    printWriter.flush();
	    printWriter.flush();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private String readRequest() {

	String s;
	StringBuilder res = new StringBuilder();
	int contentLength = -1;

	while (true) {
	    try {
		s = bufferedReader.readLine();
		res.append(s + "\n");
		if (s.startsWith("Content-Length: ")) {
		    contentLength = Integer.parseInt(s.substring(
			    "Content-Length: ".length(), s.length()));
		}
		if (s.isEmpty()) {
		    if (contentLength != -1) {
			CharBuffer buffer = CharBuffer.allocate(contentLength);
			bufferedReader.read(buffer.array(), 0, contentLength);
			res.append(buffer.toString() + "\n");
		    }
		    return res.toString();
		}
	    } catch (IOException e) {
		e.printStackTrace();
		return new String();
	    }
	}

    }

    private void pointControllerDispacher(HttpRequest req, HttpResponse resp) {

	if (req.getMethod().equals(HttpRequestMethod.GET)) {
	    pointController.doGet(req, resp);
	} else if (req.getMethod().equals(HttpRequestMethod.POST)) {
	    pointController.doPost(req, resp);
	} else if (req.getMethod().equals(HttpRequestMethod.PUT)) {
	    pointController.doPut(req, resp);
	} else if (req.getMethod().equals(HttpRequestMethod.DELETE)) {
	    pointController.doDelete(req, resp);
	}

    }

}
