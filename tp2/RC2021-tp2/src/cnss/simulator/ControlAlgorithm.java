package cnss.simulator;

/**
 * The <code>ControlAlgorithm</code> interface should be implemented by any
 * class whose instances are intended to provide the routing algorithm. The
 * class that implements this algorithm must have a zero argument constructor.
 *
 * @author José Legatheaux of DI - NOVA Science and Technology - Portugal
 * @version 1.0, September 2021
 */
public interface ControlAlgorithm {
	static final int LOCAL = Node.LOCAL; // the number of the virtual loop back interface
	static final int UNKNOWN = Node.UNKNOWN; // an unknown interface - means do not know how
	static final int INFINITY = 60; // just a suggestion

	/**
	 * Initializes the control algorithm and returns the required
	 * control_clock_tick_period. If control_clock_tick_period == 0, no clock_ticks
	 * will be submitted; Interfaces are numbered 0 to nint-1. Each has a link
	 * attached: links[i] Interface -1 is virtual and denotes, when needed, the
	 * local loop interface.
	 * 
	 * @param now        the current virtual clock value
	 * @param id         this node id
	 * @param nodeObj    a reference to the node object executing this algorithm
	 * @param parameters the collection of global parameters
	 * @param links      the nodes links array
	 * @param nint       the number of interfaces (or links) of this node
	 * @return the requested clock_tick_period
	 */
	public int initialise(int now, int node_id, Node nodeObj, GlobalParameters parameters, Link[] links, int nint);

	/**
	 * A periodic clock interrupt.
	 * 
	 * @param now the current virtual clock value
	 */
	public void on_clock_tick(int now);

	/**
	 * Signals a link up event
	 * 
	 * @param now   the current virtual clock value
	 * @param iface interface id where this link is connected
	 */
	public void on_link_up(int now, int iface);

	/**
	 * Signals a link down event
	 * 
	 * @param now   the current virtual clock value
	 * @param iface interface id where this link is connected
	 */
	public void on_link_down(int now, int iface);

	/**
	 * Signals a timeout event
	 * 
	 * @param now the current virtual clock value
	 */
	public void on_timeout(int now);

	/**
	 * Given a control packet from another node, process it
	 * 
	 * @param now   the current virtual clock value
	 * @param p     the packet to process
	 * @param iface the interface it came in from
	 */
	public void on_receive(int now, Packet p, int iface);

	/**
	 * Given a packet from another node, forward it to the appropriate interfaces
	 * using nodeObj.send(Packet p, int iface); Packet ttl has already been
	 * decreased and controlled. If the algorithm has no solution to route this packet,
	 * send it to the UNKNOWN interface using nodeObj.send(p,UNKNOWN)
	 * 
	 * @param now   the current virtual clock value
	 * @param p     the packet to process
	 * @param iface the interface it was received by the node
	 */
	public void forward_packet(int now, Packet p, int iface);

	/**
	 * Prints control state table(s) to the screen in a previously agreed format.
	 */
	public void showControlState(int now);

	/**
	 * Prints the routing table to the screen in a previously agreed format.
	 */
	public void showRoutingTable(int now);
}
