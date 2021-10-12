package tcp;

import java.io.*;
import java.net.*;

public class EchoServer {

	private static final int BUF_SIZE = 1024;
	
	public static int PORT = 8000;

	public static void main(String[] args) {

		try (ServerSocket ss = new ServerSocket(PORT)) {
			for (;;) {
				try( Socket cs = ss.accept()){
					
					InputStream is = cs.getInputStream();
					OutputStream os = cs.getOutputStream();
					
					byte[] buf = new byte[BUF_SIZE];
					
					int n;
					while( (n = is.read( buf )) > 0 )
						os.write( buf, 0, n);
					
				} catch( IOException x) {
					x.printStackTrace();
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

	}
}
