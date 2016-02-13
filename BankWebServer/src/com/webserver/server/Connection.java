package com.webserver.server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.webserver.processor.Processor;


/**
 *  Reference: https://github.com/ibogomolov/WebServer/blob/master/src/web/Connection.java
 *  
 *  Enhancement made by me: Modified the class Contructor to take only the Socket value. 
 *  Modified the code to support HTTP methods PUT, POST and DELETE in addition to GET
 *  Invoking Processor class to process the incoming HTTP request, parse the business data and return suitable HTTP response.
 *  The code written by me is marked below.
 *  
 */

public class Connection implements Runnable {
	private Socket client;
	private InputStream in;
	private OutputStream out;

	public Connection(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			in = client.getInputStream();
			out = client.getOutputStream();

			HttpRequest request = HttpRequest.parseAsHttp(in);

			if (request != null) {
				System.out.println("Request for " + request.getUrl() + " is being processed " +
						"by socket at " + client.getInetAddress() +":"+ client.getPort());

				HttpResponse response;

				String method;
				
				/************************************************** @author Madhuri Nagaraj Kaushik ********************************************************/
				if ((method = request.getMethod()).equals(HttpRequest.HttpMethod.PUT) 
						|| method.equals(HttpRequest.HttpMethod.POST) || method.equals(HttpRequest.HttpMethod.GET)
						|| method.equals(HttpRequest.HttpMethod.DELETE)) {
					Processor processor = Processor.getInstance();
					response = processor.process(request);
				/************************************************** @author Madhuri Nagaraj Kaushik ********************************************************/
				} else {
					response = new HttpResponse(HttpResponse.StatusCode.NOT_IMPLEMENTED);
				}
				respond(response);
			} else {
				System.err.println("Server accepts only HTTP protocol.");
			}
		} catch (IOException e) {
			System.err.println("Error in client's IO.");
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				System.err.println("Error while closing client socket.");
			}
		}

	}
	public void respond(HttpResponse response) {
		String toSend = response.toString();
		PrintWriter writer = new PrintWriter(out);
		writer.write(toSend);
		writer.flush();
		
	}
}
