
package http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Super simple incomplete HTTP Server
 */
public class TODO_HttpServer {

	static final int PORT = 8080;

	static final String GET = "GET";
	static final String POST = "POST";

	/**
	 * Returns an input stream with an error message "Not Implemented"
	 */
	static InputStream notImplementedPageStream() {
		final String page = "<HTML><BODY>Request not implemented...</BODY></HTML>";
		int length = page.length();
		StringBuilder reply = new StringBuilder("HTTP/1.0 501 Not Implemented\r\n");
		reply.append("Date: " + new Date().toString() + "\r\n");
		reply.append("Server: " + "The tiny server (v0.1)" + "\r\n");
		reply.append("Content-Length: " + String.valueOf(length) + "\r\n\r\n");
		reply.append(page);
		return new ByteArrayInputStream(reply.toString().getBytes());
	}

	/**
	 * getFile: sends the requested file resource to the client
	 * 
	 */
	static void getFile(String fileName, OutputStream out) throws IOException {
		// TODO
	}

	/**
	 * postFile: receives the requested file resources from the client
	 * 
	 */
	static void postFile(String fileName, InputStream in, OutputStream out) throws IOException {
		// TODO
	}

	/**
	 * processHTTPrequest - handle one HTTP request
	 * 
	 * @param in  - stream from client
	 * @param out - stream to client
	 */
	private static void processHTTPrequest(InputStream in, OutputStream out) throws IOException {

		String request = "TODO";

		// TODO read request

		System.out.println("received: " + request);

		String[] requestParts = Http.parseHttpRequest(request);

		// TODO consume request headers

		String method = requestParts[0].toUpperCase();

		switch (method) {
		case GET:
			getFile(requestParts[1], out);
			break;
		case POST:
			postFile(requestParts[1], in, out);
			break;
		default:
			Http.dumpStream(notImplementedPageStream(), out);
		}
	}

	/**
	 * MAIN - accept and handle client connections
	 */

	public static void main(String[] args) {

		try (ServerSocket ss = new ServerSocket(PORT)) {
			for (;;) {
				System.out.println("Server ready at " + PORT);
				try (Socket clientS = ss.accept()) {
					InputStream in = clientS.getInputStream();
					OutputStream out = clientS.getOutputStream();
					processHTTPrequest(in, out);
				} catch (IOException x) {
					x.printStackTrace();
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
	}
}
