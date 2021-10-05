package udp;

import java.io.*;
import java.net.*;

public class EchoClient {

	static final int MAX_DATAGRAM_SIZE = 65536;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("usage: <server> <port> <msg>");
			System.exit(0);
		}

		String host = args[0];
		String message = args[2];
		int port = Integer.valueOf(args[1]);

		InetSocketAddress server = new InetSocketAddress(host, port);

		try (DatagramSocket socket = new DatagramSocket()) {
			byte[] payload = message.getBytes();
			DatagramPacket echoRequest = new DatagramPacket(payload, payload.length, server);

			socket.send(echoRequest);

			byte[] buffer = new byte[MAX_DATAGRAM_SIZE];
			DatagramPacket echoReply = new DatagramPacket(buffer, buffer.length);
			socket.receive(echoReply);

			String reply = new String(echoReply.getData(), 0, echoReply.getLength());
			System.out.printf("echo reply: '%s'\n", reply);
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

}
