package jig.ironLegends.oxide.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import jig.ironLegends.oxide.util.PacketBuffer;

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
	
	public static final byte IL_GAME_DATA_HEADER = 0x47; // 'G'
	public static final byte IL_CONNECT_HEADER = 0x43; // 'C'
	public static final byte IL_EVENT_HEADER = 0x45; // 'E'
	public static final byte IL_SERVER_ADVERTISEMENT_HEADER = 0x53; // 'S'
	public static final byte IL_LOBBY_DATA_HEADER = 0x4C; // 'L'
	public static final byte IL_LOBBY_EVENT_HEADER = 0x6C; // 'l'
	public static final byte IL_READY_HEADER = 0x52; // 'R'
	
	public static final int ORDER_FLAG = 0x80;
	public static final int SPLIT_FLAG = 0x40;
	
	/** Iron Legends protocol identifier for filtering purposes */
	public static final int IL_PROTOCOL_ID = 0xFCFC731F;
		
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
	 * @throws IOException 
	 */
	public byte[] getBytes() throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeInt(IL_PROTOCOL_ID);
		dos.write(this.protocolData.array());
		dos.writeByte(this.headerData);
		dos.write(this.contentData.array());
		
		// Fill out the buffer
		int remaining = MAX_PACKET_SIZE - dos.size();
		for (int i = 0; i < remaining; i++)
			dos.writeByte('\0');
		
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
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
			byte packetCount, short splitSize, boolean inOrder, boolean split) 
			throws IOException
	{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		byte flag = 0;
		if(inOrder) flag |= ORDER_FLAG;
		if(split) flag |= SPLIT_FLAG;
		
		dos.writeByte(flag);
		dos.writeInt(packetID);
		dos.writeByte(packetNum);
		dos.writeByte(packetCount);
		dos.writeShort(splitSize);
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
	}
	
	public static byte[] createProtocolData(int packetID, byte packetNum,
			byte packetCount, short splitSize) 
			throws IOException 
	{
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
	 * @throws IOException 
	 */
	public ByteBuffer getByteBuffer() throws IOException {
		return ByteBuffer.wrap(this.getBytes());
	}
	
}
