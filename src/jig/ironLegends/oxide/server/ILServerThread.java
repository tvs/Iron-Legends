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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jig.ironLegends.oxide.client.ClientInfo;
import jig.ironLegends.oxide.events.ILEvent;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;
import jig.ironLegends.oxide.sockets.ILAdvertisementSocket;
import jig.ironLegends.oxide.sockets.ILDataSocket;


/**
 * <p>ILServerThread that handles packet sending and receiving.</p>
 * 
 * <p>Outgoing events are stuffed into a queue and then converted into a packet.</p>
 * 
 * <p>Incoming events are stuffed into an event queue for the server to execute.</p>
 * 
 * <p><i>Note:</i> The server needs to be updated by the main game loop so that we 
 * can maintain the tickrate</p>
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
	
	private List<ILEvent> receivedData;
	private List<ILEvent> outgoingData;
	
	private Map<SelectionKey, ClientInfo> clients;
	
	protected boolean advertise;
	protected boolean active;
	
	private byte numberOfPlayers;
	private byte maxPlayers;
	private String serverName;
	private String map;
	private String version;
	
	private int packetID = 0;
	
	private int tickrate;
	private double tickTime;
	private long lastUpdate = 0;
	private long time = 0;

	public ILServerThread(InetAddress hostAddress, int port, int tickrate) 
			throws IOException 
	{
		this.hostAddress = hostAddress;
		this.port = port;
		this.tickrate = tickrate;
		
		this.tickTime = (1.0 / this.tickrate); // Ticks per second
		
		this.advertSocket = new ILAdvertisementSocket("230.0.0.1", 5000);
		this.selector = this.initSelector();
		this.advertise = true;
		this.active = true;
		this.receivedData = new LinkedList<ILEvent>();
		this.outgoingData = new LinkedList<ILEvent>();
		this.clients = new HashMap<SelectionKey, ClientInfo>();
	}
	
	/**
	 * Updates the server time (used for sending at a tickrate)
	 * @param deltaMs Change in time since last update occurred
	 */
	public void update(long deltaMs) {
		this.lastUpdate = this.time;
		this.time += deltaMs;
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
						}
						// Removed in favor of a full client-update method
//						else if (key.isWritable()) {
//							this.write(key);
//						}
						
						// If the tick has expired, update each of the clients
						if (this.time - this.lastUpdate > this.tickTime) {
							this.updateClients();
						}
						
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
		synchronized(this.outgoingData) {
			this.outgoingData.add(event);
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
			// TODO: Send reject packet?
		} else {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
			
			try {
				// Accept the connection and make it non-blocking
				SocketChannel socketChannel = serverSocketChannel.accept();
				// Socket socket = socketChannnel.socket();
				socketChannel.configureBlocking(false);
				// Register the socket with the selector and an interest on reading
				socketChannel.register(this.selector, SelectionKey.OP_READ);
				// Load the client into our map of key->clients
				this.clients.put(key, new ClientInfo(this.numberOfPlayers, socketChannel));
				this.numberOfPlayers++;
				
				this.updatePacket();
			} catch (IOException e) {
				// TODO: Send reject packet?
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
			this.clients.remove(key);
			return;
		}
		
		if (numRead == -1) {
			// Remote entity shut the socket down cleanly, let's do the same and cancel the channel
			key.channel().close();
			key.cancel();
			this.clients.remove(key);
			return;
		}
		
		// TODO: Add the received packet to the event queue
		this.receivedData.add(ILPacketFactory.getPacketFromData(readBuffer.array()).getEvent());
		
		// TODO: If we receive an ACK, map the key to the ACK'd packet
		
	}
	
	private void updateClients() {
		
		synchronized (this.outgoingData) {
			// Create a (set) of packet events
			// packetQueue<ILPacket> = ILPacketFactory.createEventPacket(this.outgoingData)
			
			// TODO: Create EventPacket[s]
			// TODO: Create delta change packets between last ACK'd and current event packet
			// TODO: Keep a queue of the last X number of packets sent out
		
			for (ClientInfo c : this.clients.values()) {
				for (ILPacket p : packetQueue) {
					c.channel.write(p.getByteBuffer());
				}
			}
		}
	}
	
	private void write(SelectionKey key) throws IOException {
		// TODO: Keep a queue of the last X number of packets sent out
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		synchronized (this.outgoingData) {
			
			// TODO: Create EventPacket[s] (if there are new pending events)
			// TODO: Send the delta change between last ACK'd and current event packet
			
			
			// Write until there are no events left
			// eventQueue<ILPacket> = ILPacketFactory.createEventPacket(this.outgoingData)
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