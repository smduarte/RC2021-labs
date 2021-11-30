package ft21.recv;

import ft21.FT21Packet;

class FT21_DataPacket extends FT21Packet {
	public final int seqN;
	public final byte[] data;
	public final byte[] optional_data;
	
	FT21_DataPacket(byte[] bytes) {
		super(bytes);
		this.seqN = super.getInt();
		this.optional_data = super.getBytes( super.getByte());
		this.data = super.getBytes();
	}
	
	public String toString() {
		return String.format("DATA<%d, len: %d>", seqN, data.length);
	}

}