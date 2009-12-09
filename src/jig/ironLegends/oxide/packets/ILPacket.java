package jig.ironLegends.oxide.packets;

import java.nio.ByteBuffer;

import jig.ironLegends.oxide.events.ILEvent;
import jig.ironLegends.oxide.util.PacketBuffer;
import jig.ironLegends.oxide.util.Utility;

/**
 * Generic packet class for sending data between server and client
 * @author Travis Hall
 */
public abstract class ILPacket {
	public static final int MAX_PACKET_SIZE = 1400;
	public static final short DEFAULT_SPLIT_SIZE = 0x04E0;
	
	// Headers
	public static final byte ACK_HEADER = 0x6A;
	public static final byte PING_HEADER = 0x69;
	public static final byte IL_GAME_DATA_HEADER = 0x71;
	public static final byte IL_SERVER_ADVERTISEMENT_HEADER = 0x53;
	public static final byte IL_SERVER_LOBBY_DATA_HEADER = 0x76;
	
	public static final int ORDER_FLAG = 0x80;
	public static final int SPLIT_FLAG = 0x40;
	
	/** Iron Legends protocol identifier for filtering purposes */
	private static final int IL_PROTOCOL_ID = 0xFCFC731F;
		
	protected byte headerData;
	protected PacketBuffer contentData;
	protected PacketBuffer protocolData;
	
	protected int packetID;
	protected byte packetNumber;
	protected byte packetCount;
	protected short splitSize;
	protected byte flags;
	
	protected ILPacket(byte headerData, byte[] protocolData) {
		this(headerData, protocolData, new byte[0]);
	}
	
	protected ILPacket(byte headerData, byte[] protocolData, byte[] contentBytes) {
		this.headerData = headerData;
		this.protocolData = new PacketBuffer(protocolData);
		this.contentData = new PacketBuffer(contentBytes);
		
		this.flags = this.protocolData.getByte();
		this.packetID = this.protocolData.getInt();
		this.packetNumber = this.protocolData.getByte();
		this.packetCount = this.protocolData.getByte();
		this.splitSize = this.protocolData.getShort();
		this.protocolData.rewind();
	}
	
	public PacketBuffer getContent() {
		return this.contentData;
	}
	
	/**
	 * Get a byte array representation of the packet for sending
	 * @return byte array of the data
	 */
	public byte[] getBytes() {
		int pos = 0;
		byte[] bytes = new byte[this.contentData.getLength() + this.protocolData.getLength() + 5];
		Utility.addIntegerToByteArray(IL_PROTOCOL_ID, bytes, pos);
		pos = 4;
		
		System.arraycopy(this.protocolData.array(), 0, bytes, pos, this.protocolData.getLength());
		pos += this.protocolData.getLength();
		
		bytes[pos++] = this.headerData;
		
		System.arraycopy(this.contentData.array(), 0, bytes, pos, this.contentData.getLength());
		
		return bytes;
	}
	
	public int getPacketID() {
		return this.packetID;
	}
	
	public byte getPacketNumber() {
		return this.packetNumber;
	}
	
	public byte getPacketCount() {
		return this.packetCount;
	}
	
	public short getSplitSize() {
		return this.splitSize;
	}
	
	public boolean inOrder() {
		return ((this.flags & ORDER_FLAG) == ORDER_FLAG);
	}
	
	public boolean isSplit() {
		return ((this.flags & SPLIT_FLAG) == SPLIT_FLAG);
	}
	
	public static byte[] createProtocolData(int packetID, byte packetNum, 
			byte packetCount, short splitSize, boolean inOrder, boolean split) {
		
		byte[] data = new byte[9];
		
		byte flag = 0;
		if(inOrder) flag |= ORDER_FLAG;
		if(split) flag |= SPLIT_FLAG;
		
		data[0] = flag;
		
		Utility.addIntegerToByteArray(packetID, data, 1);
		data[5] = packetNum;
		data[6] = packetCount;
		Utility.addShortToByteArray(splitSize, data, 7);
		
		return data;
		
	}
	
	public static byte[] createProtocolData(int packetID, byte packetNum,
			byte packetCount, short splitSize) {
		return createProtocolData(packetID, packetNum, packetCount, splitSize, 
				false, false);
	}

	public String toString() {
		return 	"Protocol:\n" +
			   		"\tPacket ID: " + this.packetID + "\n" +
			   		"\tPacket Number: " + this.packetNumber + "\n" +
			   		"\tPacket Count: " + this.packetCount + "\n" +
			   		"\tSplit Size: " + this.splitSize + "\n" +
			   		"\tFlags:\n" +
			   			"\t\tIn Order: " + this.inOrder() + "\n" +
			   			"\t\tIs Split: " + this.isSplit();
	}

	/**
	 * @return A byte buffer wrapped around our byte array
	 */
	public ByteBuffer getByteBuffer() {
		return ByteBuffer.wrap(this.getBytes());
	}
	
	public abstract ILEvent getEvent();
	
}
