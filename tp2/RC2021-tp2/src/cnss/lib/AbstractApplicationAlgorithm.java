package cnss.lib;

import cnss.simulator.ApplicationAlgorithm;
import cnss.simulator.Node;
import cnss.simulator.Packet;
import cnss.simulator.DataPacket;

abstract public class AbstractApplicationAlgorithm implements ApplicationAlgorithm {
	
/**
 * @author Sérgio Duarte of DI - NOVA Science and Technology - Portugal 
 * @version 1.0, September 2020                                                            
 */

	protected boolean logOn = false;
	protected Node self;
	protected String name;
	protected String[] args;
	protected int nodeId;

	protected AbstractApplicationAlgorithm(boolean logOn, String name) {
		this.logOn = logOn;
		this.name = name;
	}

	@Override
	public int initialise(int now, int node_id, Node nodeObj, String[] args) {
		this.self = nodeObj;
		this.args = args;
		this.nodeId = node_id;
		return 0;
	}
	public void set_log(boolean log) {
		this.logOn = log;
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

	protected void log(int now, String msg) {
		if (logOn)
			System.out.println("log: " + name + " time " + now + " node " + self.getId() + " " + msg);
	}
}
