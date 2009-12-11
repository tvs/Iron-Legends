package jig.ironLegends.oxide.packets;

import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILReadyPacket extends ILPacket {

	/**
	 * @param headerData
	 * @param protocolData
	 */
	protected ILReadyPacket(byte[] protocolData) {
		super(ILPacket.IL_READY_HEADER, protocolData);
		this.contentData = new PacketBuffer(new byte[0]);
	}

}
