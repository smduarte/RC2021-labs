package cnss.lib;

import cnss.simulator.ControlAlgorithm;
import cnss.simulator.GlobalParameters;
import cnss.simulator.Link;
import cnss.simulator.Node;
import cnss.simulator.Packet;

public class FloodingSwitch implements ControlAlgorithm {

	private Node nodeObj;
	private int nodeId;
	private GlobalParameters parameters;
	private Link[] links;
	private int numInterfaces;
	private String name = "flooding switch control: ";
	private boolean tracingOn = false;

	public FloodingSwitch() {

	}

	public int initialise(int now, int node_id, Node mynode, GlobalParameters parameters, Link[] links, int nint) {
		nodeId = node_id;
		nodeObj = mynode;
		this.parameters = parameters;
		this.links = links;
		numInterfaces = nint;
		tracingOn = parameters.containsKey("trace");
		return 0;
	}

	public void on_clock_tick(int now) {
		trace(now, "clock tick");
	}

	public void on_timeout(int now) {
		trace(now, "timeout");
	}

	public void on_link_up(int now, int iface) {
		trace(now, iface + " link up");
	}

	public void on_link_down(int now, int iface) {
		trace(now, iface + " link down");
	}

	public void on_receive(int now, Packet p, int iface) {
		trace(now, "received control packet");
	}
	
	private void flood_packet (int now, Packet p, int iface) {
		int copiesSent = 0;
		// do the flood
		for (int i = 0; i < links.length; i++) {
			if (i != iface && links[i].isUp()) {
				// always send a copy of p, not the object itself
				nodeObj.send(p.getCopy(), i);
				copiesSent++;
			}
		}
		if (copiesSent == 0) { // allows the local node to count dropped packets
			nodeObj.send(p, UNKNOWN);
		}
		trace(now, "forwarded " + copiesSent + " packet copy(ies)");
	}


	public void forward_packet(int now, Packet p, int iface) {

		if ( p.getDestination() == nodeObj.getId()) {
			Packet localPacket = p.getCopy();
			nodeObj.send(localPacket, LOCAL);
			trace(now, "forwarded a packet locally sent to this node");
			return; // all done
		}

		if ( p.getDestination() == Packet.BROADCAST ) {
			Packet localPacket = p.getCopy();
			localPacket.setDestination(nodeObj.getId());
			nodeObj.send(localPacket, LOCAL);
			trace(now, "forwarded a local copy of a broadcasted packet");
		}
		
		flood_packet (now, p, iface);
	}

	public void showControlState(int now) {
		trace(now, "has no state to show");
	}

	public void showRoutingTable(int now) {
		trace(now, "has no routing table to show");
	}

	// auxiliary methods

	private void trace(int now, String msg) {
		if (tracingOn)
			System.out.println("-- trace: " + name + " time " + now + " node " + nodeId + " " + msg);
	}

}
