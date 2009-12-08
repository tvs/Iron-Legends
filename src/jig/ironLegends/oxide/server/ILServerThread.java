package jig.ironLegends.oxide.server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jig.ironLegends.oxide.events.ILEvent;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;
import jig.ironLegends.oxide.sockets.ILAdvertisementSocket;
import jig.ironLegends.oxide.sockets.ILDataSocket;


/**
 * @author Travis Hall
 */
public class ILServerThread implements Runnable {
	// The host:port combination to listen on
	private InetAddress hostAddress;
	private int port;
	
	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;
	
	// The selector we monitor for game updates on
	private Selector selector;
	
	private ILServerAdvertisementPacket advertPacket;
	private ILAdvertisementSocket advertSocket;
//	private ILDataSocket dataSocket;
	
	private List<ILEvent> pendingData;
	
	protected boolean advertise;
	protected boolean active;
	
	private byte numberOfPlayers;
	private byte maxPlayers;
	private String serverName;
	private String map;
	private String version;
	
	private int packetID = 0;

	public ILServerThread(InetAddress hostAddress, int port) 
			throws IOException 
	{
		this.hostAddress = hostAddress;
		this.port = port;
		this.advertSocket = new ILAdvertisementSocket("230.0.0.1", 5000);
//		this.dataSocket = new ILDataSocket(InetAddress.getByName("localhost"), 4445);
		this.selector = this.initSelector();
		this.advertise = true;
		this.active = true;
		this.pendingData = new LinkedList<ILEvent>();
	}
	
	public void run() {
		while(active) {
			try {
				// Advertise the server over the LAN (multicast)
				if (advertise) {
					synchronized(this.advertPacket) {
						advertSocket.send(this.advertPacket);
					}
				}
				
				// Do a non-blocking select to find if there were any new key updates since last time
				int count = this.selector.selectNow();
				
				if (count > 0) {
					// Iterate over the set of keys for which we have available events
					Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
					while (selectedKeys.hasNext()) {
						SelectionKey key = selectedKeys.next();
						selectedKeys.remove();
						
						if (!key.isValid()) {
							continue;
						} 
						
						if (key.isAcceptable()) {
							this.accept(key);
						} else if (key.isReadable()) {
							this.read(key);
						} else if (key.isWritable()) {
							this.write(key);
						}
						
						// TODO: If the tick has expired, update the clients
						// Remove the key.isWritable line
					}
				}
				

//				dSocket.send(this.packet);
			} catch (IOException e) {
				continue;
			}
		}
	}
	
	public void setAdvertise(boolean state) {
		this.advertise = state;
	}
	
	public void setActive(boolean state) {
		this.active = state;
	}
	
	public void setServerPacket(byte numberOfPlayers, byte maxPlayers, String serverName, String map, String version) {
		this.numberOfPlayers = numberOfPlayers;
		this.maxPlayers = maxPlayers;
		this.serverName = serverName;
		this.map = map;
		this.version = version;
		
		if (this.advertPacket == null) {
			this.advertPacket = ILPacketFactory.newAdvertisementPacket(packetID, numberOfPlayers, maxPlayers, serverName, map, version);
		} else {
			synchronized(this.advertPacket) {
				this.advertPacket = ILPacketFactory.newAdvertisementPacket(packetID, numberOfPlayers, maxPlayers, serverName, map, version);
			}
		}
	}
	
	public void addEvent(ILEvent event) {
		synchronized(this.pendingData) {
			this.pendingData.add(event);
		}
	}
	
	private void updatePacket() {
		this.advertPacket = ILPacketFactory.newAdvertisementPacket(this.packetID, this.numberOfPlayers, this.maxPlayers, this.serverName, this.map, this.version);
	}
	
	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();
		
		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		this.serverChannel.configureBlocking(false);
		
		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.hostAddress, 
				this.port);
		this.serverChannel.socket().bind(isa);
		
		// Register the server socket channel, indicating an interest in
		// accepting new connections
		this.serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		
		return socketSelector;
	}
	
	private void accept(SelectionKey key) {	
		if (this.numberOfPlayers > maxPlayers) {
			// Send reject packet?
		} else {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
			
			try {
				// Accept the connection and make it non-blocking
				SocketChannel socketChannel = serverSocketChannel.accept();
				// Socket socket = socketChannnel.socket();
				socketChannel.configureBlocking(false);
				socketChannel.register(this.selector, SelectionKey.OP_READ);
				// TODO: Add player to a queue with an associated ID -- IN READ
				this.updatePacket();
			} catch (IOException e) {
				// Send reject packet?
			}
			
		}
	}
	
	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		ByteBuffer readBuffer = ByteBuffer.allocate(ILPacket.MAX_PACKET_SIZE);
		
		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection. Cancel the key and close the channel
			key.cancel();
			socketChannel.close();
			return;
		}
		
		if (numRead == -1) {
			// Remote entity shut the socket down cleanly, let's do the same and cancel the channel
			key.channel().close();
			key.cancel();
			return;
		}
		
		// TODO: Deal with the received data!
		// TODO: If we receive an ACK, map the key to the ACK'd packet
		
	}
	
	private void write(SelectionKey key) throws IOException {
		// TODO: Write sequence
		// TODO: Keep a queue of the last X number of packets sent out
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		synchronized (this.pendingData) {
			
			// TODO: Create EventPacket[s] (if there are new pending events)
			// TODO: Send the delta change between last ACK'd and current event packet
			
			
			// Write until there are no events left
			while (!eventQueue.isEmpty()) {
				ByteBuffer buf = eventQueue.get(0).getBytes();
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// If the socket fills we just give up
					break;
				}
				eventQueue.remove(0);
			}
			
			if (eventQueue.isEmpty()) {
				// We wrote all the data off so we don't really care about writing for a while
				// Switch back to waiting for data
				key.interestOps(SelectionKey.OP_READ);
			}
			
		}
	}
	
}