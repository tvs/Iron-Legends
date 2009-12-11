package jig.ironLegends.oxide.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.exceptions.PacketFormatException;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.util.PacketList;

/**
 * @author Travis Hall
 */
public class ClientInfo {
	
	public int id;
	public String name;
	public byte team;
	public SocketChannel channel;
	
	public Map<Integer, PacketList> splitPackets;
	
	public List<ILPacket> pendingPackets;
	
	public int lastPacketID = 0;
	
	public ClientInfo(int id, SocketChannel channel) {
		this.id = id;
		this.channel = channel;
		this.splitPackets = new HashMap<Integer, PacketList>();
		this.pendingPackets = new LinkedList<ILPacket>(); 
	}
	
	/**
	 * A constructor for purely information-related purposes.
	 * Has no channel to read/write with!
	 * @param id The player's ID
	 * @param name The player's name
	 * @param team The player's team selection
	 */
	public ClientInfo(int id, String name, byte team) {
		this.id = id;
		this.name = name;
		this.team = team;
		this.channel = null;
	}
	
	public int read() throws IOException {
		ByteBuffer readBuffer = ByteBuffer.allocate(ILPacket.MAX_PACKET_SIZE);
		int numread = this.channel.read(readBuffer);
		
		try {
			ILPacket packet = ILPacketFactory.getPacketFromData(readBuffer);
			
			if (packet.isSplit()) {
				PacketList list = splitPackets.get(packet.getPacketID());
				
				if (list == null) {
					list = new PacketList(packet.getPacketCount());
					list.add(packet.getPacketNumber(), packet);
				} else {
					list.add(packet.getPacketNumber(), packet);
				}
				
				if (list.count == packet.getPacketCount()) {
					ILPacket completedPacket;
					try {
						completedPacket = ILPacketFactory.reassemblePacket(list);
						this.addPacketToPendingData(completedPacket);
					} catch (IronOxideException e) {
						Logger.getLogger("server").warning(e.toString());
					}
				} else {
					splitPackets.put(packet.getPacketID(), list);
				}
			} else {
				// If we're not split, add the packet into the pending packet queue
				this.addPacketToPendingData(packet);
			}
			
		} catch (PacketFormatException e) {
			Logger.getLogger("server").info(e.toString());
		}
		
		return numread;
	}
	
	private void addPacketToPendingData(ILPacket packet) {
		if (packet.getPacketID() > this.lastPacketID) {
			this.pendingPackets.add(packet);
			this.lastPacketID = packet.getPacketID();
		}
	}

}
