package http;

import java.io.FileOutputStream;

public class HttpDownloader {

	private static final String DEFAULT_URL = "http://localhost:8080/earth.jpg";
	private static final String RESULT_FILE = "result.out.jpg";

	public static void main(String[] args) throws Exception {

		String url = args.length == 1 ? args[0] : DEFAULT_URL;

		
		try(FileOutputStream fos = new FileOutputStream(RESULT_FILE)){
			HttpClient c = new HttpClient1_0();
			byte[] data = c.doGet(url);
			System.out.printf("Got %s bytes\n", data.length);
			
			fos.write( data );
		} catch( Exception x ) {
			x.printStackTrace();
		}
	}

}
