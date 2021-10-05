package udp;

import java.io.*;
import java.net.*;

public class EchoServer {
	static final int MAX_DATAGRAM_SIZE = 65536;

	public static int PORT = 8000;

	public static void main(String[] args) {

		try (DatagramSocket socket = new DatagramSocket(PORT)) {
			for (;;) {
				byte[] buffer = new byte[MAX_DATAGRAM_SIZE];
				DatagramPacket echoRequest = new DatagramPacket(buffer, buffer.length);

				socket.receive(echoRequest);
				System.out.printf( "Got request from: %s", echoRequest.getSocketAddress() );
				
				DatagramPacket reply = new DatagramPacket(echoRequest.getData(), echoRequest.getLength(),
						echoRequest.getSocketAddress());

				socket.send(reply);
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

	}
}
