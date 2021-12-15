package cnss.examples;

import cnss.simulator.ApplicationAlgorithm;
import cnss.simulator.DataPacket;
import cnss.simulator.Node;
import cnss.simulator.Packet;
import cnss.simulator.DataPacket;

public class Sender implements ApplicationAlgorithm {

	private Node nodeObj;
	private int nodeId;
	private String[] args;

	private String name = "sender";
	private boolean logOn = true;
	private	int count = 0;

    
	public Sender() {
	}

	public int initialise(int now, int node_id, Node mynode, String[] args) {
		nodeId = node_id;
		nodeObj = mynode;
		this.args = args;

		log(now, "starting pings");
		return 1000;
	}

	public void on_clock_tick(int now) {
		count++;
		byte[] message = ("ping "+count).getBytes();
		DataPacket p = nodeObj.createDataPacket(2, message);
		log(now, "sent ping packet n. "+count+" - " + p);
		nodeObj.send(p);
	}

	public void on_timeout(int now) {
		log(now, "timeout");
	}

	public void on_receive(int now, DataPacket p) {
		log(now, " received reply \"" + new String(p.getPayload()) + "\"");
	}

	public void showState(int now) {
		System.out.println(name + " received "+count+" replies to pings");
	}

	// auxiliary methods

	private void log(int now, String msg) {
		if (logOn)
			System.out.println("log: " + name + " time " + now + " node " + nodeId + " " + msg);
	}

}
