package cnss.simulator;


/**
 * The <code>ApplicationPacket</code> constructor. This is a subclass of the
 * packet super class and merely sets the packet type to DATA.
 * 
 * @author José Legatheaux of DI - NOVA Science and Technology - Portugal
 *  
 */
public class DataPacket extends Packet {
	/**
	 * <code>DataPacket</code> constructor. Calls the super class and sets
	 * the type to DATA.
	 * 
	 * @param s  source address
	 * @param d  destination address
	 * @param pl initial payload
	 */
	DataPacket(int src, int dst, byte[] pl) {
		super(src, dst, pl);
		type = PacketType.DATA;
	}
}
