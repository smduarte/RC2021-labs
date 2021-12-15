package cnss.simulator;

import java.nio.charset.StandardCharsets;

/**
 * The <code>Packet</code> class models a network packet.
 */
public class Packet {

	public static enum PacketType {
		DATA, CONTROL, TRACING, UNKNOWN
	}

	/**
	 * The size of a Packet with no payload - similar to IP
	 */
	public static int HEADERSIZE = 20;
	/**
	 * The size of a Packet with no payload - similar to IP
	 */
	public static int INITIALTTL = 32;
	
	/**
	 * A packet with destination ONEHOP is directed to the first node that receives it
	 */
	public static int ONEHOP = 10000;
	
	/**
	 * A packet with destination BROADCAST is directed to all nodes
	 */
	public static int BROADCAST = 11111;

	/**
	 * The unknown address.
	 */
	public static int UNKNOWNADDR = -1;

	protected int src;
	protected int dst;
	protected int ttl;
	protected int seq;
	protected int size; // size of the packet including payload size
	protected byte[] payload;
	protected PacketType type;

	/**
	 * <code>Packet</code> constructor for the super class. This defaults to setting
	 * the packet type to be the UNKNOWN type.
	 * 
	 * @param s  source address
	 * @param d  destination address
	 * @param pl initial payload
	 */
	public Packet(int s, int d, byte[] pl) {
		src = s;
		dst = d;
		type = PacketType.UNKNOWN;
		payload = pl;
		ttl = INITIALTTL;
		seq = 0;
		size = HEADERSIZE + payload.length;
	}

	/**
	 * make an exact copy of this packet
	 * 
	 * @return a copy of the packet
	 */
	public Packet getCopy() {
		byte[] copypl = new byte[payload.length];
		System.arraycopy(this.payload, 0, copypl, 0, this.payload.length);
		Packet copy = new Packet(src, dst, copypl);
		copy.setType(type);
		copy.setTtl(ttl);
		copy.setSequenceNumber(seq);
		// copy.setPayload(copypl) would be useless
		// copy.setSize(this.getSize()) would be useless
		return copy;
	}

	/**
	 * Gets the source address
	 * 
	 * @return int source address
	 */
	public int getSource() {
		return src;
	}
	
	/**
	 * Sets the source address
	 * 
	 * @param s the source address
	 */
	public void setSource(int s) {
		src = s;
	}

	/**
	 * Gets the packet type
	 * 
	 * @return int packet type
	 */
	public PacketType getType() {
		return type;
	}

	/**
	 * Sets the packet type
	 * 
	 * @param t packet type
	 */
	public void setType(PacketType t) {
		type = t;
	}

	/**
	 * Gets the size of the packet.
	 * 
	 * @return int the size of the packets
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of the packet.
	 * 
	 * @param s the size of the packet
	 */
	public void setSize(int s) {
		size = s;
	}

	/**
	 * Gets the destination address
	 * 
	 * @return int destination address
	 */
	public int getDestination() {
		return dst;
	}
	
	/**
	 * Sets the destination address
	 * 
	 * @param d the new destination address
	 */
	public void setDestination(int d) {
		dst = d;
	}

	/**
	 * Sets the packet sequence number, this is for marking purposes.
	 * 
	 * @param s sequence number
	 */
	public void setSequenceNumber(int s) {
		seq = s;
	}

	/**
	 * Gets the packet sequence number.
	 * 
	 * @param s sequence number
	 */
	public int getSequenceNumber() {
		return seq;
	}

	/**
	 * Simple to string method.
	 * 
	 * @return String string representation
	 */
	public String toString() {
		String s;
		s = "src " + src + " dst " + dst + " type " + type + " ttl " + ttl + " seq " + seq + " size " + size;
		if (type == PacketType.TRACING) {
			s = s + " path " + new String(payload, StandardCharsets.UTF_8);;
		}
		return s;
	}

	/**
	 * Sets the Payload for the packet.
	 * 
	 * @param d the packets payload
	 */
	public void setPayload(byte[] d) {
		payload = d;
		size = HEADERSIZE + payload.length;
	}

	/**
	 * Gets the Payload of the packet.
	 * 
	 * @return Payload the packets's payload.
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Reduces the ttl by 1.
	 */
	public void decrementTtl() {
		ttl--;
	}

	/**
	 * Gets the current packet ttl
	 * 
	 * @return int current ttl
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * Sets the current packet ttl
	 * 
	 * @param int ttl to set
	 */
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	/**
	 * Returns the packet as a <code>DataPacket</code> Type
	 * 
	 * @param p is the packet to return
	 * @return p as a DataPacket
	 */
	public DataPacket toDataPacket() {
	    if ( type != PacketType.DATA ) {
	    	System.err.println("toDataPacket: not a DataPacket");
	    	System.exit(-1);
	    }
	    // a brute force copy
        byte[] pl = new byte[this.payload.length];
	    System.arraycopy(this.payload, 0, pl, 0, pl.length);
	    DataPacket copy = new DataPacket(this.src, this.dst, pl);
	    copy.setSource(this.src);
	    copy.setDestination(this.dst);
	    copy.setPayload(pl);
	    copy.setTtl(this.ttl);
	    copy.setSequenceNumber(this.seq);
	    copy.setSize(this.size);
	    copy.setType(PacketType.DATA);
	    return copy;   
	    
	}

}
