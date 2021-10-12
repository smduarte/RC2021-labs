package tcp;

import java.io.*;
import java.net.*;

public class FileSenderClient {


	private static final byte NEWLINE = '\n';
	private static final int BUF_SIZE = 1024;

	static void sendFile(String server, int port, String filename) {

		try (Socket socket = new Socket(server, port); FileInputStream fis = new FileInputStream(filename)) {

			OutputStream os = socket.getOutputStream();
			
			os.write( filename.getBytes());
			os.write( NEWLINE );

			int n;
			byte[] buf = new byte[BUF_SIZE];
			while ((n = fis.read(buf)) > 0)
				os.write(buf, 0, n);
			
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("usage: <server> <port> <filename>");
			System.exit(0);
		}

		String host = args[0];
		String filename = args[2];
		int port = Integer.valueOf(args[1]);

		sendFile(host, port, filename);
	}
}
