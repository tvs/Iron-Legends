package jig.ironLegends.oxide.sockets;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.exceptions.PacketFormatException;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;

/**
 * Simple socket-reading class using channels
 * NOT IN USE
 * @author Travis Hall
 */
public abstract class ILSocket {
	public static int TIME_OUT = 1000;
	public InetAddress hostAddress;
	protected ByteBuffer buffer;
	protected SelectableChannel channel;
	protected InetSocketAddress remoteSocket;
	
	/**
	 * Creates a new ILSocket used to connect to the given IP address
	 * and port number
	 * @param ipAddress The IP of the server to connect to
	 * @param portNumber The port number of the server
	 * @throws IOException 
	 */
	protected ILSocket(InetAddress ipAddress, int portNumber) 
			throws IOException 
	{
		this.hostAddress = ipAddress;
		this.buffer = ByteBuffer.allocate(1400);
		this.buffer.order(ByteOrder.BIG_ENDIAN);
		this.remoteSocket = new InetSocketAddress(ipAddress, portNumber);
	}
	
	/**
	 * Reads a single packet from the buffer into an IronLegendsPacket object
	 * @return The IronLegendsPacket object created from the data in the buffer
	 * @throws PacketFormatException When the IronLegendsPacket could not be
	 * 		   created because of a format error
	 * @throws IOException 
	 */
	protected ILPacket getPacketFromData() 
			throws PacketFormatException, IOException 
	{		
		return ILPacketFactory.getPacketFromData(this.buffer, this.hostAddress.getHostAddress() + "\0");
	}
	
	/**
	 * Retrieves a packet from the socket
	 * @return The packet associated with the socket's data
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws IronOxideException
	 */
	public ILPacket getMessage() 
			throws IOException, TimeoutException, IronOxideException
	{
		int bytesRead;
		ILPacket packet;
		
		bytesRead = this.receivePacket(1400);
		
		if(this.packetIsSplit()) {
			byte[] splitData;
			int packetCount, packetNumber, packetId, splitSize;
			ArrayList<byte[]> splitPackets = new ArrayList<byte[]>();
			
			do {
				// Parsing of split packet headers
				packetId = this.buffer.getInt();
				packetNumber = this.buffer.get();
				packetCount = this.buffer.get();
				splitSize = this.buffer.getShort();
				
				// Caching split packet data
				splitData = new byte[Math.min(splitSize, this.buffer.remaining())];
				this.buffer.get(splitData);
				splitPackets.ensureCapacity(packetCount);
				splitPackets.add(packetNumber, splitData);
				
				// Receiving the next packet
				if (splitPackets.size() < packetCount) {
					try {
						bytesRead = this.receivePacket();
					} catch(TimeoutException e) {
						bytesRead = 0;
					}
				} else {
					bytesRead = 0;
				}
				
				Logger.getLogger("global").info("Received packet #" +
						packetNumber + " of " + packetCount + 
						" for request ID "+ packetId + ".");
			} while (bytesRead > 0 && this.packetIsSplit());
			
			packet = null;
//			packet = ILPacketFactory.reassemblePacket(splitPackets);
		} else {
			packet = this.getPacketFromData();
		}
		
		this.buffer.flip();
		
		return packet;
	}
	
	protected boolean packetIsSplit() {
		return ((this.buffer.get() & ILPacket.SPLIT_FLAG) == 
				ILPacket.SPLIT_FLAG);
	}
	
	/**
	 * Reads a UDP packet into an existing or a new buffer
	 * @param bufferLength The length of the new buffer to create or 0 
	 * 		  to use the existing buffer
	 * @return The number of bytes received
	 * @throws IOException
	 * @throws TimeoutException
	 */
	protected int receivePacket(int bufferLength)
			throws IOException, TimeoutException
	{
		Selector selector = Selector.open();
		this.channel.register(selector, SelectionKey.OP_READ);
		
		if (selector.select(TIME_OUT) == 0) {
			selector.close();
			throw new TimeoutException();
		}
		
		if (bufferLength == 0) {
			this.buffer.clear();
		} else {
			this.buffer = ByteBuffer.allocate(bufferLength);
		}
		
		int bytesRead = ((ReadableByteChannel) this.channel).read(this.buffer);
		if (bytesRead > 0) {
			this.buffer.rewind();
			this.buffer.limit(bytesRead);
		}
		
		selector.close();
		
		return bytesRead;
	}
	
	protected int receivePacket() throws IOException, TimeoutException {
		return this.receivePacket(0);
	}
	
	/**
	 * Closes the DatagramChannel
	 */
	@Override
	public void finalize() throws IOException {
		this.channel.close();
	}

}
