package cnss.simulator;

/**
 * The <code>ControlPacket</code> constructor. This is a subclass of the packet
 * super class and merely sets the packet type to CONTROL.
 * 
 * @author José Legatheaux of DI - NOVA Science and Technology - Portugal
 *  
 */
public class ControlPacket extends Packet {
	/**
	 * <code>ControlPacket</code> constructor. Calls the super class and sets the
	 * type to CONTROL.
	 * 
	 * @param s  source address
	 * @param d  destination address
	 * @param pl initial payload
	 */
	ControlPacket(int src, int dst, byte[] pl) {
		super(src, dst, pl);
		type = PacketType.CONTROL;
	}
}
