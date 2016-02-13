package com.webserver.server;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Reference: https://github.com/ibogomolov/WebServer/blob/master/src/web/Server.java
 * 
 * Enhancement made by me: Added a webserver.properties file to register port number and thread limit. 
 * Modified the code to read the port number and thread limit values from the properties file, instead of passing them as args.
 * The code written by me is marked below.
 *
 */
public class WebServer implements Runnable {
	private ServerSocket serverSocket;
	private ExecutorService threadsPool;
	private final int serverPort;
	private final int threadsLimit;

	public WebServer(int port, int maxThreads) {
		this.serverPort = port;
		this.threadsLimit = maxThreads;
	}

	public static void main(String[] args) {
		
		/************************************************** @author Madhuri Nagaraj Kaushik ********************************************************/
		int port = 0;
		int maxThreads = 0;

		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("webserver.properties");
			properties.load(input);
			try {
				port = Integer.parseInt(properties.getProperty("webserver.port"));
				maxThreads = Integer.parseInt(properties.getProperty("webserver.maxThreads"));
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/***************************************************@author Madhuri Nagaraj Kaushik ********************************************************/
		
		new Thread(new WebServer(port, maxThreads)).start();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(serverPort);
			threadsPool = Executors.newFixedThreadPool(threadsLimit);
		} catch (IOException e) {
			System.err.println("Cannot listen on port " + serverPort);
			System.exit(1);
		}

		System.out.println("Running server on the port " + serverPort + 
				" with " + threadsLimit + " threads limit.");
		while (!Thread.interrupted()) {
			try {
				threadsPool.execute(new Thread(new Connection(serverSocket.accept())));
			} catch (IOException e) {
				System.err.println("Cannot accept client.");
			}
		}
		close();

	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Error while closing server socket.");
		}
		threadsPool.shutdown();
		try {
			if (!threadsPool.awaitTermination(10, TimeUnit.SECONDS)) 
				threadsPool.shutdownNow();
		} catch (InterruptedException e) {}
	}
}
