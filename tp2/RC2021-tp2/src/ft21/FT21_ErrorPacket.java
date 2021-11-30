package ft21;

public class FT21_ErrorPacket extends FT21Packet {
	public final String error;

	FT21_ErrorPacket(byte[] bytes) {
		super(bytes);
		this.error = super.getString();
	}

	public FT21_ErrorPacket(String error) {
		super(PacketType.ERROR);
		super.putString(error);
		this.error = error;
	}
	
	public String toString() {
		return String.format("ERROR<%s", error);
	}
}