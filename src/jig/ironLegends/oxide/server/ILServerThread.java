package jig.ironLegends.oxide.server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
import jig.ironLegends.oxide.events.ILCommandEvent;
import jig.ironLegends.oxide.packets.ILLobbyPacket;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;
import jig.ironLegends.oxide.sockets.ILAdvertisementSocket;


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
	
	private ILLobbyPacket lobbyPacket;
	
	private List<ILCommandEvent> receivedData;
	private List<ILPacket> outgoingData;
	
	private Map<SelectionKey, ClientInfo> clients;
	
	protected boolean advertise;
	protected boolean active;
	protected boolean lobby;
	
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
		this.lobby = true;
		this.active = true;
		this.receivedData = new LinkedList<ILCommandEvent>();
		this.outgoingData = new LinkedList<ILPacket>();
		this.clients = new HashMap<SelectionKey, ClientInfo>();
		
		this.map = "";
		
		this.updateLobbyPacket();
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
				
				// TODO: Create lobby status packets
				if (lobby) {
					synchronized(this.lobbyPacket) {
						
						this.updateLobbyPacket();
						// Create the lobby packet (if something has changed since)
						// Send it out
						// TODO: Update the clients with the lobby status (put lobby packet into outgoing queue)
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
							this.read(key, this.clients.get(key));
						}
					}
				}
				
				// If the tick has expired, update each of the clients
				if (this.time - this.lastUpdate > this.tickTime) {
					this.updateClients();
				}
				
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
			this.advertPacket = ILPacketFactory.newAdvertisementPacket(this.packetID(), numberOfPlayers, maxPlayers, serverName, map, version);
		} else {
			synchronized(this.advertPacket) {
				this.advertPacket = ILPacketFactory.newAdvertisementPacket(this.packetID(), numberOfPlayers, maxPlayers, serverName, map, version);
			}
		}
	}
	
	private int packetID() {
		return this.packetID++;
	}
	
	private void updateAdvertisementPacket() {
		this.advertPacket = ILPacketFactory.newAdvertisementPacket(this.packetID(), this.numberOfPlayers, this.maxPlayers, this.serverName, this.map, this.version);
	}
	
	private void updateLobbyPacket() {
		this.lobbyPacket = ILPacketFactory.newLobbyPacket(this.packetID(), this.numberOfPlayers, this.map, this.clients.values());
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
				
				this.updateAdvertisementPacket();
			} catch (IOException e) {
				// TODO: Send reject packet?
			}
			
		}
	}
	
	/**
	 * Read client's data and add any completed packets to the received queue
	 * @param key 
	 * @param client
	 * @throws IOException
	 */
	private void read(SelectionKey key, ClientInfo client) throws IOException {
		int numRead;
		try {
			// Attempt to read off the channel
			numRead = client.read();
		} catch (IOException e) {
			// The remote forcibly closed the connection. Cancel the key
			this.close(key);
			return;
		}
		
		if (numRead == -1) {
			// Remote entity shut the socket down cleanly, let's do the same
			this.close(key);
			return;
		}
		
		// TODO: Add any completed packets to the event queue
		// TODO: Receive a connection data (id, team, etc.) to update client status
		for (ILPacket p : client.pendingPackets) {
			// Convert the packet to its constituent event(s)
			// If it's an event packet add them to the event queue
		}
		
		
//		this.receivedData.add(ILPacketFactory.getPacketFromData(readBuffer.array()).getEvent());
		
		// TODO: If we receive an ACK, map the key to the ACK'd packet
		
	}
	
	/**
	 * Cancel the key, close the channel, and remove it from the clients map
	 * @param key The key to cancel
	 * @throws IOException
	 */
	private void close(SelectionKey key) throws IOException {
		key.cancel();
		key.channel().close();
		this.clients.remove(key);
	}
	
	/**
	 * Write updates to each of the clients
	 */
	private void updateClients() {
		// TODO: Keep a queue of the last X number of packets sent out
		synchronized (this.outgoingData) {
			// Create a (set) of packet events
			// packetQueue<ILPacket> = ILPacketFactory.createEventPacket(this.outgoingData)
			
			// TODO: Create EventPacket[s]
			// TODO: Create delta change packets between last ACK'd and current event packet
			// TODO: Keep a queue of the last X number of packets sent out
		
//			for (ClientInfo c : this.clients.values()) {
//				for (ILPacket p : packetQueue) {
//					c.channel.write(p.getByteBuffer());
//				}
//			}
		}
	}
	
}