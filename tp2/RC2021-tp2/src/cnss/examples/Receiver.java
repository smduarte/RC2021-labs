package cnss.examples;

import cnss.simulator.ApplicationAlgorithm;
import cnss.simulator.DataPacket;
import cnss.simulator.Node;

public class Receiver implements ApplicationAlgorithm {

	private Node nodeObj;
	private int nodeId;
	private String[] args;

	private String name = "receiver";
	private boolean logOn = true;
	private	int counter = 0;

	public Receiver() {
	}

	public int initialise(int now, int node_id, Node mynode, String[] args) {
		nodeId = node_id;
		nodeObj = mynode;
		this.args = args;

		log(now, "started listening");
		return 0;
	}

	public void on_clock_tick(int now) {
		log(now, "clock tick");
	}

	public void on_timeout(int now) {
		log(now, "timeout");
	}

	public void on_receive(int now, DataPacket p) {
	    counter++;
		String msg = name + " received \"" + new String(p.getPayload()) + "\"";
		log(now, msg);
		// Reply to sender
		DataPacket reply = nodeObj.createDataPacket(p.getSource(), msg.getBytes());
		nodeObj.send(reply);
	}

	public void showState(int now) {
		System.out.println(name + " replyed to "+counter+" ping messages");
	}

	// auxiliary methods

	private void log(int now, String msg) {
		if (logOn)
			System.out.println("log: " + name + " time " + now + " node " + nodeId + " " + msg);
	}

}
