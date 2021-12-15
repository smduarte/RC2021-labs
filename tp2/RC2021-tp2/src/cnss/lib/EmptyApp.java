package cnss.lib;

// An application algorithm that does nothing

import cnss.simulator.ApplicationAlgorithm;
import cnss.simulator.Node;
import cnss.simulator.Packet;
import cnss.simulator.DataPacket;

public class EmptyApp implements ApplicationAlgorithm {

	private Node nodeObj;
	private int nodeId;
	private String[] args;
	private int clockPeriod = 0;
	private int timeoutPeriod = 0;
	private String name = "empty app";
	private boolean logOn = false;

	public EmptyApp() {
	}

	public int initialise(int now, int node_id, Node mynode, String[] args) {
		nodeId = node_id;
		nodeObj = mynode;
		this.args = args;
		log(0, "starting");
		return 0;
	}

	public void on_clock_tick(int now) {
		log(now, "clock tick");
	}

	public void on_timeout(int now) {
		log(now, "timeout");
	}

	public void on_receive(int now, DataPacket p) {
		log(now, "received packet " + p);
	}

	public void showState(int now) {
		log(now, "has no state to show");
	}

	// auxiliary methods

	private void log(int now, String msg) {
		if (logOn)
			System.out.println("log: " + name + " time " + now + " node " + nodeId + " " + msg);
	}

}
