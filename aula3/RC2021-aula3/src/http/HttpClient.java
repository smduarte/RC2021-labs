package http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

public class HttpClient {

	private static final int HTTP_DEFAULT_PORT = 80;

	private static final String DEFAULT_URL = "http://localhost:9999/index.html";
	
	public static void main(String[] args) throws Exception {
		
		String url = args.length == 1 ? args[0] : DEFAULT_URL;

		URL u = new URL(url);

		System.out.println("\n========================================\n");
		System.out.println("Processing url: " + url + "\n");
		System.out.println("========================================\n");

		System.out.println("protocol = " + u.getProtocol());
		System.out.println("authority = " + u.getAuthority());
		System.out.println("host = " + u.getHost());
		System.out.println("port = " + u.getPort());
		System.out.println("path = " + u.getPath());
		System.out.println("query = " + u.getQuery());
		System.out.println("filename = " + u.getFile());
		System.out.println("ref = " + u.getRef());
		System.out.println(u);

		// Assuming URL of the form http:// ....

		int port = u.getPort();
		String host = u.getHost();
		String fileName = u.getPath();
		try (Socket sock = new Socket(host, port == -1 ? HTTP_DEFAULT_PORT : port )) {

			InputStream fromServer = sock.getInputStream();
			OutputStream toServer = sock.getOutputStream();

			System.out.println("\n========================================\n");
			System.out.println("Connected to server");
			String request = String.format("GET %s HTTP/1.0\r\n\r\n", fileName);

			toServer.write(request.getBytes());
			System.out.println("Sent request: " + request);

			System.out.println("========================================");
			String statusLine = Http.readLine(fromServer);
			System.out.println("Got status line: " + statusLine + "\n");

			// String[] result = Http.parseHttpReply(statusLine);

			String headerLine;
			while ((headerLine = Http.readLine(fromServer)).length() > 0) {
				System.out.println("Header line:\t" + headerLine);
			}

			System.out.println("\n========================================");
			System.out.println("\n Got an empty line, showing body \n");
			System.out.println("========================================");

			Http.dumpStream(fromServer, System.out);
			System.out.println();
		}
	}

}
