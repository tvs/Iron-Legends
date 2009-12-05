package jig.ironLegends.messages;

public class SPStartGame extends Message {
	String m_map;
	
	public SPStartGame(String map)
	{
		m_map = map;
	}
}
