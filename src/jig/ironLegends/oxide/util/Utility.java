package jig.ironLegends.oxide.util;

/**
 * Some utility classes for packing and retrieving values from byte arrays
 * @author Travis Hall
 */
public class Utility {

	public static byte[] byteArrayFromInt(int integer) {
		return new byte[] {
				(byte) (integer >>> 24), 
				(byte) (integer >>> 16),
				(byte) (integer >>> 8),
				(byte) (integer)
		};
	}
	
	public static int intFromByteArray(byte[] byteArray) {
		return  (int) 	(byteArray[0] << 24 |
						(byteArray[1] & 0xFF) << 16 |
						(byteArray[2] & 0xFF) << 8 |
						(byteArray[3] & 0xFF));
	}
	
	public static short shortFromByteArray(byte[] byteArray) {
		return 	(short) (byteArray[0] << 8 |
						(byteArray[1] & 0xFF));
	}
	
	public static void addIntegerToByteArray(int integer, byte[] byteArray, 
			int position) {		
		for (int i = 0; i < 4; ++i) {
			int offset = (3 - i) * 8;
			byteArray[position + i] = (byte) (integer >>> offset);
		}
	}
	
	public static void addShortToByteArray(short value, byte[] byteArray,
			int position) {
		for (int i = 0; i < 2; ++i) {
			int offset = (1 - i) * 8;
//			System.out.println(i + " " + Integer.toHexString(value >> offset));
			byteArray[position + i] = (byte) (value >>> offset);
		}
	}
	
	public static void addStringToByteArray(String string, byte[] byteArray, 
			int position) {
		byte[] stream = string.getBytes();
		for (int i = 0; i < stream.length - 1; ++i) {
			byteArray[position + i] = stream[i];
		}
	}

}