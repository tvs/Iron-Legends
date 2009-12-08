package jig.ironLegends.oxide.util;

import static org.junit.Assert.*;
import jig.ironLegends.oxide.util.Utility;

import org.junit.Test;


/**
 * @author Travis Hall
 */
public class UtilityTests {

	/**
	 * Test method for 
	 * {@link oxide.util.Utility#intFromByteArray(byte[])}
	 * and {@link oxide.util.Utility#byteArrayFromInt(int)}.
	 */
	@Test
	public void testIntConversions() {
		int i = 0x5d5e1010;
		byte[] d = Utility.byteArrayFromInt(i);
		int j = Utility.intFromByteArray(d);
		assertEquals(i, j);
	}
	
	/**
	 * Test method for 
	 * {@link oxide.util.Utility#addIntegerToByteArray(int, byte[], int)}.
	 */
	@Test
	public void testAddIntegerToByteArray() {
		int i = 0x5d5e1010;
		byte[] d = new byte[4];
		
		Utility.addIntegerToByteArray(i, d, 0);
		
		assertEquals(0x5d, d[0]);
		assertEquals(0x5e, d[1]);
		assertEquals(0x10, d[2]);
		assertEquals(0x10, d[3]);
	}
	
	/**
	 * Test method for 
	 * {@link oxide.util.Utility#addShortToByteArray(short, byte[], int)}.
	 */
	@Test
	public void testAddShortToByteArray() {
		short i = 0x5d5e;
		byte[] d = new byte[2];
		
		Utility.addShortToByteArray(i, d, 0);
		
		assertEquals(0x5d, d[0]);
		assertEquals(0x5e, d[1]);
	}

	/**
	 * Test method for 
	 * {@link oxide.util.Utility#addStringToByteArray(string, byte[], int)}.
	 */
	@Test
	public void testAddStringToByteArray() {
		String i = "test\0";
		byte[] d = new byte[5];
		
		Utility.addStringToByteArray(i, d, 0);
		
		assertEquals('t', d[0]);
		assertEquals('e', d[1]);
		assertEquals('s', d[2]);
		assertEquals('t', d[3]);
		assertEquals('\0', d[4]);
	}
}
