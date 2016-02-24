package model;

import java.io.IOException;

public class HttpRequestParseException extends IOException {

    private static final long serialVersionUID = 5792443088605596602L;

    public HttpRequestParseException(String message) {
	super(message);
    }

}
