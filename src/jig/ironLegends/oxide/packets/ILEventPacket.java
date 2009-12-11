package jig.ironLegends.oxide.packets;

import java.io.IOException;

import jig.ironLegends.oxide.events.ILCommandEvent;
import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILEventPacket extends ILPacket {
	
	public ILCommandEvent event;

	/**
	 * @param headerData
	 * @param protocolData
	 * @throws IOException 
	 */
	public ILEventPacket(byte[] protocolData, ILCommandEvent event) 
			throws IOException 
	{
		super(ILPacket.IL_EVENT_HEADER, protocolData);
		this.contentData = new PacketBuffer(event.getBytes());
		this.event = event;
	}
	
	public ILEventPacket(byte[] protocolData, byte[] contentData) {
		super(ILPacket.IL_EVENT_HEADER, protocolData);
		this.contentData = new PacketBuffer(contentData);
		
		this.event = new ILCommandEvent(this.contentData);
	}

}
