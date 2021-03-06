package jig.ironLegends.oxide.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import jig.ironLegends.CommandState;
import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.packets.ILGameStatePacket;
import jig.ironLegends.oxide.packets.ILLobbyPacket;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.packets.ILPacketFactory;
import jig.ironLegends.oxide.packets.ILReadyPacket;
import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;
import jig.ironLegends.oxide.packets.ILStartGamePacket;
import jig.ironLegends.oxide.sockets.ILAdvertisementSocket;

/**
 * @author Travis Hall
 */
public class ILClientThread implements Runnable {
	// The host:port combination to listen on
	public InetAddress hostAddress;
	
	public InetAddress myAddress;
	
	// The advertisement socket we listen on
	private ILAdvertisementSocket advertSocket;
	private ILAdvertisementSocket gameSocket;
	private ILAdvertisementSocket lobbySocket;
	
	// A map of servers discovered by the aSocket
	public Map<InetSocketAddress, ILServerAdvertisementPacket> servers;
	
	public ConcurrentLinkedQueue<ILGameStatePacket> stateUpdates;
	public ConcurrentLinkedQueue<ILPacket> outgoingData;
	public ILLobbyPacket lobbyState;
	
	private boolean lookingForServers;
	private boolean active;
	private boolean connectedToGame;
	private boolean lobby;
	
	public boolean receivedStartGame = false;
	public boolean loadedMap = false;
	public boolean receivedGo = false;
	
	private ILStartGamePacket startGamePacket;
	
	private int tickrate;
	private long lastTick = 0;
	private long time = 0;
	
	private int packetID = 0;
	
	public ILClientThread(int tickrate) 
			throws SocketException, IOException 
	{
		this.tickrate = tickrate;
		
		this.myAddress = InetAddress.getLocalHost();
		this.advertSocket = new ILAdvertisementSocket("224.0.0.23", 7002, 7001);
		this.lobbySocket = new ILAdvertisementSocket("224.0.0.23", 7006, 7005);
		this.lookingForServers = false;
		this.active = false;
		this.connectedToGame = false;
		
		this.lobby = false;
		
		this.servers = new HashMap<InetSocketAddress, ILServerAdvertisementPacket>();
		this.stateUpdates = new ConcurrentLinkedQueue<ILGameStatePacket>();
		this.outgoingData = new ConcurrentLinkedQueue<ILPacket>();
	}
	
	public int packetID() {
		if (this.packetID == Integer.MAX_VALUE) this.packetID = 0;
		return this.packetID++;
	}
	
	public void update(long deltaMs) {
		this.time += deltaMs;
	}
	
	public boolean tickExpired() {
		return (this.time - this.lastTick > this.tickrate);
	}
	public void disconnect()
	{
		connectedToGame = false;		
	}
	public void connectTo(InetAddress hostAddress) throws IOException {
		this.hostAddress = hostAddress;
		
		this.gameSocket = new ILAdvertisementSocket("224.0.0.23", 7004, 7003);
		connectedToGame = true;
		lobby = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
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
			
			if (lookingForServers) {
				try{
					ILServerAdvertisementPacket aPacket = (ILServerAdvertisementPacket) advertSocket.getMessage();
					servers.put(aPacket.address, aPacket);
				} catch (SocketTimeoutException e) {
					;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IronOxideException e) {
					e.printStackTrace();
				}
			}
			
			if (connectedToGame) {
				try {
					if (lobby) this.readLobby();
					this.read();
					this.write();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
		//System.out.println("Exiting client thread");
	}
	
	public void send(CommandState event) throws IOException {
		ILPacket p = ILPacketFactory.newEventPacket((int) this.packetID(),
					this.hostAddress.getHostAddress() + "\0", 
					this.myAddress.getHostAddress() + "\0", event);
		this.send(p);
	}
	
	public void send(ILPacket packet) throws IOException {
		synchronized(this.outgoingData) {
			this.outgoingData.add(packet);
		}
	}
	
	public void sendLobby(ILPacket packet) throws IOException {
		this.lobbySocket.send(packet);
	}
	
	public void sendReadyPacket() throws IOException {
		synchronized(this.outgoingData) {
			ILReadyPacket p = ILPacketFactory.newReadyPacket((int) this.packetID(), 
					this.hostAddress.getHostAddress() + "\0", this.myAddress.getHostAddress() + "\0");
			this.receivedStartGame = false;
			this.outgoingData.add(p);
		}
	}
	
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
		if (readPacket instanceof ILGameStatePacket) {
			synchronized(this.stateUpdates) {
				this.stateUpdates.add((ILGameStatePacket) readPacket);
			}
		} else if (readPacket instanceof ILServerAdvertisementPacket) {
			return;
		} else if (readPacket instanceof ILLobbyPacket) {
			if (this.lobbyState == null) this.lobbyState = (ILLobbyPacket) readPacket;
			else {
				synchronized(this.lobbyState) {
					this.lobbyState = (ILLobbyPacket) readPacket;
				}
			}
		} else if (readPacket instanceof ILStartGamePacket) {
			if (this.startGamePacket == null) this.setStartGamePacket((ILStartGamePacket) readPacket);
			else {
				synchronized (this.startGamePacket) {
					this.startGamePacket = (ILStartGamePacket) readPacket;
				}
			}
			this.receivedStartGame = true;
		}
		
	}
	
	synchronized private void setStartGamePacket(ILStartGamePacket readPacket) {
		this.startGamePacket = readPacket;
	}

	private void write() throws IOException {
		if (this.tickExpired()) {
			synchronized (this.outgoingData) {
				// Write until there's no more data
				while (!this.outgoingData.isEmpty()) {
					this.gameSocket.send(this.outgoingData.remove());
					//this.outgoingData.remove(0);
				}
			}
			this.lastTick = this.time;
		}
	}

	public void setActive(boolean b) {
		this.active = b;
	}
	
	public void setLookingForServers(boolean b) {
		this.lookingForServers = b;
	}
	
	public void setConnectedToGame(boolean b) {
		this.connectedToGame = b;
	}

	synchronized public ILStartGamePacket getStartGamePacket() {
		// TODO synchronize access to set/get of this variable
		return this.startGamePacket;
	}
	
}
