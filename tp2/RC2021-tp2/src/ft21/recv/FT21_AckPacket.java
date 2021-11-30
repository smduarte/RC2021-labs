package ft21.recv;

import ft21.FT21Packet;

class FT21_AckPacket extends FT21Packet {
	public final int cSeqN;

	public FT21_AckPacket(int cSeqN, byte[] optional_data) {
		super( PacketType.ACK );
		super.putInt( cSeqN);
		super.putBytes( optional_data, optional_data.length);
		this.cSeqN = cSeqN;
	}
	
	public String toString() {
		return String.format("ACK<%d>", cSeqN);
	}
	
}