package cnss.simulator;

/**
 * The <code>Event</code> class which represents an event to occur in the
 * simulator.
 * 
 * @author José Legatheaux of DI - NOVA Science and Technology - Portugal, based on a
 * @author preliminary version by Adam Greenhalgh of UCL
 * @version 1.0, September 2020
 */
public class Event {

	public static enum EventType {
		UNKNOWN, TRACEROUTE, UPLINK, DOWNLINK, DUMP_RT, DUMP_PACKETS, 		
		DELIVER_PACKET,
		DUMP_CONTROLSTATE, DUMP_APPSTATE, CLOCK_INTERRUPT
	}

	public static long DISPLACEMENT = 1000000; // # of max != events per processing step

	private EventType operation;
	private long uuid;
	private int time;
	private String[] args;
	// some events require the below informations, others don't
	// using a String[] to represent them is not adequate
	// TODO: this solution should be reassessed
	private Packet packet;
	private int node;
	private int itface;

	/**
	 * <code>Event</code> constructor, which takes the name and UUID of the event,
	 * the time the event is to occur, and a list of arguments to the event.
	 * 
	 * @param op the name (type) of the event
	 * @param t  the time the event is to occur
	 * @param id an unique id of the event, t<<32+id is the UUID of the event
	 * @param a  the arguments to the event.
	 */
	public Event(EventType op, int t, long id, String[] a) {
		operation = op;
		time = t;
		long longtime = (long) t;
		uuid = longtime * DISPLACEMENT + id;
		args = a;
		packet = null;
		node = 0;
		itface = 0;
		if (uuid < 0) {
			System.err.println("new event: event with negative uuid: this");
			System.exit(-1);
		}
	}

	/**
	 * <code>Event</code> constructor, which takes the name and UUID of the event,
	 * the time the event is to occur, and a list of arguments to the event.
	 * 
	 * @param op   the name (type) of the event
	 * @param t    the time the event is to occur
	 * @param id   an unique id of the event, t<<32+id is the UUID of the event
	 * @param a    the arguments to the event.
	 * @param p    a Packet associated with this event.
	 * @param n    a node associated with this event.
	 * @param itfc an interface of the above node associated with this event.
	 */
	public Event(EventType op, int t, long id, String[] a, Packet p, int n, int itfc) {
		operation = op;
		time = t;
		long longtime = (long) t;
		uuid = longtime * DISPLACEMENT + id;
		args = a;
		packet = p;
		node = n;
		itface = itfc;
		if (uuid < 0) {
			System.err.println("new event: event with negative uuid: this");
			System.exit(-1);
		}
	}

	/**
	 * Return the time this event is scheduled to occur at
	 * 
	 * @return current time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Return the UUID of this event
	 * 
	 * @return uuid of the event
	 */
	public long getUUID() {
		return uuid;
	}

	/**
	 * Sets the uuid of this event
	 * 
	 * @param uuid
	 */
	public void setUUID(long u) {
		uuid = u;
	}

	/**
	 * Get the operation set by this event.
	 * 
	 * @return name of the operation
	 */
	public EventType getOperation() {
		return operation;
	}

	/**
	 * Get the operation set by this event.
	 * 
	 * @param op the operation
	 */
	public void setOperation(EventType op) {
		operation = op;
	}

	/**
	 * Get the number of arguments this event has.
	 * 
	 * @return the number of arguments
	 */
	public int getNumberOfArgumentss() {
		return args.length;
	}

	/**
	 * Return argument i to the event which was set at the creation of the event
	 * 
	 * @param i argument i
	 * @return the argument at i
	 */
	public String getArgument(int i) {
		return args[i];
	}

	/**
	 * Return arguments of the event which was set at the creation of the event
	 * 
	 * @return the arguments
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * Gets the Packet associated with the event.
	 * 
	 * @return the Packet associated with the event.
	 */
	public Packet getPacket() {
		return packet;
	}

	/**
	 * Sets the Packet associated with the event.
	 * 
	 * @param p the Packet to be associated with the event.
	 */
	public void setPacket(Packet p) {
		packet = p;
	}

	/**
	 * Gets the node number associated with the event.
	 * 
	 * @return the node number associated with this event
	 *
	 */
	public int getNode() {
		return node;
	}

	/**
	 * Sets the node associated with the event.
	 * 
	 * @param n the node to be associated with the event.
	 */
	public void setNode(int n) {
		node = n;
	}

	/**
	 * Gets the interface number associated with the event. // * @return the
	 * interface number associated with this event.
	 */
	public int getInterface() {
		return itface;
	}

	/**
	 * Sets the interface associated with the event.
	 * 
	 * @param i the interface to be associated with the event.
	 */
	public void setInterface(int i) {
		itface = i;
	}

	/**
	 * Generic toString method which describes the event and its arguments. 
	 * TODO: by the moment, it ignores the optional field packet
	 * 
	 * @return String
	 */
	public String toString() {
		String s = "Event " + uuid + " op= " + operationString(operation) + " time " + time;
		if (args != null) {
			s = s + " with";
			for (int i = 0; i < args.length; i++) {
				s = s + " " + args[i];
			}
		}
		s = s + " (node " + node + ")";
		if ( operation == EventType.DELIVER_PACKET) {
			s = s +" interface "+itface;
			s = s +" packet "+packet;
		}
		return s;
	}


	/**
	 * Auxiliary method of toString().
	 * 
	 * @param i the operation associated with the event.
	 * @return its value as a string
	 */
	private String operationString(EventType i) {
		return i.toString();
	}

}
