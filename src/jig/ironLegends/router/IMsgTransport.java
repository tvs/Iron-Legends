package jig.ironLegends.router;

import jig.ironLegends.messages.Message;

public interface IMsgTransport {
	public abstract void send(MsgQueue messages);

	public abstract boolean hasRxMsg();

	public abstract Message nextRxMsg();

	public abstract void send(Message msg);
}
