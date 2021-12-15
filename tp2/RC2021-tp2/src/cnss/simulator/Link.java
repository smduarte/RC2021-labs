package cnss.simulator;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import cnss.simulator.Event.EventType;
import cnss.simulator.Packet.PacketType;

/**
 * A <code>Link</code> class that represents a link between two nodes. It
 * contains two out bound packet queues, one for each side
 * of the link. Sides of the link are noted side 1 and side 2.
 * Transmitted packets are kepts in the global event queue up to their
 * delivery.
 * 
 * @author José Legatheaux of DI - NOVA Science and Technology - Portugal, based on a
 * @author preliminary version by Adam Greenhalgh of UCL
 * @version 2.0, September 2021
 */

public class Link {
	// there are two sides: side 1 and side 2, each with a node
	// and an interface and two queues with counters
	private int node1;
	private int node2;
	private int iface1;
	private int iface2;

	private int counter1_in = 0;
	private int counter2_in = 0;
	private int counter1_out = 0;
	private int counter2_out = 0;

//	private Queue<Packet> in1 = new LinkedList<>();
//	private Queue<Packet> in2 = new LinkedList<>();
	private Queue<Packet> out1 = new LinkedList<>();
	private Queue<Packet> out2 = new LinkedList<>();

	// During a processing step, packets may be sent. Before proceeding
	// to the next one, these packets are processed and enqueued in the global
	// queue to be delivered in due time, i.e. taking in consideration the
	// previous ones transmission time.
	
	private int timeOfLastBitTransmitted1 = 0;
	private int timeOfLastBitTransmitted2 = 0;

	// the queue of events containing the packets to be delivered after
	// the call of transmitPackets method
	private Queue<Event> outputEvents = new LinkedList<Event>();

	private long bwidth = 1000; // in bits per second - bps
	private int latency = 0; // in ms
	private double errors = 0.0; // error rate in % - 0.0 is a perfect (no errors) link
	private double jitter = 0.0; // in % - 0.0 is a link without jitter
	private boolean up = true;
	private int max_queue = 1000000; // max size of link's queues == endless

	private Random randomDrop = null;
	private Random randomJitt = null;

	Simulator simulator; // the simulator where this link leaves.
	// required to allow a link to create events of packet delivery to
	// the other extreme of the link

	/*
	 * Constructor that takes routers id and interfaces id for both ends of the
	 * link. as well as the other required parameters
	 * 
	 * @param n1 node 1's id
	 * 
	 * @param i1 node 1's interface
	 * 
	 * @param n2 node 2's id
	 * 
	 * @param i2 node 2's interface
	 * 
	 * @param bd bandwidth of the link in bps
	 * 
	 * @param lat latency of the link in ms
	 * 
	 * @param errs link error rate in %
	 * 
	 * @param j link jitter in %
	 */
	public Link(int n1, int i1, int n2, int i2, long bd, int lat, double errs, double j, Simulator s) {
		node1 = n1;
		node2 = n2;
		iface1 = i1;
		iface2 = i2;
		bwidth = bd;
		latency = lat;
		errors = errs;
		jitter = j;
		up = true;
		simulator = s;

		if ( errs > 0.0001 ) randomDrop = new Random(10000+n1+n2+i1+i2);
		if ( jitter > 0.0001 ) randomJitt = new Random(20000+n1+n2+i1+i2);
		
		// if a different max_queue has been defined
		if (s.parameters().containsKey("max_queue")) {		
			max_queue = Integer.valueOf(s.parameters().get("max_queue"));	
		};
	}

	/**
	 * Get the node attached to a particular side of the link, 1 specifies side 1
	 * and 2 the other side.
	 * 
	 * @param side which end of the link (1, 2)
	 * @return the node id.
	 */
	public int getNode(int side) {
		if (side == 1)
			return node1;
		else
			return node2;
	}

