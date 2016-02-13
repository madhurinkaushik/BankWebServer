package com.webserver.server;

import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * Reference: https://github.com/ibogomolov/WebServer/blob/master/src/web/HttpResponse.java
 * 
 * Enhancement made by me: 
 * Added method 'frameHttpResponse()' which populates header values and body to the HttpResponse object.
 * 
 */
public class HttpResponse {
	private static final String protocol = "HTTP/1.0";

	private String status;
	private NavigableMap<String, String> headers = new TreeMap<String, String>();
	private byte[] body = null;

	public HttpResponse(String status) {
		this.status = status;
		setDate(new Date());
	}
	
	/************************************************** @author Madhuri Nagaraj Kaushik ********************************************************/
	public HttpResponse frameHttpResponse(String responseMsg) {
		setContentLength(responseMsg.length());
		setContentType(ContentType.TEXT);
		body = new byte[responseMsg.length()];
		body = responseMsg.getBytes();
		return this;
	}
	
	/************************************************** @author Madhuri Nagaraj Kaushik ********************************************************/
	
	public void setContentLength(long value) {
		headers.put("Content-Length", String.valueOf(value));
	}

	public void setContentType(String value) {
		headers.put("Content-Type", value);
	}
	
	public void setDate(Date date) {
		headers.put("Date", date.toString());
	}
	
	@Override
	public String toString() {
		String result = protocol + " " + status +"\n";
		for (String key : headers.descendingKeySet()) {
			result += key + ": " + headers.get(key) + "\n";
		}
		result += "\r\n";
		if (body != null) {
			result += new String(body);
		}
		return result;
	}
	
	public static class ContentType {
		public static final String TEXT = "text/plain";
	}
	
	public static class StatusCode {
		public static final String OK = "200 OK";
		public static final String NOT_IMPLEMENTED = "501 Not Implemented";
	}
}
