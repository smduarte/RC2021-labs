package tcp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileSenderServer {
	static final int BUF_SIZE = 1024;

	public static int PORT = 8000;

	public static String readLine(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		
		// TODO
		
		return sb.toString();
	}

	static void receiveFile(Socket cs) throws IOException {

		// TODO
		
	}

	public static void main(String[] args) {

		try (ServerSocket ss = new ServerSocket(PORT)) {
			for (;;) {
				try (Socket cs = ss.accept()) {

					receiveFile(cs);

				} catch (IOException x) {
					x.printStackTrace();
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
	}
}
