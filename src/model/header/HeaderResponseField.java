package model.header;

public enum HeaderResponseField {

    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"), ACCEPT_PATCH(
	    "Accept-Patch"), ACCEPT_RANGES("Accept-Ranges"), AGE("Age"), ALLOW(
	    "Allow"), CACHE_CONTROL("Cache-Control"), CONNECTION("Connection"), CONTENT_DISPOSITION(
	    "Content-Disposition"), CONTENT_ENCODING("Content-Encoding"), CONTENT_LENGTH(
	    "Content-Length"), CONTENT_LOCATION("Content-Location"), CONTENT_RANGE(
	    "Content-Range"), CONTENT_TYPE("Content-Type"), DATE("Date"), ETAG(
	    "ETag"), EXPIRES("Expires"), LAST_MODIFIED("Last-Modified"), LINK(
	    "Link"), LOCATION("Location"), PRAGMA("Pragma"), PROXY_AUTHENTICATE(
	    "Proxy-Authenticate"), RETRY_AFTER("Retry-After"), SERVER("Server"), SET_COOKIE(
	    "Set-Cookie"), STATUS("Status"), STRICT_TRANSPORT_SECURITY(
	    "Strict-Transport-Security"), TRAILER("Trailer"), TRANSFER_ENCODING(
	    "Transfer-Encoding"), TSV("TSV"), UPGRADE("Upgrade"), VARY("Vary"), VIA(
	    "Via"), WARNING("Warning"), WWW_AUTHENTICATE("WWW-Authenticate");

    private String name;

    private HeaderResponseField(String name) {
	this.name = name;
    }

    private String getName() {
	return this.name;
    }

    public static HeaderResponseField getField(String reqName) {
	for (HeaderResponseField field : HeaderResponseField.values()) {
	    if (field.getName().equalsIgnoreCase(reqName))
		return field;
	}
	return null;
    }

}
