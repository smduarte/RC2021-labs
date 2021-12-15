package cnss.simulator;

/**
 * The <code>ApplicationAlgorithm</code> interface should be implemented by any
 * class whose instances are intended to implement the application running in
 * nodes. The class that implements this algorithm must have a zero argument
 * constructor.
 *
 * @author José Legatheaux of DI - NOVA Science and Technology - Portugal
 * @version 1.0, September 2020
 */
public interface ApplicationAlgorithm {

	/**
	 * Initializes the application algorithm and returns the required
	 * app_clock_tick_period. If app_clock_tick_period == 0, no clock_ticks will be
	 * submitted.
	 * 
	 * @param now     the current virtual clock value, always 0 during
	 *                initialization
	 * @param id      this node id
	 * @param nodeObj a reference to the node object executing this algorithm
	 * @param atgs    the arguments of the application algorithm
	 * @return the requested clock_tick_period
	 */
	public int initialise(int now, int node_id, Node nodeObj, String[] args);

	/**
	 * A periodic clock interrupt.
	 * 
	 * @param now the current virtual clock value
	 */
	public void on_clock_tick(int now);

	/**
	 * Signals a timeout event
	 * 
	 * @param now the current virtual clock value
	 */
	public void on_timeout(int now);

	/**
	 * Given an application packet from another node, process it
	 * 
	 * @param now the current virtual clock value
	 * @param p   the packet received
	 */
	public void on_receive(int now, DataPacket p);

	/**
	 * Prints application state table(s) to the screen in a previously agreed
	 * format.
	 */
	public void showState(int now);

}
