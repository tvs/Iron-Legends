package jig.ironLegends.oxide.sockets;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.exceptions.PacketFormatException;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;

/**
 * Assumes that received and outgoing packets are NEVER SPLIT
 * 
 * @author Travis Hall
 */
public class ILAdvertisementSocket {
	protected InetSocketAddress address;
	protected ByteBuffer buffer;
	protected InetAddress group;
	
	protected int readPort;
	protected int writePort;
	
	protected MulticastSocket outSocket;
	protected MulticastSocket inSocket;
	
	/**
	 * Opens Multicast sockets for data sending/retrieval
	 * @param ipAddress Multicast group to connect to
	 * @param portNumber Port number of the server
	 * @throws IOException 
	 */
	public ILAdvertisementSocket(String ipAddress, int readPort, int writePort) 
		throws IOException, SocketException
	{			
		this.buffer = ByteBuffer.allocate(ILPacket.MAX_PACKET_SIZE);
		this.buffer.order(ByteOrder.BIG_ENDIAN);
		
		this.group = InetAddress.getByName(ipAddress);
		
		if (!this.group.isMulticastAddress()) {
			throw new SocketException("Invalid multicast address");
		}
		
		this.outSocket = new MulticastSocket();
		this.inSocket = new MulticastSocket(readPort);
		this.inSocket.joinGroup(group);
		
		this.readPort = readPort;
		this.writePort = writePort;
		this.inSocket.setSoTimeout(ILSocket.TIME_OUT);
	}
	
	public void send(ILPacket dataPacket) throws IOException {
//		Logger.getLogger("global").info("Sending data packet of type \"" + 
//				dataPacket.getClass().getSimpleName() + "\"");
		 
		DatagramPacket packet;
		
		byte[] buffer = dataPacket.getBytes();
		
		packet = new DatagramPacket(buffer, buffer.length, this.group, this.writePort);
		this.outSocket.send(packet);
	}
	
	public void finalize() throws IOException {
		this.inSocket.leaveGroup(group);
		this.inSocket.close();
		
		this.outSocket.close();
	}
	
	/**
	 * Reads a single packet from the buffer into an IronLegendsPacket object
	 * @return The IronLegendsPacket object created from the data in the buffer
	 * @throws PacketFormatException When the IronLegendsPacket could not be
	 * 		   created because of a format error
	 * @throws IOException 
	 */
	protected ILPacket getPacketFromData(String id) 
			throws PacketFormatException, IOException 
	{
		this.buffer.rewind();
		return ILPacketFactory.getPacketFromData(this.buffer, id);
	}
	
	/**
	 * Retrieves a packet from the socket
	 * @return The packet associated with the socket's data
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws IronOxideException
	 */
	public ILPacket getMessage(String id) 
			throws IOException, SocketTimeoutException, IronOxideException
	{	
		this.receivePacket();
		
		ILPacket p = this.getPacketFromData(id);
		
		if (p instanceof ILServerAdvertisementPacket) {
			ILServerAdvertisementPacket packet;
			packet = (ILServerAdvertisementPacket) this.getPacketFromData(id);
			packet.address = this.address;
			
			this.buffer.flip();
			return packet;
		} else {
			this.buffer.flip();
			return p;
		}
	}
	
	public ILPacket getMessage() 
		throws IOException, SocketTimeoutException, IronOxideException
	{
		return this.getMessage(null);
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
	protected int receivePacket()
			throws IOException, SocketTimeoutException
	{
		
		byte buf[] = new byte[ILPacket.MAX_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		
		this.inSocket.receive(packet);
		
		this.address = (InetSocketAddress) packet.getSocketAddress();
		
		this.buffer = ByteBuffer.wrap(packet.getData());
				
		return packet.getLength();
	}
	
	
}
