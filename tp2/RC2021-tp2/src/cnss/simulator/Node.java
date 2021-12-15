package cnss.simulator;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import cnss.simulator.Event.EventType;
import cnss.simulator.Packet.PacketType;


/**
 * The <code>Node</code> class represents a node in the network. Each node
 * references its own control algorithm class, whose name was provided in the
 * constructor. A node looks up the class from its name and instantiates it
 * using java reflection. The same applies for the application code the node
 * executes.
 * 
 * @author System's team of the Department of Informatics of FCT/UNL based on a
 * @author preliminary version by Adam Greenhalgh of UCL
 * @version 1.0, September 2021
 */
public class Node {

	static final int LOCAL = -1; // the number of the virtual loop back interface
	static final int UNKNOWN = -2; // an unknown interface 
	// its use means "I do not know how to route, drop the packet"

	// The node specific state
	private int node_id;
	private int now; // the current virtual now
	private int num_interfaces;
	private Link[] links;
	private String control_class_name;
	private String application_class_name;
	private ControlAlgorithm control_alg;
	private ApplicationAlgorithm app_alg;
	private String[] args;
	private boolean traceForwarding;

	// Events and global variables
	private Queue<Event> inputEvents = new LinkedList<>();
	private Queue<Event> outputEvents = new LinkedList<>();
	private GlobalParameters parameters;
	
	// Time when next timeout or clock tick events should be triggered
	private int next_app_timeout = 0;
	private int next_control_timeout = 0;
	private int next_app_clock_tick = 0;
	private int next_control_clock_tick = 0;
	
	private int app_clock_tick_period = 0;
	private int control_clock_tick_period = 0;

	// Packet counters
	private int[] counter = new int[4];
	private int SENT = 0;
	private int RECV = 1;
	private int DROP = 2;
	private int FORW = 3;

	private int packet_counter = 0; // allows the generation of sequence numbers

