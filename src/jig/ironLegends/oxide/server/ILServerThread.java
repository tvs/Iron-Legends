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
import jig.ironLegends.oxide.packets.ILLobbyEventPacket;
import jig.ironLegends.oxide.packets.ILLobbyPacket;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.packets.ILReadyPacket;
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
	
	private int tickrate;
	private long lastTick = 0;
	private long time = 0;
	
	public long packetID = 0;

	public ILServerThread(InetAddress hostAddress, int port, int tickrate) 
			throws IOException 
	{
		this.hostAddress = hostAddress;
		this.port = port;
		this.tickrate = tickrate; // Ticks per second
		
		this.advertSocket = new ILAdvertisementSocket("230.0.0.1", 5000);
		this.selector = this.initSelector();
		this.advertise = true;
		this.lobby = true;
		this.active = true;
		this.receivedData = new LinkedList<ILCommandEvent>();
		this.outgoingData = new LinkedList<ILPacket>();
		this.clients = new HashMap<SelectionKey, ClientInfo>();
		
		this.serverName = "Server\0";
		this.map = "Map\0";
		this.version = "1.0.0\0";
		this.numberOfPlayers = 0;
		
		this.updateAdvertisementPacket();
		this.updateLobbyPacket();
	}
	
	/**
	 * @return true if the tick has expired
	 */
	public boolean tickExpired() {
		return (this.time - this.lastTick > this.tickrate);
	}
	
	/**
	 * Updates the server time (used for sending at a tickrate)
	 * @param deltaMs Change in time since last update occurred
	 */
	public void update(long deltaMs) {
		this.time += deltaMs;
	}
	
	public void run() {
		while(active) {
			try {
				// Advertise the server over the LAN (multicast)
				if (advertise) {
					synchronized(this.advertPacket) {
						this.updateAdvertisementPacket();
						if (this.tickExpired()) {
							advertSocket.send(this.advertPacket);
						}
					}
				}
				
				// Send out lobby updates
				if (lobby) {
					synchronized(this.lobbyPacket) {
						this.updateLobbyPacket();
						if (this.tickExpired())
							this.sendLobbyState();
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
				if (this.tickExpired()) {
					// Update the tick
					this.lastTick = this.time;
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
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public void setMapName(String map) {
		this.map = map;
	}
	
	/**
	 * Send a user-defined packet (ie game state, start game, etc)
	 * @param packet
	 */
	public void send(ILPacket packet) {
		synchronized(this.outgoingData) {
			this.outgoingData.add(packet);
		}
	}
	
	/**
	 * Returns a packet number and then increments the counter
	 * @return The packet number to be sent
	 */
	public long packetID() {
		return this.packetID++;
	}
	
	private void updateAdvertisementPacket() {
		this.advertPacket = ILPacketFactory.newAdvertisementPacket((int) this.packetID(), this.numberOfPlayers, this.maxPlayers, this.serverName, this.map, this.version);
	}
	
	private void updateLobbyPacket() {
		this.lobbyPacket = ILPacketFactory.newLobbyPacket((int) this.packetID(), this.numberOfPlayers, this.map, this.clients.values());
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
			return;
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
				return;
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
		if (client == null) return;
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
		
		for (ILPacket p : client.pendingPackets) {
			
			// TODO: If it's an event packet add them to the event queue
			if (p instanceof ILLobbyEventPacket) {
				// If it's a lobby packet, update the client state
				ILLobbyEventPacket ep = (ILLobbyEventPacket) p;
				synchronized(client)
				{
					client.name = ep.name;
					client.team = ep.team;
				}
			} else if (p instanceof ILReadyPacket) {
				// If it's a ready packet, update the client to ready
				synchronized(client) {
					client.ready = true;
				}
			}
		}
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
		synchronized(this.outgoingData) {
			while(!outgoingData.isEmpty()) {
				ILPacket p = outgoingData.get(0);
				for (ClientInfo c : this.clients.values()) {
					try {
						c.channel.write(p.getByteBuffer());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				outgoingData.remove(0);
			}
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
	
	private void sendLobbyState() {
		for (Iterator<ClientInfo> it = this.clients.values().iterator(); it.hasNext();) {
			ClientInfo c = it.next();
			try {
				c.channel.write(this.lobbyPacket.getByteBuffer());
			} catch (IOException e) {
				// Remote closed the connection -- scrag him
				try {
					c.channel.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				it.remove();
				//e.printStackTrace();
			}
		}
	}
	
}