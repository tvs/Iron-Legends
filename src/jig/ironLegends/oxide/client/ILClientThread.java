package jig.ironLegends.oxide.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jig.ironLegends.EntityState;
import jig.ironLegends.oxide.events.ILCommandEvent;
import jig.ironLegends.oxide.events.ILLobbyEvent;
import jig.ironLegends.oxide.server.ServerInfo;
import jig.ironLegends.oxide.sockets.ILAdvertisementSocket;
import jig.ironLegends.oxide.util.ChangeRequest;

/**
 * @author Travis Hall
 */
public class ILClientThread implements Runnable {
	// The host:port combination to listen on
	private InetAddress hostAddress;
	private int port;
	
	// The selector we monitor
	private Selector selector;
	
	// The advertisement socket we listen on
	ILAdvertisementSocket advertSocket;
	
	// A map of servers discovered by the aSocket
	private Map<InetAddress, ServerInfo> servers;
	
	private List<ChangeRequest> pendingChanges;
	
	public List<EntityState> stateUpdates;
	public List<ILCommandEvent> outgoingData;
	public List<ILLobbyEvent> lobbyUpdates;
	
	protected boolean lookingForServers;
	protected boolean active;
	protected boolean lobby;
	
//	private String name;
//	private int playerID;
	
	private int tickrate;
	private double tickTime;
	private long lastUpdate = 0;
	private long time = 0;
	
	
	
	public ILClientThread(int tickrate) 
			throws SocketException, IOException 
	{
		this.tickrate = tickrate;
		
		this.tickTime = (1.0 / this.tickrate); // Ticks per second
		
		this.advertSocket = new ILAdvertisementSocket("230.0.0.1", 5000);
		this.selector = this.initSelector();
		this.lookingForServers = false;
		this.active = false;
		this.lobby = false;
		
		this.pendingChanges = new LinkedList<ChangeRequest>();
		this.stateUpdates = new LinkedList<EntityState>();
		this.outgoingData = new LinkedList<ILCommandEvent>();
		this.lobbyUpdates = new LinkedList<ILLobbyEvent>();
	}
	
	public void update(long deltaMs) {
		this.lastUpdate = this.time;
		this.time += deltaMs;
	}
	
	public void connectTo(InetAddress hostAddress, int port) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		
		// Kick off our connection establishment
		socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));
		
		// Queue a channel registration since hte caller is not the selecting
		// thread. As part of the registration, we'll register an interest in 
		// connection events. These are raised when a channel is ready to
		// complete connection establishment.
		synchronized (this.pendingChanges) {
			this.pendingChanges.add(new ChangeRequest(socketChannel, 
					ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true) {
			try {
				// Process any pending changes
				synchronized(this.pendingChanges) {
					Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = changes.next();
						switch (change.type){
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
							break;
						case ChangeRequest.REGISTER:
							change.socket.register(this.selector, change.ops);
							break;
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event on one of the registered channels
				this.selector.select();
				
				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();
					
					if (!key.isValid()) {
						continue;
					}
					
					// Check what event is available and deal with it
					if (key.isConnectable()) {
						this.finishConnection(key);
					} else if (key.isReadable()) {
//						this.read(key);
					} else if (key.isWritable()) {
//						this.write(key);
					}
				} 
			} catch (Exception e) {
					e.printStackTrace();
			}
		}
	}
	
	private void finishConnection(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		// Finish the connection. If the connection operation failed
		// this will raise an IOException
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			// Cancel the chanel's registration with our selector
			key.cancel();
			return;
		}
		
		// Register an interest in reading from this channel
		key.interestOps(SelectionKey.OP_READ);
	}
	
	private Selector initSelector() throws IOException {
		return SelectorProvider.provider().openSelector();
	}
	
}
