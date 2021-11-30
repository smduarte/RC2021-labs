package ft21;

import cnss.lib.AbstractApplicationAlgorithm;
import cnss.simulator.DataPacket;
import cnss.simulator.Node;
import ft21.FT21Packet.PacketType;

abstract public class FT21AbstractSenderApplication extends AbstractApplicationAlgorithm {

	private static final String TRAFFIC = "bytes";

	FT21Stats stats;

	protected FT21AbstractSenderApplication(boolean logOn, String name) {
		super(logOn, name);
	}

	public int initialise(int now, int node_id, Node self, String[] args) {
		super.initialise(now, node_id, self, args);
		this.stats = new FT21Stats(now);
		return 0;
	}

	protected void sendPacket(int now, int dest, FT21Packet pkt) {
		super.log(now, "SENDING: " + pkt);

		DataPacket cnssPkt = self.createDataPacket(dest, pkt.encodeToBytes());
		self.send(cnssPkt);

		stats.out.increment(pkt.getClass().getSimpleName(), 1);
		stats.out.increment(TRAFFIC, cnssPkt.getSize());
	}

	public void on_timeout(int now) {
		stats.timeoutEvents++;

		super.log(now, "TIMEOUT...");
	}

	public void on_receive(int now, DataPacket cnssPkt) {
		int src = cnssPkt.getSource();
		byte[] bytes = cnssPkt.getPayload();

		stats.in.increment(TRAFFIC, cnssPkt.getSize());		

		switch (PacketType.values()[bytes[0]]) {
		case ACK:
			this.on_receive_ack(now, src, new FT21_AckPacket(bytes));
			break;
		case ERROR:
		default:
			System.out.println("FATAL ERROR...");
			System.exit(-1);
		}
	}

	protected void printReport(int now) {
		System.out.println("\n+++++++++++++++++++++++++++++++++++++++++");
		System.out.println(super.name + " STATS\n");
		stats.printReport(now);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++\n");
	}

	protected void tallyRTT(int rttSample) {
		stats.rtt.tally(rttSample);
	}

	protected void tallyTimeout(int timeoutSample) {
		stats.timeout.tally(timeoutSample);
	}

	protected void on_receive_ack(int now, int src, FT21_AckPacket ack) {
		this.logPacket( now, ack);
	}
	
	protected void logPacket( int now, FT21Packet pkt ) {
		super.log(now, "GOT: " + pkt);
		stats.in.increment(pkt.getClass().getSimpleName(), 1);
	}
}
