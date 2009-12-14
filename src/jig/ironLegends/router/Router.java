package jig.ironLegends.router;


import jig.ironLegends.oxide.packets.ILPacket;

public class Router {
	MsgQueue m_tx = null;
	MsgQueue m_rx = null;

	public Router()
	{
		m_tx = new MsgQueue();
		m_rx = new MsgQueue();
	}
	public void send(ILPacket msg)
	{
		m_tx.add(msg);
	}
	
	public MsgQueue getRxQueue() {
		return m_rx;
	}
	public MsgQueue getTxQueue()
	{
		return m_tx;
	}
}
