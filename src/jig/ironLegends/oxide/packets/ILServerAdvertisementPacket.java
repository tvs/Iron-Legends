package jig.ironLegends.oxide.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Travis Hall
 */
public class ILServerAdvertisementPacket extends ILPacket {

	public InetSocketAddress address;
	public byte numberOfPlayers;
	public byte maxPlayers;
	public String serverName;
	public String map;
	public String gameVersion;
	
	/**
	 * @param headerData
	 * @param protocolData
	 */
	public ILServerAdvertisementPacket(byte[] protocolData, byte[] contentBytes) {
		super(ILPacket.IL_SERVER_ADVERTISEMENT_HEADER, protocolData, 
				contentBytes);
		
		this.numberOfPlayers = this.contentData.getByte();
		this.maxPlayers = this.contentData.getByte();
		this.serverName = this.contentData.getString();
		this.map = this.contentData.getString();
		this.gameVersion = this.contentData.getString();
		this.contentData.rewind();
	}
	
	public static ILServerAdvertisementPacket packetFromData(byte[] protocolData, 
			byte numberOfPlayers,byte maxPlayers, String serverName, String map, 
			String gameVersion) 
		throws IOException 
	{	
		byte[] contentBytes = createContent(numberOfPlayers, maxPlayers, 
						serverName, map, gameVersion);
		
		return new ILServerAdvertisementPacket(protocolData, contentBytes);
	}
	
	protected static byte[] createContent(byte numberOfPlayers, byte maxPlayers,
									   String serverName, String map, 
									   String gameVersion) 
		throws IOException 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeByte(numberOfPlayers);
		dos.writeByte(maxPlayers);
		dos.writeBytes(serverName);
		dos.writeBytes(map);
		dos.writeBytes(gameVersion);
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
	}

	public String toString() {
		return super.toString() + 
		"\nHeader: " + this.headerData + "\n" +
		"Content:\n" +
		"\tNumber of Players: " + this.numberOfPlayers + "\n" +
		"\tMax Players: " + this.maxPlayers + "\n" +
		"\tServer Name: " + this.serverName + "\n" +
		"\tMap: " + this.map + "\n" +
		"\tGame Version: " + this.gameVersion;
 	}

}
