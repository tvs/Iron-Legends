package jig.ironLegends.oxide.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import jig.ironLegends.CommandState;
import jig.ironLegends.oxide.client.ClientInfo;
import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.packets.ILEventPacket;
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
	private static final byte MAX_PLAYERS = 8;
	
	// The host:port combination to listen on
	public InetAddress hostAddress;
	
	private ILServerAdvertisementPacket advertPacket;
	private ILAdvertisementSocket advertSocket;
	private ILAdvertisementSocket lobbySocket;
	private ILAdvertisementSocket gameSocket;
	
	private ILLobbyPacket lobbyPacket;
	
	public ConcurrentLinkedQueue<CommandState> receivedData;
	private ConcurrentLinkedQueue<ILPacket> outgoingData;
	private Map<String, ClientInfo> clients;
	
	protected boolean advertise;
	protected boolean active;
	protected boolean lobby;
	
	private byte numberOfPlayers;
	private String serverName;
	private String map;
	private String version;
	
	private int tickrate;
	private long lastTick = 0;
	private long time = 0;
	
	public int packetID = 0;

	public boolean createdTanks = false;
	
	public ILServerThread(int tickrate) 
			throws IOException 
	{
		this.hostAddress = InetAddress.getLocalHost();
		this.tickrate = tickrate; // Ticks per second
		
		this.advertSocket = new ILAdvertisementSocket("224.0.0.23", 7001, 7002);
		this.gameSocket = new ILAdvertisementSocket("224.0.0.23", 7003, 7004);
		this.lobbySocket = new ILAdvertisementSocket("224.0.0.23", 7005, 7006);
		
		this.advertise = true;
		this.lobby = true;
		this.active = false;
		this.clients = new HashMap<String, ClientInfo>();
		this.receivedData = new ConcurrentLinkedQueue<CommandState>();
		this.outgoingData = new ConcurrentLinkedQueue<ILPacket>();
		
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
		while(true) {
			if (!active) {
				try {
					// Apparently need this or else everything breaks? What the hell?
					Thread.sleep(50L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			boolean exp = this.tickExpired();
	
			try {
				// Advertise the server over the LAN (multicast)
				if (advertise) {
					synchronized(this.advertPacket) {
						this.updateAdvertisementPacket();
						if (exp) {
							advertSocket.send(this.advertPacket);
						}
					}
				}
				
				if (lobby) {
					// Read a lobby update
					this.readLobby();
					synchronized(this.lobbyPacket) {
						this.updateLobbyPacket();
						if (exp) {
							// Send out lobby updates
							this.sendLobbyState();
						}
					}
				}
					
				// Read a client game update
				this.read();
				
				// If the tick has expired, update each of the clients
				if (exp) {
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
		this.serverName = serverName + "\0";
	}
	
	public void setMapName(String map) {
		this.map = map + "\0";
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
	 * Returns a packet number and then increments the counter (wraps back to 0
	 * if we run out of numbers)
	 * @return The packet number to be sent
	 */
	public int packetID() {
		if (this.packetID == Integer.MAX_VALUE) this.packetID = 0;
		return this.packetID++;
	}
	
	private void updateAdvertisementPacket() {
		this.advertPacket = ILPacketFactory.newAdvertisementPacket((int) this.packetID(), this.hostAddress.getHostAddress() + "\0", this.hostAddress.getHostAddress() + "\0", this.numberOfPlayers, MAX_PLAYERS, this.serverName, this.map, this.version);
	}
	
	private void updateLobbyPacket() {
		this.lobbyPacket = ILPacketFactory.newLobbyPacket((int) this.packetID(), this.hostAddress.getHostAddress() + "\0",  this.hostAddress.getHostAddress() + "\0", this.numberOfPlayers, this.serverName, this.map, this.clients.values());
	}
		
	/**
	 * Read client's data and add any completed packets to the received queue
	 * @param key 
	 * @param client
	 * @throws IOException
	 */
	private void read() throws IOException {
		ILPacket readPacket;
		try {
			readPacket = gameSocket.getMessage();
		} catch(SocketTimeoutException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (IronOxideException e) {
			e.printStackTrace();
			return;
		}
		
		this.handlePacket(readPacket);
	}
	
	private void readLobby() throws IOException {
		ILPacket readPacket;
		try {
			readPacket = lobbySocket.getMessage();
		} catch(SocketTimeoutException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (IronOxideException e) {
			e.printStackTrace();
			return;
		}
		
		this.handlePacket(readPacket);
	}
	
	private void handlePacket(ILPacket readPacket)  
	{	
		if (readPacket instanceof ILEventPacket) {
			synchronized(this.receivedData) {
				ILEventPacket ep = (ILEventPacket) readPacket;
				this.receivedData.add(ep.event);
			}
		} else if (readPacket instanceof ILLobbyEventPacket) {
			ILLobbyEventPacket ep = (ILLobbyEventPacket) readPacket;
			
			ClientInfo c = this.clients.get(ep.getSenderAddress());
			if (c == null) {
				if (numberOfPlayers < MAX_PLAYERS) {
					c = new ClientInfo(this.numberOfPlayers++, ep.getSenderAddress(), ep.name, ep.team);
					synchronized(this.clients) {
						this.clients.put(ep.getSenderAddress(), c);
					}
				}
			} else {
				synchronized(c) {
					c.name = ep.name;
					c.team = ep.team;
					c.clientIP = ep.getSenderAddress();
				}
			}
		} else if (readPacket instanceof ILReadyPacket) {
			ClientInfo c = this.clients.get(readPacket.getSenderAddress());
			synchronized (c) {
				c.ready = true;
			}
		}
	}
	
	/**
	 * Write updates to each of the clients
	 * @throws IOException 
	 */
	private void updateClients() throws IOException {
		synchronized(this.outgoingData) {
			while(!outgoingData.isEmpty()) {
				this.gameSocket.send(outgoingData.remove());
				//outgoingData.remove(0);
			}
		}
	}
	
	private void sendLobbyState() throws IOException {
		this.lobbySocket.send(this.lobbyPacket);
	}

	/**
	 * @return
	 */
	public String getMapName() {
		return this.map;
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return this.active;
	}
	
}