package ft21.recv;

import java.io.*;
import java.util.*;

import cnss.simulator.*;
import ft21.*;

public class FT21Receiver extends FT21AbstractReceiverApplication {

	private int windowSize; // by default in blocks

	private SortedMap<Integer, byte[]> window = new TreeMap<>();

	private int nextSeqN;
	private String filename;
	private FileOutputStream fos;

	public FT21Receiver() {
		super(true, "   FT21Receiver");
	}

	@Override
	public int initialise(int now, int nodeId, Node self, String[] args) {
		super.initialise(now, nodeId, self, args);
		if( args.length != 1 ) {
			System.err.println( this.getClass().getSimpleName() + " missing windowSize argument [in config file]");
			System.exit(-1);
		}
		this.windowSize = Integer.valueOf(args[0]);
		this.fos = null;
		this.nextSeqN = 0;
		return 0;
	}

	@Override
	public void on_receive_upload(int now, int client, FT21_UploadPacket upload) {
		super.logPacket(now, upload);
		
		if (nextSeqN <= 1) {
			super.sendPacket(now, client, new FT21_AckPacket(0, upload.optional_data));
			nextSeqN = 1;
			window.clear();
			filename = upload.filename;
		} else
			super.sendPacket(now, client,
					new FT21_ErrorPacket("Unexpected packet type...[Already initiated a transfer...]"));
	}

	@Override
	public void on_receive_data(int now, int client, FT21_DataPacket block) {
		super.logPacket(now, block);
		
		// outside the window.
		if (block.seqN < nextSeqN || block.seqN > nextSeqN + windowSize) {
			int cSeqN = (windowSize == 1 ? nextSeqN - 1 : -(nextSeqN - 1));
			super.sendPacket(now, client, new FT21_AckPacket( cSeqN, block.optional_data));			
		}
		else {
			
			window.putIfAbsent(block.seqN, block.data);
			
			//try to slide window and flush to disk.
			byte[] bytes;
			while ((bytes = window.remove(nextSeqN)) != null) {
				writeBlockToFile(bytes);
				nextSeqN += 1;
			}
			super.sendPacket(now, client, new FT21_AckPacket(nextSeqN - 1, block.optional_data));
		}
	}

	@Override
	public void on_receive_fin(int now, int client, FT21_FinPacket fin) {
		super.logPacket(now, fin);
		
		if (window.isEmpty() && nextSeqN == fin.seqN)
			super.printReport( now );

		super.sendPacket(now, client, new FT21_AckPacket(fin.seqN, fin.optional_data));
	}

	private void writeBlockToFile(byte[] data) {
		try {
			if (fos == null)
				fos = new FileOutputStream("copy-of-" + filename);
			fos.write(data);
		} catch (Exception x) {
			System.err.println("FATAL ERROR: " + x.getMessage());
			System.exit(-1);
		}
	}
}
