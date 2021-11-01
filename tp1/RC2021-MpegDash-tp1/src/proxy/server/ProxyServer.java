package proxy.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import http.Http;
import static media.MovieManifest.SegmentContent;

/**
 * Server-side of the MpegDash proxy.
 * 
 * Accepts HTTP segments requests from the browser movie player.
 * 
 * 1) Launches a new client-side handler as needed.
 * 2) Feeds browser from a shared segment data queue.
 * 
 * @author smduarte
 */
public class ProxyServer {

	private static final int PROXY_SERVER_PORT = 1234;

	public static void start( BiFunction<String, BlockingQueue<SegmentContent>, Runnable> factory ) {
		try (var ss = new ServerSocket(PROXY_SERVER_PORT)) {
			System.err.printf("ProxyServer listening on port %s\n", ss.getLocalPort());
			for (;;) {
				try(var cs = ss.accept()) {
					System.err.println( "Handling request from:" +cs.getLocalSocketAddress());
					Player.processBrowserRequest(cs, factory);					
					System.err.println( "Done");
				} catch( Exception x ) {
					x.printStackTrace();
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
	}
}

class Player {
	private static final String CRLF = "\r\n";
	private static final String HTTP_OK = "HTTP/1.0 200 OK\r\n";
	private static final String HTTP_CORS = "Access-Control-Allow-Origin: *\r\n";
	private static final String HTTP_CONTENT_TYPE_FMT = "Content-Type: %s\r\n";
	private static final String HTTP_CONTENT_LENGTH_FMT = "Content-Length: %d\r\n";

	private static final int MAX_SEGMENTS = 2;

	private static final Object START_COMMAND = "start";
	
	final BlockingQueue<SegmentContent> queue;
	
	static void processBrowserRequest(Socket cs, BiFunction<String, BlockingQueue<SegmentContent>, Runnable> factory) throws Exception {
		InputStream is = cs.getInputStream();

		String request = Http.readLine(is);
		System.err.println(request);

		while (Http.readLine(is).length() > 0)
			;

		String path = Http.parseHttpRequest(request)[1];
		String[] pathTokens = path.split("/");
		if (pathTokens.length != 4)
			return;

		String playerId = pathTokens[1];
		String movie = pathTokens[2];
		String command = pathTokens[3];

		System.err.println( movie );

		var player = players.get(playerId);
		if (player == null && command.equals(START_COMMAND)) {
			players.put(playerId, player = new Player());
			new Thread(factory.apply(movie, player.queue)).start();
		}
		
		var segment = player.queue.take();
		var data = segment.data();
		
		var sb = new StringBuffer(HTTP_OK)
				.append( HTTP_CORS )
				.append(String.format(HTTP_CONTENT_TYPE_FMT, segment.contentType()))
				.append(String.format(HTTP_CONTENT_LENGTH_FMT, data.length))
				.append(CRLF);

		System.err.println("REPLY:" + sb.toString());
		
		cs.getOutputStream().write( sb.toString().getBytes() );
		cs.getOutputStream().write( data);	
		cs.close();
		
		if( data.length == 0)
			players.remove( playerId );
	}

	Player() {
		this.queue = new ArrayBlockingQueue<>(MAX_SEGMENTS);
	}


	private static Map<String, Player> players = new ConcurrentHashMap<>();
}