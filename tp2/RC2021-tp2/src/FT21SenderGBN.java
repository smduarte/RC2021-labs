import java.io.File;
import java.io.RandomAccessFile;

import cnss.simulator.Node;
import ft21.FT21AbstractSenderApplication;
import ft21.FT21_AckPacket;
import ft21.FT21_DataPacket;
import ft21.FT21_FinPacket;
import ft21.FT21_UploadPacket;

public class FT21SenderGBN extends FT21AbstractSenderApplication {
	
	private static final int TIMEOUT = 1000;

	static int RECEIVER = 1;

	enum State {
		BEGINNING, UPLOADING, FINISHING, FINISHED
	};

	static int DEFAULT_TIMEOUT = 1000;

	private File file;
	private RandomAccessFile raf;
	private int blockSize, windowSize, packetsSent, packetsInQueue, packetCounter;
	private int nextPacketSeqN, lastPacketSeqN;
	private int[] receivedPackets;

	private State state;
	
	public FT21SenderGBN() {
		super(true, "FT21SenderGBN");
	}
	
	public int initialise(int now, int node_id, Node nodeObj, String[] args) {
		super.initialise(now, node_id, nodeObj, args);

		raf = null;
		file = new File(args[0]);
		blockSize = Integer.parseInt(args[1]);
		windowSize = Integer.parseInt(args[2]);
		packetsSent = 0;
		packetCounter = 0;
		packetsInQueue = 0;
		nextPacketSeqN = 0;

		state = State.BEGINNING;
		lastPacketSeqN = (int) Math.ceil(file.length() / (double) blockSize);
		
		receivedPackets  = new int[lastPacketSeqN+1];
		
		sendNextPacket(now);
		return 1;
	}
	
	public void on_clock_tick(int now) {
		if(packetsInQueue < windowSize && state == State.UPLOADING && nextPacketSeqN <= lastPacketSeqN) {
			packetsSent = nextPacketSeqN;
			packetsInQueue++;
			sendNextPacket(now);
			self.set_timeout(TIMEOUT);
			nextPacketSeqN++;
		}
		
	}
	
	public void on_timeout(int now) {
		super.on_timeout(now);
		super.tallyRTT(now);
		super.tallyTimeout(now);
		
		packetsInQueue = 0;
		nextPacketSeqN = packetCounter+1;
	}

	private void sendNextPacket(int now) {	
		switch (state) {
		case BEGINNING:
			super.sendPacket(now, RECEIVER, new FT21_UploadPacket(file.getName()));
			break;
		case UPLOADING:
			super.sendPacket(now, RECEIVER, readDataPacket(file, nextPacketSeqN));
			break;
		case FINISHING:
			super.sendPacket(now, RECEIVER, new FT21_FinPacket(nextPacketSeqN));
			break;
		case FINISHED:
		}
		
	}

	@Override
	public void on_receive_ack(int now, int client, FT21_AckPacket ack) {
		switch (state) {
		case BEGINNING:
			state = State.UPLOADING;
		case UPLOADING:
			if (packetsSent == 0 && ack.cSeqN == 0) {
				nextPacketSeqN = ack.cSeqN + 1;
			}
			else if(ack.cSeqN > packetCounter) {
					packetsInQueue = packetsInQueue - (ack.cSeqN - packetCounter);
					packetCounter = ack.cSeqN;
					for (int i = 1; i <= ack.cSeqN; i++) {
						if(receivedPackets[i] == 0)
							receivedPackets[i] = i;
					}
			}
			
			if (nextPacketSeqN > lastPacketSeqN && receivedPackets[lastPacketSeqN] != 0) {
				state = State.FINISHING;
				sendNextPacket(now);
			}
			self.set_timeout(TIMEOUT);
			break;
		case FINISHING:
			super.log(now, "All Done. Transfer complete...");
			super.printReport(now);
			state = State.FINISHED;
			return;
		case FINISHED:
		}
		
		
	}

	private FT21_DataPacket readDataPacket(File file, int seqN) {
		try {
			if (raf == null)
				raf = new RandomAccessFile(file, "r");

			raf.seek(blockSize * (seqN - 1));
			byte[] data = new byte[blockSize];
			int nbytes = raf.read(data);
			return new FT21_DataPacket(seqN, data, nbytes);
		} catch (Exception x) {
			throw new Error("Fatal Error: " + x.getMessage());
		}
	}

}
