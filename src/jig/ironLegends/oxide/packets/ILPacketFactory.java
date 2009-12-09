package jig.ironLegends.oxide.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import jig.ironLegends.oxide.exceptions.IncompletePacketException;
import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.exceptions.PacketFormatException;


/**
 * @author Travis Hall
 */
public class ILPacketFactory {
	
	public static ILPacket getPacketFromData(ByteBuffer buffer)
			throws PacketFormatException 
	{
//		ByteBuffer buffer = ByteBuffer.wrap(rawData);
		
		buffer.getInt(); // Strip the protocol ID
		
		byte[] protocolData = new byte[9];
		buffer.get(protocolData,0, protocolData.length);
		
		int header = buffer.get();
		
		byte[] contentData = new byte[buffer.remaining()];
		buffer.get(contentData);
		
		
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
	
	public static ILPacket reassemblePacket(ArrayList<byte[]> splitPackets)
			throws IOException, IronOxideException
	{
		byte[] packetData, tmpData;
		packetData = new byte[0];
		
		for (byte[] splitPacket : splitPackets) {
			if (splitPacket == null) {
				throw new IncompletePacketException();
			}
			
			tmpData = packetData;
			packetData = new byte[tmpData.length + splitPacket.length];
			System.arraycopy(tmpData, 0, packetData, 0, tmpData.length);
			System.arraycopy(splitPacket, 0, packetData, tmpData.length, 
					splitPacket.length);
		}
		
		packetData = new String(packetData).substring(4).getBytes();
		
		return ILPacketFactory.getPacketFromData(packetData);
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
