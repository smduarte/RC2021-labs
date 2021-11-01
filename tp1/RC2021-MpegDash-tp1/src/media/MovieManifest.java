package media;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class for modelling the manifest data of a movie.
 * 
 * @author smduarte
 *
 */
public class MovieManifest {

	/**
	 * Stores the parsed information of movie manifest file
	 *
	 */
	public static record Manifest(String name, List<Track> tracks) {
	}
	
	/**
	 * 
	 * Stores the information of a movie track
	 *
	 */
	public static record Track(String filename, String contentType, int avgBandwidth, int segmentDuration, List<Segment> segments) {
	}

	/**
	 * 
	 * Stores the information of a movie track segment
	 * 
	 */
	public static record Segment(int offset, int length) {
	}

	/**
	 * 
	 * Stores the encoded byte data of a movie segment
	 * 
	 */
	public static record SegmentContent(String contentType, byte[] data) {
	}

	/**
	 * Parses a manifest file
	 * @param manifest - the text contents of a manifest file
	 * @return parsed manifest file
	 */
	public static Manifest parse(String manifest) {

			try (var sc = new Scanner(manifest)) {
				var movie = sc.nextLine();
				var numTracks = Integer.valueOf(sc.nextLine());
				var tracks = new ArrayList<Track>();
				for (int t = 0; t < numTracks; t++) {
					var filename = sc.nextLine();
					var contentType = sc.nextLine();
					var avgBandwidth = Integer.valueOf(sc.nextLine());
					var duration = Integer.valueOf(sc.nextLine());
					var numSegments = Integer.valueOf(sc.nextLine());

					var segments = new ArrayList<Segment>();
					for (var s = 0; s < numSegments; s++) {
						var parts = sc.nextLine().split(" ");
						int offset = Integer.valueOf(parts[0]);
						int length = Integer.valueOf(parts[1]);
						segments.add(new Segment(offset, length));
					}
					tracks.add(new Track(filename, contentType, avgBandwidth, duration, segments));					
				}
				return new Manifest(movie, tracks);
			} catch( Exception x ) {
				x.printStackTrace();
				return null;
			}
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		var manifest = parse(EXAMPLE_MANIFEST);
		System.out.println( manifest );
	}
	
	
	private static String EXAMPLE_MANIFEST = 
"""
coco
2
coco-1.mp4
video/mp4; codecs="avc1.42C015, mp4a.40.2"
593614
3003
3
0 1237
1237 270231
271468 257957
coco-2.mp4
video/mp4; codecs="avc1.42C015, mp4a.40.2"
983096
3003
3
0 1237
1237 407599
408836 393808			
""";
}
