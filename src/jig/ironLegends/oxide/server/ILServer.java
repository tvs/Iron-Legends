package jig.ironLegends.oxide.server;

import java.io.IOException;

/**
 * A testing class for running the server thread
 * @author Travis Hall
 */
public class ILServer {
	public static void main(String[] args) throws IOException {
		try {
//			InetAddress addr = InetAddress.getByName("localhost");
			
			ILServerThread sThread = new ILServerThread(33);
			sThread.setActive(true);
			sThread.setServerName("Doggles house of dogs");
			sThread.setMapName("Badlands");
			new Thread(sThread).start();
			
			//sThread.setServerPacket((byte) 4, (byte) 4, "Doggles house of dogs\0", "Badlands\0", "1.0.1\0");
			
			while (true) {
				sThread.update((long) 1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
