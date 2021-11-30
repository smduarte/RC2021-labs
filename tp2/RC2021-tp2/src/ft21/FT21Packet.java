package ft21;

import java.nio.ByteBuffer;

public class FT21Packet {

	static final int MAX_FT21_PACKET_SIZE = (1 << 16); // 64KB

	public static enum PacketType {
		UPLOAD, DATA, FIN, ACK, ERROR
	};

	private ByteBuffer bb;
	public final PacketType type;

	protected static byte NO_OPTIONAL_DATA_LEN = (byte)0;
	/**
	 * Constructor for creating a new FT21Packet with the given opcode
	 * 
	 **/
	protected FT21Packet(PacketType type) {
		this.type = type;
		bb = ByteBuffer.allocate(MAX_FT21_PACKET_SIZE).put((byte) type.ordinal());
	}

	/**
	 * Constructor for decoding a byte array as a FT21Packet
	 * 
	 **/
	protected FT21Packet(byte[] bytes) {
		this.bb = ByteBuffer.wrap(bytes);
		this.type = PacketType.values()[bb.get()];
	}

	public byte[] encodeToBytes() {
		byte[] res = new byte[bb.position()];
		System.arraycopy(bb.array(), 0, res, 0, res.length);
		return res;
	}

	/********************
	 * PUTS - adds info to packet current position (starts at 0)
	 **********************/

	/**
	 * Appends a byte to the FT21Packet
	 * 
	 * @param b int to append
	 * @return this packet
	 */
	protected FT21Packet putByte(int b) {
		bb.put((byte) b);
		return this;
	}

	/**
	 * Appends an int (4 bytes, in net byte order) to the FT21Packet
	 * 
	 * @param i int to append
	 * @return this packet
	 */
	protected FT21Packet putInt(int i) {
		bb.putInt(i);
		return this;
	}

	/**
	 * Appends a Java String to the FT21Packet [does not include any terminator!]
	 * 
	 * @param s string to append
	 * @return this packet
	 */
	protected FT21Packet putString(String s) {
		bb.put(s.getBytes());
		return this;
	}

	/**
	 * Appends length bytes from the byte array to the FT21Packet
	 * 
	 * @param block  byte array from were to copy
	 * @param length numb of bytes to append
	 * @return this packet
	 */
	protected FT21Packet putBytes(byte[] block, int length) {
		bb.put(block, 0, length);
		return this;
	}

	/********************
	 * GETS - gets info from packet current position (starts at 0)
	 **********************/

	protected byte getByte() {
		return bb.get();
	}

	/**
	 * Gets an int (4 bytes) stored in net byte order (Big Endian)
	 * 
	 * @return int with value
	 */
	protected int getInt() {
		return bb.getInt();
	}

	/**
	 * Gets a Java String (from current position until end)
	 * 
	 * @return String
	 */
	protected String getString() {
		byte[] b = new byte[bb.remaining()];
		bb.get(b);
		return new String(b);
	}

	/**
	 * Gets a byte array (from current position until end)
	 * 
	 * @return byte[]
	 */
	protected byte[] getBytes() {
		byte[] b = new byte[bb.remaining()];
		bb.get(b);
		return b;
	}

	protected byte[] getBytes(int nbytes) {
		byte[] bytes = new byte[nbytes];
		bb.get(bytes);
		return bytes;
	}
}
