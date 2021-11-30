package ft21.recv;

import ft21.FT21Packet;

class FT21_UploadPacket extends FT21Packet {
	public final String filename;
	public final byte[] optional_data;

	FT21_UploadPacket(byte[] bytes) {
		super( bytes );
		this.optional_data = super.getBytes( super.getByte());
		this.filename = super.getString();
	}

	public String toString() {
		return String.format("UPLOAD<%s>", filename);
	}
}
