package tcp;

import java.io.*;
import java.net.*;

public class EchoClient {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("usage: <server> <port> <msg>");
			System.exit(0);
		}

		String host = args[0];
		String message = args[2];
		int port = Integer.valueOf(args[1]);

		
		try (Socket cs = new Socket(host, port)) {
			
			OutputStream os = cs.getOutputStream();
			
			os.write( message.getBytes() );
			
			cs.shutdownOutput();
			
			InputStream is = cs.getInputStream();
			
			byte[] echo = is.readAllBytes();
			
			System.out.printf("echo reply: '%s'\n", new String( echo ) );
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

}
