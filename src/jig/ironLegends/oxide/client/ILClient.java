package jig.ironLegends.oxide.client;


import java.io.IOException;
import java.net.InetAddress;

import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;

/**
 * A little testing class for emulating a client that receives data
 * @author Travis Hall
 */
public class ILClient {
	
	public static void main(String[] args) throws IOException {
		try {
			InetAddress addr = InetAddress.getByName("localhost");
			
			ILClientThread cThread = new ILClientThread(20);
			cThread.connectTo(addr);
			cThread.setActive(true);
			cThread.setLookingForServers(true);
			
			new Thread(cThread).start();
			boolean cont = true;
			boolean advert = true;
			boolean lobby = true;
			while(cont) {
				if (advert) {
					synchronized (cThread.servers) {
						for (ILServerAdvertisementPacket p : cThread.servers.values() ) {
							System.out.println(p);
							advert = false;
						}
					}
				}
				
				if (lobby) {
					if (cThread.lobbyState != null) {
						synchronized (cThread.lobbyState) {
							System.out.println(cThread.lobbyState);
							lobby = false;
						}
					}
				}
			}
			if (lobby == false && advert == false) {
				cont = false;
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
