package jig.ironLegends.oxide.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILLobbyEventPacket extends ILPacket {

	public String name;
	public byte team;

	/**
	 * @param headerData
	 * @param protocolData
	 * @throws IOException 
	 */
	protected ILLobbyEventPacket(byte[] protocolData, String name, byte team)
			throws IOException 
	{
		super(ILPacket.IL_LOBBY_EVENT_HEADER, protocolData);
		this.team = team;
		
		this.contentData = new PacketBuffer(createContentData(name, team));
	}
	
	protected ILLobbyEventPacket(byte[] protocolData, byte[] contentData) {
		super(ILPacket.IL_LOBBY_EVENT_HEADER, protocolData);
		
		this.contentData = new PacketBuffer(contentData);
		this.name = this.contentData.getString();
		this.team = this.contentData.getByte();
		this.contentData.rewind();
	}
	
	private static byte[] createContentData(String name, byte team) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeBytes(name);
		dos.write(team);
		
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
	}

}
