package model;

import java.util.HashMap;
import java.util.Map;

import model.header.HeaderField;
import model.header.HeaderRequestField;
import model.header.HeaderField;

public class HttpRequest {

	private final HttpRequestMethod method;
	private final Map<HeaderField, String> headers;
	private final Map<String, String> cookies;
	private final String body;

	public HttpRequest(HttpRequestMethod method, Map<HeaderField, String> headers, Map<String, String> cookies,
			String body) {
		super();
		this.method = method;
		this.headers = (headers != null) ? headers : new HashMap<HeaderField, String>();
		this.cookies = (cookies != null) ? cookies : new HashMap<String, String>();
		this.body = body;
	}

	public Map<HeaderField, String> getHeaders() {
		return headers;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public HttpRequestMethod getMethod() {
		return method;
	}

	public String addHeaderValue(HeaderField field, String value) {
		return headers.put(field, value);
	}

	public String getHeaderValue(HeaderField field) {
		return headers.get(field);
	}

	public boolean haveCookie(String key) {
		return cookies.containsKey(key);
	}

	public String addCookie(String key, String value) {
		return cookies.put(key, value);
	}

	public String getCookie(String key) {
		return cookies.get(key);
	}

	public String getBody() {
		return body;
	}

	public static HttpRequest parse(String httpRequest) throws Exception {
		HttpRequestMethod method;
		Map<HeaderField, String> headers = new HashMap<>();
		Map<String, String> cookies = new HashMap<>();
		String body = "";
		boolean reachedBody = false;

		String[] lines = httpRequest.split("\n");
		if (lines.length < 1) {
			throw new Exception("Error ! Request can't be empty");
		}
		method = HttpRequestMethod.valueOf(lines[0].split(" ")[0]);
		System.out.println("Method : " + method);

		HeaderField key;
		String value;
		String[] splitL, cookiePair;
		for (int i = 1; i < lines.length; i++) {
			if (!reachedBody) {
				if (lines[i].length() > 0) {
					splitL = lines[i].split(": ");
					key = HeaderRequestField.getField(splitL[0]);
					value = splitL[1];
					if (key == HeaderRequestField.COOKIE) {
						for (String s : value.split(";")) {
							cookiePair = s.split(":");
							cookies.put(cookiePair[0], cookiePair[1]);

							System.out.printf("Cookie : %s => %s\n", cookiePair[0], cookiePair[1]);
						}
					} else {
						System.out.printf("Argument : %s -- Value : %s\n", key.getName(), value);
						headers.put(key, value);
					}
				} else {
					reachedBody = true;
				}
			} else {
				body += lines[i];
			}
		}
		System.out.printf("Body :\n%s\n", body);
		return new HttpRequest(method, headers, cookies, body);
	}

	@Override
	public String toString() {
		if (method == null)
			return "";

		String request = method.name() + " / HTTP/1.1";

		for (HeaderField field : headers.keySet()) {
			request += field.getName() + ": " + headers.get(field) + "\n";
		}

		if (!cookies.isEmpty()) {
			request += "Cookie:";
		}

		for (String name : cookies.keySet()) {
			request += " " + name + "=" + cookies.get(name) + ";";
		}

		request += "\n\n";
		if (body != null)
			request += body;

		return request;
	}

}
