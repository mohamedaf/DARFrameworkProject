package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
				System.out.println("Request : \n" + request);
				try {
					HttpRequest rq = HttpRequest.parse(request);

					HttpResponse rp = new HttpResponse(HttpResponseStatus.OK, rq.getHeaders(), rq.getCookies(),
							rq.getBody());
					System.out.println(
							"HTTP/1.1 200 OK\nDate: Mon, 23 May 2005 22:38:34 GMT\nServer: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\nLast-Modified: Wed, 08 Jan 2003 23:11:55 GMT\nETag: \"3f80f-1b6-3e1cb03b\"\nContent-Type: text/html; charset=UTF-8\nContent-Length: 138\nAccept-Ranges: bytes\n\n"
									+ rp.getHtmlResponse());
					printWriter.write(
							"HTTP/1.1 200 OK\nDate: Mon, 23 May 2005 22:38:34 GMT\nServer: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\nLast-Modified: Wed, 08 Jan 2003 23:11:55 GMT\nETag: \"3f80f-1b6-3e1cb03b\"\nContent-Type: text/html; charset=UTF-8\nContent-Length: 138\nAccept-Ranges: bytes\n\n"
									+ rp.getHtmlResponse());
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
