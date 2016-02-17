package model.header;

public enum HeaderRequestField implements HeaderField {

	ACCEPT("Accept"),
	ACCEPT_CHARSET("Accept-Charset"),
	ACCEPT_ENCODING("Accept-Encoding"),
	ACCEPT_DATETIME("Accept-Datetime"),
	ACCEPT_LANGUAGE("Accept-Language"),
	USER_AGENT("User-Agent"), 
	AUTHORIZATION("Authorization"),
	CONNECTION("Connection"),
	EXPECT("Expect"),
	FORWARDED("Forwarded"),
	FROM("From"),
	IF_MODIFIED_SINCE("If-Modified-Since"),
	IF_RANGE("If-Range"),
	IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
	IF_NONE_MATCH("If-None-Match"),
	CACHE_CONTROL("Cache-Control"),
	VIA("Via"),
	WARNING("Warning"),
	HOST("Host"),
	COOKIE("Cookie"),
	RANGE("Range"),
	DNT("DNT");

	private String name;

	private HeaderRequestField(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public static HeaderRequestField getField(String reqName) {
		for (HeaderRequestField field : HeaderRequestField.values()) {
			if (field.getName().equalsIgnoreCase(reqName))
				return field;
		}
		return null;
	}
}
