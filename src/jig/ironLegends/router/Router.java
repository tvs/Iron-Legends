package jig.ironLegends.router;

import jig.ironLegends.messages.Message;

public class Router {
	MsgQueue m_tx = null;
	MsgQueue m_rx = null;

	public Router()
	{
		m_tx = new MsgQueue();
		m_rx = new MsgQueue();
	}
	public void send(Message msg)
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
