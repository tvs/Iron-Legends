package jig.ironLegends.router;


import jig.ironLegends.oxide.packets.ILPacket;

public interface IMsgTransport {
	public abstract void send(MsgQueue messages);

	public abstract boolean hasRxMsg();

	public abstract ILPacket nextRxMsg();

	public abstract void send(ILPacket msg);
}
