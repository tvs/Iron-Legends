package jig.ironLegends.oxide.packets;

import jig.ironLegends.oxide.util.Utility;

/**
 * @author Travis Hall
 */
public class ILServerAdvertisementPacket extends ILPacket {

	protected byte numberOfPlayers;
	protected byte maxPlayers;
	protected String serverName;
	protected String map;
	protected String gameVersion;
	
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
			String gameVersion) {
		
		byte[] contentBytes = createContent(numberOfPlayers, maxPlayers, 
						serverName, map, gameVersion);
		
		return new ILServerAdvertisementPacket(protocolData, contentBytes);
	}
	
	protected static byte[] createContent(byte numberOfPlayers, byte maxPlayers,
									   String serverName, String map, 
									   String gameVersion) {
		int len = serverName.length() + map.length() + gameVersion.length() + 2;
		
		byte[] contentData = new byte[len];
		
		int pos = 0;
		contentData[pos++] = numberOfPlayers;
		contentData[pos++] = maxPlayers;
		
		Utility.addStringToByteArray(serverName, contentData, pos);
		pos += serverName.length();
		
		Utility.addStringToByteArray(map, contentData, pos);
		pos += map.length();
		
		Utility.addStringToByteArray(gameVersion, contentData, pos);
		
		return contentData;
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
