package jig.ironLegends.oxide.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jig.ironLegends.oxide.exceptions.PacketFormatException;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;

/**
 * @author Travis Hall
 */
public class ClientInfo {
	
	public int id;
	public SocketChannel channel;
	
	public Map<Integer, List<ByteBuffer>> packetQueue;
	
	public ClientInfo(int id, SocketChannel channel) {
		this.id = id;
		this.channel = channel;
		this.packetQueue = new HashMap<Integer, List<ByteBuffer>>();
	}
	
	public int read() throws IOException {
		ByteBuffer readBuffer = ByteBuffer.allocate(ILPacket.MAX_PACKET_SIZE);
		int numread = this.channel.read(readBuffer);
		
		try {
			ILPacket packet = ILPacketFactory.getPacketFromData(readBuffer);
		} catch (PacketFormatException e) {
			Logger.getLogger("server").info(e.toString());
		}
		
		return numread;
	}

}
