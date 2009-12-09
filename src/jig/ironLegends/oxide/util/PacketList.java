package jig.ironLegends.oxide.util;

import jig.ironLegends.oxide.packets.ILPacket;

/**
 * @author Travis Hall
 */
public class PacketList {
	public ILPacket list[];
	public int count;
	
	public PacketList(int capacity) {
		this.count = 0;
		this.list = new ILPacket[capacity];
	}
	
	public void add(int index, ILPacket packet) throws ArrayIndexOutOfBoundsException {
		if (index < 0 || index > this.list.length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		
		this.count++;
		this.list[index] = packet;
	}
}
