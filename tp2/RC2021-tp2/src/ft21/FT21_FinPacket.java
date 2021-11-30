package ft21;

public class FT21_FinPacket extends FT21Packet {
	public final int seqN;
	
	public FT21_FinPacket(int seqN) {
		super(PacketType.FIN);
		super.putInt( seqN );
		super.putByte( NO_OPTIONAL_DATA_LEN );
		this.seqN = seqN;
	}
	
	
	public String toString() {
		return String.format("FIN<%d>", seqN);
	}
}