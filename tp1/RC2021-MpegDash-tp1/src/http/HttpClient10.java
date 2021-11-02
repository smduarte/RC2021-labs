package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;

/**
 * Implements a basic HTTP1.0 client
 * @author smduarte
 *
 */
public class HttpClient10 implements HttpClient {

	private static final String HTTP_SUCCESS = "20";
	private static final String GET_FORMAT_STR = "GET %s HTTP/1.0\r\n%s\r\n\r\n";

	static private byte[] getContents(InputStream in) throws IOException {

		String reply = Http.readLine(in);
		//System.out.println(reply);
		if (!reply.contains(HTTP_SUCCESS)) {
			throw new RuntimeException(String.format("HTTP request failed: [%s]", reply));
		}
		while ((reply = Http.readLine(in)).length() > 0) {
			//System.out.println(reply);
		}
		return in.readAllBytes();
	}
	
	@Override
	public byte[] doGet(String urlStr) {
		try {
			URL url = new URL(urlStr);
			int port = url.getPort();
			try (Socket cs = new Socket(url.getHost(), port < 0 ? url.getDefaultPort(): port)) {
				String request = String.format(GET_FORMAT_STR, url.getFile(), USER_AGENT);
				//System.out.println(request);
				cs.getOutputStream().write(request.getBytes());
				return getContents(cs.getInputStream());
			}
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}

	public byte[] doGetRange(String urlStr, long start, long end) {
		// TODO
		return null;
	}

	@Override
	public byte[] doGetRange(String url, long start) {
		// TODO
		return null;
	}	
}
