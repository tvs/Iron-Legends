package jig.ironLegends.oxide.packets;

import jig.ironLegends.oxide.packets.ILPacket;

import org.junit.Test;


/**
 * @author Travis Hall
 */
public class PacketTests {
	
	/**
	 * 
	 */
	@Test
	public void testCreateProtocolData() {
		byte[] d = ILPacket.createProtocolData(1, (byte) 2, 
											(byte) 2, (short) 5, true, true);
			
		for (int i = 0; i < d.length; ++i) {
			System.out.println("i: " + i + ", " + d[i]);
		}
		
	}
}
