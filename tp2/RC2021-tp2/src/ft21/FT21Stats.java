package ft21;
import java.util.HashMap;
import java.util.Map;

/*
 * DO NOT EDIT
 */
public class FT21Stats {
	static String TRAFFIC = "bytes";
	
	private int startTime;
	public int timeoutEvents;

	public final Tally rtt;
	public final Tally timeout;
	public final Counters in, out;
	
	public FT21Stats(int now) {
		startTime = now;
		in = new Counters(); 
		out = new Counters();
		rtt = new Tally("RTT");
		timeout = new Tally("Timeout");
		timeoutEvents = 0;
	}

	public void printReport( int now) {
		double elapsed = now - startTime;

		System.out.println("COUNTERS:");
		System.out.print("Inbound: ");
		System.out.println( in.values );
		System.out.print("Outbound: ");
		System.out.println( out.values );
		System.out.println("------------------------------------");
		System.out.println("RTT/Timeout Stats:");
		System.out.println( rtt );
		System.out.println( timeout );
		System.out.println( "timeout events: " + timeoutEvents);
		
		System.out.println("------------------------------------");		
		System.out.println("Transfer Rates:");
		double in_speed = 8 * in.values.get(TRAFFIC) / elapsed;
		double out_speed = 8 * out.values.get(TRAFFIC) / elapsed;
		
		System.out.printf("Inbound mean transfer rate: %3.2f Kbit/s\n", in_speed );
		System.out.printf("Outbound mean transfer rate: %3.2f Kbit/s\n", out_speed );
		
	}

	public class Counters {
		private Map<String, Integer> values = new HashMap<>();
		
		public void increment(String counter, int value) {
			values.compute(counter, (k,v) -> v == null ? value : v + value);
		}
	}
	
	public class Tally {
		final String name;
		private long min, sum, max, tot;
		
		Tally(String name) {
			this.name = name;
			min = Long.MAX_VALUE;
			max = Long.MIN_VALUE;
			sum = tot = 0;
		}
		
		public void tally( long val ) {
			min = val < min ? val : min;
			max = val > max ? val : max;
			sum += val;
			tot++;
		}

		public String toString() {
			if( tot > 0 )
				return String.format("%s (min: %d / avg: %.2f / max: %d / #samples: %d)", name, min, sum/(double)tot, max, tot);
			else  
				return String.format("%s (max/avg/min/#samples) : ?, ?, ?, ?", name);
		}
	}
}