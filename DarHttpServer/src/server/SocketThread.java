package server;

import httpServlet.IHttpServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.util.Map;

import model.HttpRequest;
import model.HttpResponse;
import model.HttpSessionProvider;
import model.request.HeaderRequestField;
import model.request.HttpRequestMethod;
import model.request.IHttpRequest;
import model.response.HttpResponseError;
import model.response.HttpResponseStatus;
import model.response.IHttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dispatcher.Dispatcher;
import dispatcher.DispatcherResult;

public class SocketThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketThread.class);
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private final Dispatcher dispatcher;
    private final HttpSessionProvider sessionProvider;
    private final Socket socket;

    public SocketThread(Socket socket, Dispatcher dispatcher,
	    HttpSessionProvider sessionProvider) {

	super();
	LOGGER.info("new Socket Thread");

	try {
	    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    printWriter = new PrintWriter(socket.getOutputStream());
	} catch (IOException e) {
	    LOGGER.error("", e);
	}
	this.dispatcher = dispatcher;
	this.sessionProvider = sessionProvider;
	this.socket = socket;

    }

    @Override
    public void run() {

	super.run();
	try {
	    IHttpRequest req;
	    IHttpResponse resp;
	    String request = readRequest();

	    if (request.length() > 0) {
		req = HttpRequest.parse(request, sessionProvider);
		resp = new HttpResponse(HttpResponseStatus.OK, req);
		dispatcher(req, resp);
		checkSession(req, resp);
	    } else {
		resp = new HttpResponse(HttpResponseStatus.Bad_Request,
			"text/plain", "Error 400 Bad Request");
	    }

	    LOGGER.info("Sending http response");
	    printWriter.write(resp.toString());
	    printWriter.flush();
	    printWriter.flush();
	    socket.close();
	} catch (Exception e) {
	    LOGGER.error("", e);
	}

    }

    private String readRequest() {
	LOGGER.info("Reading http request");

	String s;
	StringBuilder res = new StringBuilder();
	int contentLength = -1;

	while (true) {
	    try {
		s = bufferedReader.readLine();
		res.append(s + "\n");
		if (s.startsWith("Content-Length: ")) {
		    contentLength = Integer.parseInt(s.substring("Content-Length: ".length(), s.length()));
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

    private void dispatcher(IHttpRequest req, IHttpResponse resp) {
	LOGGER.info("Dispatcher");

	URL url = req.getUrl();
	String host = url.getHost();
	String path = url.getPath();
	Map<String, String> params = req.getParams();

	if (host.isEmpty() || !dispatcher.isValidApplication(host)) {
	    LOGGER.warn("Incorrect application name, Http Not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    return;
	}

	DispatcherResult result = dispatcher.isValidPath(resp, req.getMethod(), path, params);

	if (result == null) {
	    return;
	}

	IHttpServlet servlet = result.getServlet();
	String call = result.getCall();

	if (servlet == null || call == null) {
	    LOGGER.warn("Server or call is null, Http not found");
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Not_Found);
	    return;
	}

	controllerDispatcher(servlet, req, resp, call);

    }

    private void controllerDispatcher(IHttpServlet servlet, IHttpRequest req, IHttpResponse resp, String call) {
	LOGGER.info("Controller dispatcher, method : {}, call : {}", req.getMethod(), call);

	if (req.getMethod().equals(HttpRequestMethod.GET)) {
	    servlet.doGet(req, resp, call);
	} else if (req.getMethod().equals(HttpRequestMethod.POST)) {
	    servlet.doPost(req, resp, call);
	} else if (req.getMethod().equals(HttpRequestMethod.PUT)) {
	    servlet.doPut(req, resp, call);
	} else if (req.getMethod().equals(HttpRequestMethod.DELETE)) {
	    servlet.doDelete(req, resp, call);
	}

    }

    private void checkSession(IHttpRequest req, IHttpResponse resp) {

	if (resp.getStatus().equals(HttpResponseStatus.OK)) {
	    String appName = req.getUrl().getHost();
	    String userAgent = req.getHeaderValue(HeaderRequestField.USER_AGENT);
	    String ipAdress = socket.getInetAddress().toString();
	    
	    try {
		String key = URLEncoder.encode((userAgent + ipAdress), "UTF-8");
		sessionProvider.checkSessions();
		if (sessionProvider.getSession(appName, key) != null && req.getCookie(key) != null) {
		    resp.setBody(resp.getBody() + " , Not new session");
		} else {
		    resp.addCookie((userAgent + ipAdress), "session");
		    sessionProvider.addSession(appName, key);
		    resp.setBody(resp.getBody() + " , New session");
		}
	    } catch (UnsupportedEncodingException e) {
		LOGGER.error("Error while encoding key {}", e);
	    }
	}

    }

}
