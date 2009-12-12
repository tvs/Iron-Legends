package jig.ironLegends.oxide.packets;

import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILStartGamePacket extends ILPacket {

	public String map;
	
	/**
	 * @param headerData
	 * @param protocolData
	 */
	protected ILStartGamePacket(byte[] protocolData, String map) {
		super(ILPacket.IL_START_GAME_HEADER, protocolData);
		this.map = map;
		this.contentData = new PacketBuffer(map.getBytes());
	}
	
	protected ILStartGamePacket(byte[] protocolData, byte[] contentData) {
		super(ILPacket.IL_START_GAME_HEADER, protocolData);
		this.contentData = new PacketBuffer(map.getBytes());
		this.map = this.contentData.getString();
		this.contentData.rewind();
	}

}
