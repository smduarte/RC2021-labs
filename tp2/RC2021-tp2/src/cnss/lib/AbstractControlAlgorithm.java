package cnss.lib;

import cnss.simulator.ControlAlgorithm;
import cnss.simulator.DataPacket;
import cnss.simulator.GlobalParameters;
import cnss.simulator.Link;
import cnss.simulator.Node;
import cnss.simulator.Packet;

public class AbstractControlAlgorithm implements ControlAlgorithm {

	/**
	 * @author Sérgio Duarte of DI - NOVA Science and Technology - Portugal 
	 * @version 2.0, September 2021                                                            
	 */
	
	protected boolean traceOn;
	protected Node self;
	protected String name;
	protected int nodeId;
	protected GlobalParameters parameters;
	protected Link[] links;
	protected int nint;

	protected AbstractControlAlgorithm (String name) {
		traceOn = false;
		this.name = name;
	}
	
	@Override
	public int initialise (int now, int node_id, Node nodeObj, GlobalParameters parameters, Link[] links, int nint) {
		this.nodeId = node_id;
		this.self = nodeObj;
		this.parameters = parameters;
		this.links = links;
		this.nint = nint;
		return 0;
	}
	
	public void set_trace(boolean trace) {
		this.traceOn = trace;
	}
	
	public void on_clock_tick(int now) {
		trace(now, "clock tick");
	}

	public void on_timeout(int now) {
		trace(now, "timeout");
	}

	public void on_receive(int now, DataPacket p) {
		trace(now, "received control packet " + p);
	}

	public void on_link_up(int now, int iface) {
		trace(now, "interface "+iface + " link up");
	}

	public void on_link_down(int now, int iface) {
		trace(now, "interface "+iface + " link down");
	}

	public void on_receive(int now, Packet p, int iface) {
		trace(now, "received control packet "+p+" received by ifc "+iface);
	}

	public void forward_packet(int now, Packet p, int iface) {
		trace(now, "forward packet "+p+" received by ifc "+iface);
	}

	public void showControlState(int now) {
		trace(now, "has no state to show");
	}

	public void showRoutingTable(int now) {
		trace(now, "has no routing table to show");
	}

	protected void trace(int now, String msg) {
		if (traceOn)
			System.out.println("trace: " + name + " time " + now + " node " + self.getId() + " " + msg);
	}
	
}
