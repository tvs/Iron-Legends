package jig.ironLegends.core.ui;

import java.net.InetSocketAddress;

import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;

/**
 * @author Travis Hall
 */
public class ServerButton extends Button {
	
	public InetSocketAddress socketAddress;

	/**
	 * @param id
	 * @param sx
	 * @param sy
	 * @param rsc
	 */
	public ServerButton(int id, int sx, int sy, String rsc, InetSocketAddress addr) {
		super(id, sx, sy, rsc);
		this.socketAddress = addr;
	}
	
	public static ServerButton fromPacket(int id, ILServerAdvertisementPacket p, InetSocketAddress addr) {
		return new ServerButton(id, 0, 0, p.serverName, addr);
	}

}
