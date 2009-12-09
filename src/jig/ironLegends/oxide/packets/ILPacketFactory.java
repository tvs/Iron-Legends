package jig.ironLegends.oxide.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import jig.ironLegends.oxide.exceptions.IncompletePacketException;
import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.exceptions.PacketFormatException;
import jig.ironLegends.oxide.util.PacketList;


/**
 * @author Travis Hall
 */
public class ILPacketFactory {
	
	public static ILPacket getPacketFromData(ByteBuffer buffer) 
			throws PacketFormatException 
	{
		return getPacketFromData(buffer, true);
	}
	
	public static ILPacket getPacketFromData(ByteBuffer buffer, boolean hasProtocolID)
			throws PacketFormatException 
	{	
		if (hasProtocolID)
			buffer.getInt(); // Strip the protocol ID
		
		byte[] protocolData = new byte[9];
		buffer.get(protocolData,0, protocolData.length);
		
		int header = buffer.get();
		
		byte[] contentData = new byte[buffer.remaining()];
		buffer.get(contentData);
		buffer.rewind();
		
		switch(header) {
		case ILPacket.IL_SERVER_ADVERTISEMENT_HEADER:
			return new ILServerAdvertisementPacket(protocolData, contentData);
//		case IronLegendsPacket.IL_SERVER_LOBBY_DATA_HEADER:
//			return new ILServerLobbyDataPacket(protocolData, contentData);
//		case IronLegendsPacket.IL_GAME_DATA_HEADER:
//			return new ILGameDataPacket(protocolData, contentData);
		
		default:
			throw new PacketFormatException("Unknown packet. Header: 0x"
					+ Integer.toHexString(header));
		}
	}
	
	public static ILPacket reassemblePacket(PacketList packetList)
			throws IOException, IronOxideException
	{
		ILPacket firstPacket = packetList.list[0];
		
		if (firstPacket == null) {
			throw new IncompletePacketException();
		}
		
		for (int i = 1; i < packetList.list.length; i++) {
			ILPacket splitPacket = packetList.list[i];
			
			if (splitPacket == null) {
				throw new IncompletePacketException();
			}
			
			firstPacket.getContent().concatenatePacketBuffer(splitPacket.getContent());
		}
		
		return ILPacketFactory.getPacketFromData(firstPacket.getByteBuffer());
	}

	/**
	 * @param packetID
	 * @param numberOfPlayers
	 * @param maxPlayers
	 * @param serverName
	 * @param map
	 * @param version
	 * @return
	 */
	public static ILServerAdvertisementPacket newAdvertisementPacket(
			int packetID, byte numberOfPlayers, byte maxPlayers,
			String serverName, String map, String version) {
	
		byte[] protocolData = getProtocolData(packetID);
		byte[] contentBytes = ILServerAdvertisementPacket.createContent(numberOfPlayers, maxPlayers, serverName, map, version);
		
		return new ILServerAdvertisementPacket(protocolData, contentBytes);
	}

	/**
	 * @param packetID
	 * @return
	 */
	private static byte[] getProtocolData(int packetID) {
		return ILPacket.createProtocolData(packetID, (byte) 0, (byte) 1, ILPacket.DEFAULT_SPLIT_SIZE);
	}
}
