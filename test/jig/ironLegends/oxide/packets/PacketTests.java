package jig.ironLegends.oxide.packets;

import java.io.IOException;

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
		byte[] d = null;
		try {
			d = ILPacket.createProtocolData(1, (byte) 2, 
												(byte) 2, (short) 5, true, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		for (int i = 0; i < d.length; ++i) {
			System.out.println("i: " + i + ", " + d[i]);
		}
		
	}
}
