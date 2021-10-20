
package http;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

/**
 * Auxiliary methods to deal with HTTP requests and replies
 */
public class Http {

	private static final int TMP_BUF_SIZE = 1024;

	/**
	 * Copies data from an input stream to an output stream
	 */
	public static void dumpStream(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[TMP_BUF_SIZE];

		int n;
		while ((n = in.read(buf)) > 0)
			out.write(buf, 0, n);
	}

	/**
	 * Reads one line from an InputStream
	 */
	public static String readLine(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		int c;
		while ((c = is.read()) >= 0) {
			if (c == '\r')
				continue;
			if (c == '\n')
				break;
			sb.append(Character.valueOf((char) c));
		}
		return sb.toString();			
	}

	/**
	 * Parses the first line of the HTTP request and returns an array of three
	 * strings: reply[0] = method, reply[1] = object and reply[2] = version Example:
	 * input "GET index.html HTTP/1.0" output reply[0] = "GET", reply[1] =
	 * "index.html" and reply[2] = "HTTP/1.0"
	 * 
	 * If the input is malformed, returns null
	 */
	static final Pattern HTTP_REQUEST_REGEX = Pattern
			.compile("^(GET|POST|PUT|HEAD|OPTIONS|DELETE)\\s(\\S*)\\s(HTTP\\/\\d\\.\\d)");

	public static String[] parseHttpRequest(String request) {
		Matcher m = HTTP_REQUEST_REGEX.matcher(request);
		if (m.matches())
			return new String[] { m.group(1), m.group(2), m.group(3) };
		else
			return null;
	}

	/**
	 * Parses the first line of the HTTP reply and returns an array of three
	 * strings: reply[0] = version, reply[1] = number and reply[2] = result message
	 * Example: input "HTTP/1.0 501 Not Implemented" output reply[0] = "HTTP/1.0",
	 * reply[1] = "501" and reply[2] = "Not Implemented"
	 * 
	 * If the input is malformed, returns null
	 */

	static final Pattern HTTP_REPLY_REGEX = Pattern.compile("^(HTTP\\/\\d\\.\\d)\\s(\\d{3})(.*)");

	public static String[] parseHttpReply(String reply) {
		Matcher m = HTTP_REPLY_REGEX.matcher(reply);
		if (m.matches())
			return new String[] { m.group(1), m.group(2), m.group(3) };
		else
			return null;
	}

	/**
	 * Parses a http header returning an array with the name of the attribute header
	 * in position 0 and its value in position 1 Example, for "Connection:
	 * Keep-alive", returns: [0]->"Connection"; [1]->"Keep-alive"
	 * 
	 * If the input is malformed, returns null
	 *
	 */
	static final Pattern HTTP_HEADER_REGEX = Pattern.compile("^(\\S+):\\s(\\S+)");

	public static String[] parseHttpHeader(String header) {
		Matcher m = HTTP_HEADER_REGEX.matcher(header);
		if (m.matches())
			return new String[] { m.group(1), m.group(2) };
		else
			return null;
	}

	/**
	 * Parses a http range header returning an array the range values Examples:
	 * "range=1000-2000" returns { 1000, 2000 } "range=1000-" returns { 1000, -1 }
	 * 
	 * If he input is malformed, returns null;
	 */

	static final Pattern HTTP_HEADER_RANGE_REGEX = Pattern.compile("Range:\\s+bytes=(\\d+)-(\\d+)?");

	public static long[] parseRangeValues(String header) {
		Matcher m = HTTP_HEADER_RANGE_REGEX.matcher(header);
		if (m.matches()) {
			String first = m.group(1), second = m.group(2);
			if (second == null)
				return new long[] { Long.valueOf(first), -1L };
			else
				return new long[] { Long.valueOf(first), Long.valueOf(second) };
		} else
			return null;
	}
	
	static final Pattern HTTP_HEADER_SERVER_RANGE_REGEX = Pattern.compile("Content-Range:\\s+bytes\\s(\\d+)-(\\d+)\\/(\\d+)");
	public static long[] parseRangeValuesSentByServer(String header) {
		Matcher m = HTTP_HEADER_SERVER_RANGE_REGEX.matcher(header);
		if (m.matches()) {
			String first = m.group(1), second = m.group(2), third = m.group(3);
			return new long[] { Long.valueOf(first), Long.valueOf(second), Long.valueOf(third) };
		} else
			return null;
	}	

	/**
	 * Parses a http query returning a Map with the key,value pairs found Examples:
	 * "/word?ip=127.0.0.1&port=1234" returns a map: {"ip" : "127.0.0.1", "port" =
	 * "1234"}
	 * 
	 * If he input is malformed, returns an empty map;
	 */

	static final Pattern HTTP_QUERY_REGEX = Pattern.compile("^(.*)\\?((\\w+=\\S*)+(&\\w+=\\S*)*)");

	public static Map<String, String> parseQuery(String resourceKey, String resource) {
		Matcher m = HTTP_QUERY_REGEX.matcher(resource);
		if (m.matches()) {
			Map<String, String> res = new HashMap<>();

			res.put(resourceKey, m.group(1));

			String query = m.group(2);
			for (String pair : query.split("&")) {
				String[] kv = pair.split("=");
				res.put(kv[0], kv[1]);
			}
			return res;
		} else
			return Collections.emptyMap();
	}

	static void unitTestsAdHoc() {

		String request1 = "%s /resource HTTP/1.0";
		String request2 = "%s /resource?key1=val1&key2=val2 HTTP/1.1";

		for (String i : new String[] { "GET", "PUT", "POST", "HEAD", "DELETE", "OPTIONS" }) {
			Assert( Result(Http::PrintArray, Http.parseHttpRequest(String.format(request1, i))) != null );
			Assert( Result(Http::PrintArray, Http.parseHttpRequest(String.format(request2, i))) != null );
		}

		String reply1 = "HTTP/1.0 200 OK";
		String reply2 = "HTTP/1.1 501 Not Implemented";

		Assert( Result(Http::PrintArray, Http.parseHttpReply(reply1)) != null);
		Assert( Result(Http::PrintArray, Http.parseHttpReply(reply2)) != null);

		String header = "Content-Length: 12345";
		String range1 = "Range: bytes=1-";
		String range2 = "Range: bytes=1-2";

		Assert( Result(Http::PrintArray, Http.parseHttpHeader(header)) != null);
		Assert( Result(Http::PrintArray, Http.parseHttpHeader(range1)) != null);
		Assert( Result(Http::PrintArray, Http.parseHttpHeader(range2)) != null);

//		Assert( Result(Http::PrintArray, Http.parseRangeValues(range1)) != null);
//		Assert( Result(Http::PrintArray, Http.parseRangeValues(range2)) != null);

		String query1 = "/resource?";
		String query2 = "/resource?key1=val1&key2=val2";
		String query3 = "http://ip:port/path/resource?key1=val1&key2=val2";
		
		Assert( Result(System.out::println, Http.parseQuery("_", query1)).size() == 0);
		Assert( Result(System.out::println, Http.parseQuery("_", query2)).size() == 3);
		Assert( Result(System.out::println, Http.parseQuery("_", query3)).size() == 3);
	}

	static void Assert(boolean value) {
		if (value != true)
			throw new AssertionError();
	}

	static <T> T Result(Consumer<T> c, T value) {
		c.accept(value);
		return value;
	}
	
	static <T> void PrintArray( T[] arr ) {
		System.out.println( Arrays.asList( arr ));
	}
	
	public static void main(String[] args) {
		unitTestsAdHoc();
	}
}
