package ft21;

public class FT21_AckPacket extends FT21Packet {
	public final int cSeqN;
	public final boolean outsideWindow;
	
	FT21_AckPacket(byte[] bytes) {
		super( bytes );		
		int seqN = super.getInt();
		this.cSeqN = Math.abs( seqN );
		this.outsideWindow = seqN < 0;
		
		// decode optional fields here...
	}

	public String toString() {
		return String.format("ACK<%d>", cSeqN);
	}
	
}