	/**
	 * Get the interface attached to a particular side of the link, 1 specifies side
	 * 1 and 2 the other side.
	 * 
	 * @param end which side of the link (1, 2)
	 * @return the interface id.
	 */
	public int getInterface(int side) {
		if (side == 1)
			return iface1;
		else
			return iface2;
	}

	/**
	 * Is the link up or down.
	 * 
	 * @return showing the links status.
	 */
	public boolean isUp() {
		return up;
	}

	/**
	 * Sets the link status.
	 * 
	 * @param s setting the links status.
	 */
	public void setState(boolean s) {
		up = s;
		if (!up) {
			// the link is down, output queues should be reset
			out1.clear();
			out2.clear();
		}
	}

	/**
	 * If the link is up, moves packets from the out queue of one end to the global
	 * event queue by creating DELIVER events of the packets associated
	 * with the other side of the link
	 * 
	 * @param now is the current time
	 */
	public void transmitPackets(int now) {
		if (isUp()) {
			// begin by side 1 of the link
			// packets will begin being transmitted now or when previous packets are done
			if ( timeOfLastBitTransmitted1 < now ) timeOfLastBitTransmitted1 = now;
			while (out1.size() > 0) {
				Packet p = out1.poll(); // retrieves the packet from the queue
				if ( randomDrop != null ) {
					if ( randomDrop.nextInt(10000) <= (int)(errors*10000) ) continue;
				}
				ProcessNextPacket1(p,now);
			}
			// now side 2
			if ( timeOfLastBitTransmitted2 < now ) timeOfLastBitTransmitted2 = now;
			while (out2.size() > 0) {
				Packet p = out2.poll(); // retrieves the packet from the queue
				if ( randomDrop != null ) {
					if ( randomDrop.nextInt(10000) <= (int)(errors*10000) ) continue;
				}
				ProcessNextPacket2(p,now);
			}
		} else {
			// the link is down, output queues should be reset if not yet
			out1.clear();
			out2.clear();
		}
		if (out1.size() != 0 || out2.size() != 0) {
			System.out.println("TransmitPackets ends with non empty ouptput queues");
			System.exit(-1);
		}
	}

	/**
	 * Processes one packet sent from side 1 of the link
	 * 
	 * @param p the packet to be processed
	 * @param now is the current time	
	 */
	void ProcessNextPacket1 (Packet p, int now) {
		
		// TODO: is it necessary to get the tracing done here? By the moment is done by the node.
//		if (p.getType() == PacketType.TRACING) {
//			// add the link crossed to the path - time is when the packet will start being transmitted
//			String trace = new String(p.getPayload(), StandardCharsets.UTF_8)+ " time " 
//					+ timeOfLastBitTransmitted1 + " " + node1 + "." + iface1 + " -> " + node2 + "." + iface2;
//			p.setPayload(trace.getBytes());
//		}
		
		double transmissionTime = ((double) p.getSize()) * 8.0 * 1000.0 / (double) bwidth; // all in ms
		double varLat = 0.0;
		if ( randomJitt != null ) varLat = (double) randomJitt.nextInt(10000)/10000 * jitter * transmissionTime;
		int transitTime = (int) transmissionTime + latency + (int) varLat;
		// transitTime must be at least 1 to force the transmission in a future processing step
		if (transitTime < 1) transitTime = 1;
		// System.out.println("TransmitPackets computed "+transitTime+" ms");
		int deliverTime = timeOfLastBitTransmitted1+transitTime;
		if ( counter1_out - counter2_in > max_queue ) { // queue is full
			String message = new String("--> node "+node2+" at "+now+" dropping packet due to full queue ");
			System.out.println(message);
			return; 
		}
		timeOfLastBitTransmitted1 += (int) transmissionTime;
		outputEvents.add(new Event(EventType.DELIVER_PACKET, deliverTime, 0, null, p, node2, iface2));
	}


