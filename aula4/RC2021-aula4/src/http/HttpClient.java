package http;

public interface HttpClient {
	
	static final int HTTP_DEFAULT_PORT = 80;

	byte[] doGet( String url );
	
	byte[] doRangeGet( String url, long start );

	byte[] doRangeGet( String url, long start, long end );

}