	/**
	 * <code>Node</code> constructor takes the node id, the number of interfaces,
	 * the class name of the control algorithm to load as well as the class name of
	 * the application algorithm. If the control algorithm class is not found,
	 * prints an exception. If the application algorithm class is not found, prints
	 * an exception.
	 * 
	 * @param i  node id
	 * @param n  number of interfaces
	 * @param c  class name of the control algorithm to load.
	 * @param a  class name of the application algorithm to load.
	 * @param gp a reference to the global parameters collection
	 */
	public Node(int i, int n, String c, String a, String[] ags, GlobalParameters gp) {
		node_id = i;
		num_interfaces = n;
		links = new Link[n];
		now = 0;
		control_class_name = c;
		application_class_name = a;
		args = ags;
		parameters = gp;

		try {
			control_alg = (ControlAlgorithm) (Class.forName(control_class_name)).getDeclaredConstructor().newInstance();
			app_alg = (ApplicationAlgorithm) (Class.forName(application_class_name)).getDeclaredConstructor().newInstance();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		counter[SENT] = 0;
		counter[RECV] = 0;
		counter[DROP] = 0;
		counter[FORW] = 0;
		System.out.println("Created " + this);
	}

	/**
	 * Add a link to the <code>Node</code> so the node knows about its attached
	 * links. A <code>Node</code> has 0 to num_interfaces links, each connected to
	 * the <code>Link</code> links[i]. Additionally, there is a virtual interface
	 * numbered LOCAL, the local interface. It is only used as the sending interface
	 * when the node sends a packet to itself.
	 * 
	 * @param l the link to attach.
	 */
	public void addLinks(Link l) {
		int i1 = l.getInterface(1);
		int n1 = l.getNode(1);
		int i2 = l.getInterface(2);
		int n2 = l.getNode(2);
		System.out.println("Added link to node " + node_id + " - " + l);
		if (n1 == node_id) {
			links[i1] = l;
		} else if (n2 == node_id) {
			links[i2] = l;
		}
	}

	/**
	 * This <code>Node</code> starts by initializing the control and application objects
	 */
	public void initialize() {
		now = 0; // it is redundant, but ....
		next_app_timeout = -1;
		next_control_timeout = -1;
		control_clock_tick_period = control_alg.initialise(now, node_id, this, 
				parameters, links, num_interfaces);
		app_clock_tick_period = app_alg.initialise(now, node_id, this, args);
		if (control_clock_tick_period > 0) {
			next_control_clock_tick = control_clock_tick_period;
			outputEvents.add(new Event(EventType.CLOCK_INTERRUPT, 
					next_control_clock_tick, 0, null, null, node_id, 0));
		}
		if (app_clock_tick_period > 0) {
			next_app_clock_tick = app_clock_tick_period;
			outputEvents.add(new Event(EventType.CLOCK_INTERRUPT, 
					next_app_clock_tick, 0, null, null, node_id, 0));
		}
		traceForwarding = parameters.containsKey("trace_forwarding");
	}

	/**
	 * Adds an event to the <code>Node</code> input queue so the node will treat it
	 * in the next execution step.
	 * 
	 * @param e the event to add.
	 */
	public void addInputEvent(Event e) {
		inputEvents.add(e);
	}

	/**
	 * Returns an output <code>Event</code> generated by the execution step to be
	 * treated by the main loop of the simulator
	 * 
	 * @return Event, the event
	 */
	public Event getOutputEvent() {
		return outputEvents.poll();
	}

	/**
	 * Generic toString method
	 * 
	 * @return String
	 */
	public String toString() {
		return "Node " + node_id + ": " + num_interfaces + " interf.s, ctr code: " 
				+ control_class_name + " app code: " + application_class_name;

	}

	/**
	 * Dumps the control (routing | forward) table to stdout
	 * 
	 * @param now the current virtual time
	 */
	public void dumpRoutingTable(int now) {
		control_alg.showRoutingTable(now);
	}

	/**
	 * Dumps control state to stdout
	 * 
	 * @param now the current virtual time
	 */
	public void dumpControlState(int now) {
		control_alg.showControlState(now);
	}

	/**
	 * Dumps application state to stdout
	 * 
	 * @param now the current virtual time
	 */
	public void dumpAppState(int now) {
		app_alg.showState(now);
	}

	/**
	 * Dumps packet Stats to stdout for both the router and each link. s : sent , r :
	 * recv , d : drop , f : forw
	 * 
	 * @param now the current virtual time
	 */
	public void dumpPacketStats(int now) {
		String s = "\nPkt stats for node " + node_id + " time "+now+" - ";
		s = s + " s " + counter[SENT];
		s = s + " r " + counter[RECV];
		s = s + " d " + counter[DROP];
		s = s + " f " + counter[FORW];
		s = s + "\n";
		for (int i = 0; i < links.length; i++) {
			s = s + links[i].dumpPacketStats() + "\n";
		}
		System.out.print(s);
	}


	
	/******************************************************************************
	 * 
	 * Methods for processing events scheduled by the simulator for this node
	 * 
	 ******************************************************************************/
	

	
	/**
	 * Process a tracing <code>Packet</code> to be forwarded
	 * 
	 * @param p the <code>Packet</code> to be dropped
	 * @param ev the <code>Event</code> containing that packet
	 * @param now the current time stamp
	 */
	private void process_tracing_packet_to_forward (Packet p, Event ev, int now) {
		String tmp;
		if ( p.getSource() == node_id && p.getTtl() == Packet.INITIALTTL-1) {
			// it is a tracing packet being sent now
			counter[SENT]++;
			ev.setInterface(LOCAL);
			// set the sequence number now
			packet_counter++;
			p.setSequenceNumber(packet_counter);
			tmp = " "+node_id;
			System.out.println("--> node "+node_id+" time "+now+" traceroute to: "+p.getDestination());
		}
		else tmp = new String(p.getPayload(), StandardCharsets.UTF_8)+" -> "+node_id;
		p.setPayload(tmp.getBytes());
	}
	
	
	
	/**
	 * Process a DELIVER_PACKET <code>Event</code> 
	 * 
	 * @param ev the event to be processed
	 * @param now the current time stamp
	 */
	private void process_deliver_packet_event (Event ev, int now) {

		// TODO: interface LOCAL is just a NAME and does not exists
		if(ev.getInterface()>= 0) links[ev.getInterface()].countsReceivedPacket(node_id);
		Packet p = ev.getPacket();	
		if (p.getDestination() == node_id || p.getDestination() == Packet.ONEHOP) { // local packet
			counter[RECV]++;
			if (p.getType() == PacketType.DATA) {
				next_app_timeout = 0; // cancels all waiting timeouts
				app_alg.on_receive(now, p.toDataPacket()); // delivers an exact copy of the packet
			} else if (p.getType() == PacketType.CONTROL) {
				next_control_timeout = 0; // cancels all waiting timeouts
				control_alg.on_receive(now, p, ev.getInterface());
			} else if (p.getType() == PacketType.TRACING) {
				// make the result of the tracing available
				System.out.println("--> node "+node_id+" time "+now+" received traceroute: " 
				+ new String(p.getPayload(), StandardCharsets.UTF_8)+ " -> "+node_id);
			} else {
				panic("--> node "+node_id+" at "+now
						+" process_deliver_packet: unknown packet type " + p);
			}
		} else { // forward it
			p.decrementTtl();
			if (p.getTtl() == 0) {
				process_packet_to_drop(p,now);
				return; 
			}
			if (p.getType() == PacketType.TRACING) process_tracing_packet_to_forward (p, ev, now);	
			counter[FORW]++;
			control_alg.forward_packet(now, p, ev.getInterface());
		}
	}

	
	/**
	 * Finalizes dropping a <code>Packet</code> when TTL its reaches 0
	 * 
	 * @param p the packet to be dropped
	 * @param now the current time stamp
	 */
	private void process_packet_to_drop (Packet p, int now) {
		String message = new String("--> node "+node_id+" at "+now+" dropping expired ");
		if (p.getType() == PacketType.DATA) message += "packet "+p;
		else if (p.getType() == PacketType.CONTROL) message += "packet "+p;
		else if (p.getType() == PacketType.TRACING) 
			message += "trace route packet "+new String(p.getPayload(), StandardCharsets.UTF_8);
		System.out.println(message);
	}
	

	/**
	 * Process the <code>Event</code>s scheduled by the simulator for this
	 * processing step
	 * 
	 * @param n the current time stamp
	 */
	public void process_input_events(int n) {
		now = n;
		while (inputEvents.size() > 0) {
			Event ev = inputEvents.poll(); // gets the head of the events queue and removes it
			if (ev.getTime() != now) {
				panic("--> node "+node_id+" at "+now+" process_events: event out of order " + ev);
			}
			if (ev.getOperation() == EventType.UPLINK) {
				System.out.println("--> node "+node_id+" at "+
					now+" interface "+ev.getInterface()+" going up");
					control_alg.on_link_up(now, ev.getInterface());
			}
			else if (ev.getOperation() == EventType.DOWNLINK) {
				System.out.println("--> node "+node_id+" at "+
					now+" interface "+ev.getInterface()+" going down");
					control_alg.on_link_down(now, ev.getInterface());
			}
			else if (ev.getOperation() == EventType.DELIVER_PACKET) {
				process_deliver_packet_event (ev, now);
			}
			else if ( ev.getOperation() == EventType.CLOCK_INTERRUPT ) {
				// Clock interrupt events will be processed after all other type of events.
				// System.out.println("node process_events: clock interrupt event " + ev);
				// Here we do nothing since clock interrupts arise when the next clock tick
				// time is equal to now. See its processing below.
				// Several of these interrupts will be only once treated.
				// A call to set a timeout, generates an event and sets next clock tick
				// variables. Therefore, it cancels all previous timeout calls.
				// See set_timeout() and set_control_timeou() down calls below.
				// The reception of packets, cancels all timeouts. See 
				// process_deliver_packet() above.
			}
			else {
				panic("--> node process_events: unknown event " + ev);
			}
		}
		
		// all events are processed, now process CLOCK INTERRUPTS
		if ( next_control_clock_tick == now) {
			control_alg.on_clock_tick(now);
			next_control_clock_tick = now + control_clock_tick_period;
			outputEvents.add(new Event(EventType.CLOCK_INTERRUPT, 
					next_control_clock_tick, 0, null, null, node_id, 0));
		}
		if ( next_app_clock_tick == now) {
			app_alg.on_clock_tick(now);
			next_app_clock_tick = now + app_clock_tick_period;
			outputEvents.add(new Event(EventType.CLOCK_INTERRUPT, 
					next_app_clock_tick, 0, null, null, node_id, 0));
		}
		if ( next_control_timeout == now) control_alg.on_timeout(now);
		if ( next_app_timeout == now) app_alg.on_timeout(now);
	}

	/***************************************************************************
	 * 
	 * Auxiliary methods for impossible to proceed situations
	 * 
	 ***************************************************************************/

	/**
	 * Aborts the execution in a situation of panic
	 * 
	 * @param message to be printed before Simulator panic abortion
	 */
	private void panic (String message) {
		System.err.println("system panic situation: "+message);
		System.exit(-1);
	}
	
	/**
	 * Aborts the execution in a situation of panic
	 * 
	 * @param message to be printed before Simulator panic abortion
	 */
	private void down_call_panic (String message) {
		System.err.println("down call bad usage panic situation: "+message);
		System.exit(-1);
	}
	
	/***************************************************************************
	 * 
	 * Algorithms down calls
	 * 
	 ***************************************************************************/

	/**
	 * Gets this <code>Node</code> id
	 * 
	 * @return int
	 */
	public int getId() {
		return node_id;
	}
	
	/**
	 * Sends an application <code>DataPacket</code>; by convention the input
	 * interface of a locally (created and) sent packet is the LOCAL interface (-1)
	 * this method is to be be used by the application algorithm
	 * 
	 * @param p the packet
	 */
	public void send(DataPacket p) {
		// all packets sent locally are counted as sent and forwarded
		if (p == null) down_call_panic("send: no packet to send");
		if (p.getSource() != node_id) down_call_panic("send: can only send locally originated packets");
		if (p.getType() != PacketType.DATA) down_call_panic("send: can only send data packets");
		// counter[SENT] will be incremented after the packet is forwarded
		counter[FORW]++;
		control_alg.forward_packet(now, p, LOCAL);
	}

	/**
	 * Sends a <code>Packet</code> using a given interface. Besides increasing
	 * counters, this could be done by Control Algorithms. It is however, cleaner and
	 * better practice to make it available here to be shared by all different
	 * ControlAlgorithms
	 * 
	 * @param p     the packet
	 * @param iface the interface
	 */
	public void send(Packet p, int iface) {
		if (p == null) down_call_panic("control_send: no packet to send");
		if (iface == UNKNOWN || iface >= num_interfaces) {
			// increase drop counter and drop the packet since it is impossible to send it
			counter[DROP]++;
			if ( traceForwarding ) System.out.println("node "+node_id+" time "
					+now+" packet sent to UNKNOWN "+p);
		}
		else if (p.getDestination() == node_id) {
			// locally forwarded or sent directly to the node itself
			Event ev = new Event(EventType.DELIVER_PACKET, now + 1, 0, null, p, node_id, LOCAL);
			outputEvents.add(ev);
			counter[SENT]++;
		}	
		else {
			links[iface].enqueuePacket(node_id, p); // the link side is relative to the node calling it
			counter[SENT]++;
			if ( traceForwarding ) System.out.println("node "+node_id+" time "
					+now+" forwarded packet "+p);
		}
	}
	
	
	/**
	 * This supports an alternative method of dropping a packet while it is
	 * being processed by the forward() up call.
	 * In fact, this packet may be simply ignored. However, its dropping must be counted.
	 * This method allows exactly that. Sending to UNKNOWN or counting drops 
	 * are equivalent and one can use one or the other, but not both.
	 * 
	 */
	public void countDroppedPacket() {
		counter[DROP]++;
	}


	/**
	 * Installs an application timeout. The reception of a data message before or at
	 * now+t cancels all application timeouts (including those to be delivered in the same time step)
	 * 
	 * @param t the timeout value
	 */
	public void set_timeout(int t) {
		if (t < 1) down_call_panic("set_app_timeout: timeout value must be >= 1");
		next_app_timeout = now + t; // next expected control timeout
		outputEvents.add(new Event(EventType.CLOCK_INTERRUPT, next_app_timeout, 0, null, null, node_id, 0));
	}

	/**
	 * Installs a control timeout. The reception of a data message before or at now+t
	 * cancels all control timeouts (including those to be delivered in the same time step)
	 * 
	 * @param t the timeout value
	 */
	public void set_control_timeout(int t) {
		if (t < 1) down_call_panic("set_control_timeout: timeout value must be >= 1");
		next_control_timeout = now + t; // next expected app timeout
		outputEvents.add(new Event(EventType.CLOCK_INTERRUPT, next_control_timeout, 0, null, null, node_id, 0));
	}

	/**
	 * Creates a data packet with the current node as sender.
	 * ApplicationAlgorithm could implement the same functionality
	 * but setting an unique sequence number
	 * 
	 * @param receiver the receiver id
	 * @param payload  the payload of the packet
	 * @return the created data packet
	 */
	public DataPacket createDataPacket(int receiver, byte[] payload) {
		DataPacket p = new DataPacket(node_id, receiver, payload);
		packet_counter++;
		p.setSequenceNumber(packet_counter);
		return p;
	}

	/**
	 * Creates a control packet with the current node as sender.
	 * ControlAlgorithm could implement the same functionality
	 * but setting an unique sequence number
	 * 
	 * @param sender   the sender id
	 * @param receiver the receiver id
	 * @param payload  the payload of the packet
	 * @return the created data packet
	 */
	public ControlPacket createControlPacket(int sender, int receiver, byte[] payload) {
		ControlPacket p = new ControlPacket(sender, receiver, payload);
		packet_counter++;
		p.setSequenceNumber(packet_counter);
		return p;
	}

	/**
	 * Returns the interface state for the specified interface - is it up or down?
	 * 
	 * @param iface
	 * @return boolean (true if is Up)
	 */
	public boolean getInterfaceState(int iface) {
		// If the interface value is -1 (LOCAL), it is the virtual loop back interface, so
		// it is always up.
		if (iface == LOCAL) return true;
		return links[iface].isUp();
	}
	

}
