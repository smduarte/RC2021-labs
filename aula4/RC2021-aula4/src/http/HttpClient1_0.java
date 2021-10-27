package http;

import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

public class HttpClient1_0 implements HttpClient {

	private static final String GET_FORMAT_STR = "GET %s HTTP/1.0\r\n\r\n";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final Object HTTP_200_OK = "200";

	@Override
	public byte[] doGet(String url) {
		try {
			URL u = new URL(url);
			int port = u.getPort();
			try (Socket cs = new Socket(u.getHost(), port > 0 ? port : HTTP_DEFAULT_PORT)) {

				cs.getOutputStream().write(String.format(GET_FORMAT_STR, u.getPath()).getBytes());

				InputStream in = cs.getInputStream();

				String statusLine = Http.readLine(in);
				String[] statusParts = Http.parseHttpReply(statusLine);

				if (statusParts[1].equals(HTTP_200_OK)) {
					String headerLine;
					int contentLength = -1;
					while ((headerLine = Http.readLine(in)).length() > 0) {
						String[] headerParts = Http.parseHttpHeader(headerLine);
						if (headerParts[0].equalsIgnoreCase(CONTENT_LENGTH))
							contentLength = Integer.valueOf(headerParts[1]);
					}

					if (contentLength >= 0)
						return in.readNBytes(contentLength);
					else
						return in.readAllBytes();
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] doRangeGet(String url, long start) {
		// TODO
		return null;
	}

	@Override
	public byte[] doRangeGet(String url, long start, long end) {
		// TODO
		return null;
	}
}
