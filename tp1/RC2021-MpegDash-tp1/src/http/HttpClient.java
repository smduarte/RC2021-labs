package http;

/**
 * Interface for a basic HTTP client 
 * 
 * @author smduarte
 *
 */
public interface HttpClient {

	static final int HTTP_DEFAULT_PORT = 80;

	/**
	 * TODO: replace aaaaa-bbbbb with student numbers
	 */
	static final String USER_AGENT = "User-Agent: X-aaaaa-bbbbb";

	/**
	 * Gets the full contents of a resource
	 * 
	 * @param url - the url of the requested resource
	 * @return the byte contents of the resource, or null if an error occurred
	 */
	byte[] doGet(String url);

	/**
	 * 
	 * Gets a range of a resource' contents from a given offset
	 * 
	 * @param url - the url of the requested resource
	 * @param start - the start offset of the requested range
	 * @return the contents range of the resource, or null if an error occurred
	 */
	public byte[] doGetRange(String url, long start);

	/**
	 * 
	 * Gets a range of a resource' contents 
	 * 
	 * @param url - the url of the requested resource
	 * @param start - the start offset of the requested range
	 * @param end - the end offset of the requested range (inclusive)
	 * @return the contents range of the resource, or null if an error occurred
	 */
	public byte[] doGetRange(String url, long start, long end);
	
}
