package jig.ironLegends.oxide.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	// A map of servers discovered by the aSocket
	public Map<InetSocketAddress, ILServerAdvertisementPacket> servers;
	
	public List<ILGameStatePacket> stateUpdates;
	public List<ILPacket> outgoingData;
	public ILLobbyPacket lobbyState;
	
	private boolean lookingForServers;
	private boolean active;
	private boolean connectedToGame;
	
	public boolean receivedStartGame = false;
	public ILStartGamePacket startGamePacket;
	
	private int tickrate;
	private long lastTick = 0;
	private long time = 0;
	
	private int packetID = 0;
	
	public ILClientThread(int tickrate) 
			throws SocketException, IOException 
	{
		this.tickrate = tickrate;
		
		this.myAddress = InetAddress.getLocalHost();
		this.advertSocket = new ILAdvertisementSocket("230.0.0.1", 5001);
		this.lookingForServers = false;
		this.active = false;
		this.connectedToGame = false;
		
		this.servers = new HashMap<InetSocketAddress, ILServerAdvertisementPacket>();
		this.stateUpdates = new LinkedList<ILGameStatePacket>();
		this.outgoingData = new LinkedList<ILPacket>();
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
		
		this.gameSocket = new ILAdvertisementSocket("230.0.0.1", 5002);
		connectedToGame = true;
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
					this.read();
					this.write();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
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
			if (this.startGamePacket == null) this.startGamePacket = (ILStartGamePacket) readPacket;
			else {
				synchronized (this.startGamePacket) {
					this.startGamePacket = (ILStartGamePacket) readPacket;
				}
			}
			this.receivedStartGame = true;
		}
		
	}
	
	private void write() throws IOException {
		if (this.tickExpired()) {
			synchronized (this.outgoingData) {
				// Write until there's no more data
				while (!this.outgoingData.isEmpty()) {
					this.gameSocket.send(this.outgoingData.get(0));
					this.outgoingData.remove(0);
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
	
}
