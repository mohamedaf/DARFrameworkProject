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

import org.jdom.JDOMException;
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
	IHttpRequest req;
	IHttpResponse resp;
	try {
	    String request = readRequest();
	    if (request.length() > 0) {
		req = HttpRequest.parse(request, sessionProvider);
		resp = new HttpResponse(HttpResponseStatus.OK, req);
		checkSession(req, resp);
		dispatcher(req, resp);
	    } else {
		resp = new HttpResponse(HttpResponseStatus.Bad_Request, "text/plain", "Error 400 Bad Request");
	    }
	} catch (Exception e) {
	    resp = new HttpResponse(HttpResponseStatus.Internal_Server_Error, "text/plain", "Error 500 Internal Server Error");
	    LOGGER.error("Internal Server Error ", e);
	}
	
	LOGGER.info("Sending http response");
	printWriter.write(resp.toString());
	printWriter.flush();
	printWriter.flush();
	try {
	    socket.close();
	} catch (IOException e) {
	    LOGGER.error("Error while closing connection socket ", e);
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
		LOGGER.error("Socket error when reading http Request ", e);
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

	DispatcherResult result = null;
	try {
	    result = dispatcher.isValidPath(resp, req.getMethod(), host, path, params);
	} catch (JDOMException | IOException e) {
	    LOGGER.error("Internal Server Error, please check dispacher.xml file of {} app ", host, e);
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Internal_Server_Error);
	    return;
	}

	if (result == null)
	    return;

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

	String appName = req.getUrl().getHost();
	String userAgent = req.getHeaderValue(HeaderRequestField.USER_AGENT);
	String ipAdress = socket.getInetAddress().toString();

	try {
	    String key = URLEncoder.encode((userAgent + ipAdress), "UTF-8");
	    sessionProvider.checkSessions();
	    if (sessionProvider.getSession(appName, key) == null || req.getCookie(key) == null) {
		resp.addCookie((userAgent + ipAdress), "session");
		sessionProvider.addSession(appName, key);
	    }
	    ((HttpRequest) req).setSession(sessionProvider.getSession(appName, key));
	} catch (UnsupportedEncodingException e) {
	    LOGGER.error("Error while encoding key {}", e);
	    HttpResponseError.setHttpResponseError(resp, HttpResponseStatus.Internal_Server_Error);
	}

    }

}
