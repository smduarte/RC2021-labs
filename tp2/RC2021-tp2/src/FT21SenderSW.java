
import java.io.File;
import java.io.RandomAccessFile;

import cnss.simulator.Node;
import ft21.FT21AbstractSenderApplication;
import ft21.FT21_AckPacket;
import ft21.FT21_DataPacket;
import ft21.FT21_FinPacket;
import ft21.FT21_UploadPacket;

public class FT21SenderSW extends FT21AbstractSenderApplication {

	private static final int TIMEOUT = 1000;

	static int RECEIVER = 1;

	enum State {
		BEGINNING, UPLOADING, FINISHING, FINISHED
	};

	static int DEFAULT_TIMEOUT = 1000;

	private File file;
	private RandomAccessFile raf;
	private int BlockSize;
	private int nextPacketSeqN, lastPacketSeqN;

	private State state;
	private int lastPacketSent;

	public FT21SenderSW() {
		super(true, "FT21SenderSW");
	}

	public int initialise(int now, int node_id, Node nodeObj, String[] args) {
		super.initialise(now, node_id, nodeObj, args);

		raf = null;
		file = new File(args[0]);
		BlockSize = Integer.parseInt(args[1]);

		state = State.BEGINNING;
		lastPacketSeqN = (int) Math.ceil(file.length() / (double) BlockSize);
		
		lastPacketSent = -1;
		return 1;
	}

	public void on_clock_tick(int now) {
		boolean canSend = lastPacketSent < 0 || (now - lastPacketSent) > TIMEOUT;
		
		if (state != State.FINISHED && canSend)
			sendNextPacket(now);
		
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
		
		lastPacketSent = now;
	}

	@Override
	public void on_receive_ack(int now, int client, FT21_AckPacket ack) {
		switch (state) {
		case BEGINNING:
			state = State.UPLOADING;
		case UPLOADING:
			nextPacketSeqN = ack.cSeqN + 1;
			if (nextPacketSeqN > lastPacketSeqN)
				state = State.FINISHING;
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

			raf.seek(BlockSize * (seqN - 1));
			byte[] data = new byte[BlockSize];
			int nbytes = raf.read(data);
			return new FT21_DataPacket(seqN, data, nbytes);
		} catch (Exception x) {
			throw new Error("Fatal Error: " + x.getMessage());
		}
	}
}
