package jig.ironLegends.oxide.sockets;


import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import jig.ironLegends.oxide.packets.ILPacket;

/**
 * @author Travis Hall
 */
public class ILDataSocket extends ILSocket {

	/**
	 * @param ipAddress
	 * @param portNumber
	 * @throws IOException
	 */
	public ILDataSocket(InetAddress ipAddress, int portNumber)
			throws IOException {
		super(ipAddress, portNumber);
		this.channel = DatagramChannel.open();
		this.channel.configureBlocking(false);
		((DatagramChannel) this.channel).connect(this.remoteSocket);
	}

	/**
	 * Sends an ILPacket object over the UDP channel to the remote end
	 * @param dataPacket The {@link oxide.packets.ILPacket ILPacket} 
	 * 		  to send to the remote end
	 * @throws IOException
	 */
	public void send(ILPacket dataPacket) throws IOException {
		this.buffer = ByteBuffer.wrap(dataPacket.getBytes());
		((DatagramChannel) this.channel).send(this.buffer, this.remoteSocket);
		this.buffer.flip();
	}
}
