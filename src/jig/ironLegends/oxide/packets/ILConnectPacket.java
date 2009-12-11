package jig.ironLegends.oxide.packets;

import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILConnectPacket extends ILPacket {
	
	private String name;
	
	/**
	 * @param headerData
	 * @param protocolData
	 */
	public ILConnectPacket(byte[] protocolData, String name) {
		super(ILPacket.IL_CONNECT_HEADER, protocolData);
		this.name = name;
		this.contentData = new PacketBuffer(name.getBytes());
	}
	
	public ILConnectPacket(byte[] protocolData, byte[] contentBytes) {
		super(ILPacket.IL_CONNECT_HEADER, protocolData);
		this.contentData = new PacketBuffer(contentBytes);
		this.name = this.contentData.getString();
		this.contentData.rewind();
	}
	
	public String getName() {
		return this.name;
	}

}
