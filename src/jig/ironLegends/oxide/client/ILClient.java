package jig.ironLegends.oxide.client;


import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import jig.ironLegends.oxide.exceptions.IronOxideException;
import jig.ironLegends.oxide.packets.ILPacket;
import jig.ironLegends.oxide.sockets.ILAdvertisementSocket;
import jig.ironLegends.oxide.sockets.ILDataSocket;

/**
 * A little testing class for emulating a client that receives data
 * @author Travis Hall
 */
public class ILClient {
	
	public static void main(String[] args) throws IOException {
		ILAdvertisementSocket aSocket = new ILAdvertisementSocket("230.0.0.1", 5000);
		ILDataSocket dSocket = new ILDataSocket(InetAddress.getByName("localhost"), 4445);
		
		boolean cont = true;
		while (cont) {
			try {
				ILPacket packet = aSocket.getMessage();
//				ILPacket packet = dSocket.getMessage();
				System.out.println(packet);
				cont = false;
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IronOxideException e) {
				e.printStackTrace();
			}
//				catch (TimeoutException e) {
//				continue;
//				e.printStackTrace();
//			}
		}
	}
	
}
