package jig.ironLegends.oxide.packets;

import java.io.IOException;

import jig.ironLegends.CommandState;
import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILEventPacket extends ILPacket {
	
	public CommandState event;

	/**
	 * @param headerData
	 * @param protocolData
	 * @throws IOException 
	 */
	public ILEventPacket(byte[] protocolData, CommandState event) 
			throws IOException 
	{
		super(ILPacket.IL_EVENT_HEADER, protocolData);
		this.contentData = new PacketBuffer(event.getBytes());
		this.event = event;
	}
	
	public ILEventPacket(byte[] protocolData, byte[] contentData) {
		super(ILPacket.IL_EVENT_HEADER, protocolData);
		this.contentData = new PacketBuffer(contentData);
		this.event = new CommandState(this.contentData);
		this.contentData.rewind();
	}

}