	/**
	 * Processes one packet sent from side 2 of the link
	 * 
	 * @param p the packet to be processed
	 * @param now is the current time	
	 */
	void ProcessNextPacket2 (Packet p, int now) {
		// TODO: the  same as above
//		if (p.getType() == PacketType.TRACING) {
//			// add the link crossed to the path - time is when the packet will start being transmitted
//			String trace = p.getPayload().toString() + "time " 
//					+ timeOfLastBitTransmitted2 + " " + node2 + "." + iface2 + "->" + node1 + "." + iface1;
//			p.setPayload(trace.getBytes());
//		}
		
		double transmissionTime = ((double) p.getSize()) * 8.0 * 1000.0 / (double) bwidth; // all in ms
		double varLat = 0.0;
		if ( randomJitt != null ) varLat = (double) randomJitt.nextInt(10000)/10000 * jitter * transmissionTime;
		int transitTime = (int) transmissionTime + latency + (int) varLat;
		// transitTime must be at least 1 to force the transmission in a future processing step
		if (transitTime < 1) transitTime = 1;
		// System.out.println("TransmitPackets computed "+transitTime+" ms");
		int deliverTime = timeOfLastBitTransmitted2+transitTime;
		if ( counter2_out - counter1_in > max_queue ) { // queue is full
			String message = new String("--> node "+node1+" at "+now+" dropping packet due to full queue ");
			System.out.println(message);
			return;
		}
		timeOfLastBitTransmitted2 += (int) transmissionTime;
		outputEvents.add(new Event(EventType.DELIVER_PACKET, deliverTime, 0, null, p, node1, iface1));
	}


	/**
	 * Return an output <code>Event</code> generated by the transmission of packets
	 * to be later treated by the main loop of the simulator
	 * 
	 * @return Event, the event
	 */
	public Event getOutputEvent() {
		return outputEvents.poll();
	}

	/**
	 * Places the <code>Packet</code> p, in the out bound queue for the node
	 * specified by node id. Increments output counters.
	 * 
	 * @param nodeid the router whose out bound queue to place the packet in.
	 * @param p      the packet being sent.
	 */
	public void enqueuePacket(int nodeid, Packet p) {
		if (!up)
			return;
		if (nodeid == node1) {
			out1.add(p);
			counter1_out++;
		} else {
			out2.add(p);
			counter2_out++;
		}
	}
	
	
	/**
	 * Counts the number of receives packets by a node connected to this
	 * link at packet's delivery time
	 * 
	 * @param nodeid the router whose out bound queue to place the packet in.
	 */
	public void countsReceivedPacket(int nodeid) {
		if (!up)
			return;
		if (nodeid == node1) {
			counter1_in++;
		} else {
			counter2_in++;
		}
	}

	/**
	 * Returns the link bandwidth
	 * 
	 * @return the link bandwidth
	 */
	public long getBandWidth() {
		return bwidth;
	}

	/**
	 * Returns the link latency
	 * 
	 * @return the link latency
	 */
	public int getLatency() {
		return latency;
	}


	/**
	 * Generic to string method
	 * 
	 * @return string representation
	 */
	public String toString() {
		String state = up ? "up" : "down";
		String s = "Link (Node1:" + node1 + " I1:" + iface1 + ")";
		s = s + "<-->";
		s = s + "(Node2:" + node2 + " I2:" + iface2 + ") bwd: " + bwidth + " bps lat: " + latency + " ms error: " + errors + " jit: " + jitter + " "
				+ state;
		return s;
	}

	/**
	 * Returns the packet counters for this link.
	 * 
	 * @return string representation of packet counters.
	 */
	public String dumpPacketStats() {
		String s = up ? " u " : " d ";
		s += "(node:" + node1 + " ifc:" + iface1 + ")";
		s += " r " + counter1_in + " s " + counter1_out;
		s += " <-->";
		s += " (node:" + node2 + " ifc:" + iface2 + ")";
		s += " r " + counter2_in + " s " + counter2_out;
		return s;
	}

}
