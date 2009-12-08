package jig.ironLegends.oxide.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Convenience wrapper around ByteBuffer for easily retrieving values (strings primarily)
 * @author Travis Hall
 */
public class PacketBuffer {
	
	private ByteBuffer byteBuffer;
	
	/**
	 * Creates a new PacketBuffer from the given byte array
	 * @param data The data used to build the PacketBuffer
	 */
	public PacketBuffer(byte[] data) {
		this.byteBuffer = ByteBuffer.wrap(data);
	}
	
	/**
	 * Creates a new PacketBuffer from an existing ByteBuffer
	 * @param data The data used for the PacketBuffer
	 */
	public PacketBuffer(ByteBuffer data) {
		this.byteBuffer = data;
	}
	
	/**
	 * @return The byte at the buffer's current position
	 */
	public byte getByte() {
		return this.byteBuffer.get();
	}
	
	/**
	 * @return The short value at the buffer's current position
	 */
	public short getShort() {
		return this.byteBuffer.getShort();
	}
	
	/**
	 * @return The int value at the buffer's current position
	 */
	public int getInt() {
		return this.byteBuffer.getInt();
	}

	/**
	 * @return The float value at the buffer's current position
	 */
	public float getFloat() {
		return this.byteBuffer.getFloat();
	}
	
	/**
	 * @return The string value at the buffer's current position
	 */
	public String getString() {
		byte[] remainingBytes = new byte[this.byteBuffer.remaining()];
		this.byteBuffer.slice().get(remainingBytes);
		String dataString = new String(remainingBytes);
		int stringEnd = dataString.indexOf(0x00); // loc of the first null char
		
		if (stringEnd == -1) {
			return null;
		} else {
			dataString = dataString.substring(0, stringEnd);
			this.byteBuffer.position(this.byteBuffer.position() + 
									 dataString.getBytes().length + 1);
			return dataString;
		}
	}

	/**
	 * @return The length of the buffer
	 */
	public int getLength() {
		return this.byteBuffer.capacity();
	}
	
	/**
	 * @return The number of elements remaining in this buffer
	 */
	public int remaining() {
		return this.byteBuffer.remaining();
	}

	/**
	 * @return true if, and only if, there is at least one remaining element in
	 * this buffer
	 */
	public boolean hasRemaining() {
		return this.byteBuffer.hasRemaining();
	}
	
	/**
	 * Modifies the buffer's byte order
	 * @param byteOrder The new byte order, either BIG_ENDIAN or LITTLE_ENDIAN
	 * @return This buffer
	 */
	public PacketBuffer order(ByteOrder byteOrder) {
		this.byteBuffer.order(byteOrder);
		return this;
	}

	/**
	 * Modifications to this buffer's content will cause the returned array's 
	 * content to be modified, and vice versa.
	 * @return The array that backs this buffer
	 */
	public byte[] array() {
		return this.byteBuffer.array();
	}
	
	/**
	 * Rewinds this buffer. The position is set to zero and the mark is 
	 * discarded.
	 */
	public void rewind() {
		this.byteBuffer.rewind();
	}
	
}
