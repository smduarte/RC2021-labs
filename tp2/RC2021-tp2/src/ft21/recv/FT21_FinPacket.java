package ft21.recv;

import ft21.FT21Packet;

class FT21_FinPacket extends FT21Packet {
	public final int seqN;
	public final byte[] optional_data;

	FT21_FinPacket(byte[] payload) {
		super(payload);
		this.seqN = super.getInt();
		this.optional_data = super.getBytes( super.getByte());
	}
	
	public String toString() {
		return String.format("FIN<%d>", seqN);
	}
}