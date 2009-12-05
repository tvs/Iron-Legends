package jig.ironLegends;

public class PlayerInfo 
{
	protected String m_sPlayer;
	protected String m_sTeamColor;
	
	public PlayerInfo()
	{
		setName("");
	}
	public PlayerInfo(String sPlayer)
	{
		setName(sPlayer);
	}

	public void setName(String sPlayer)
	{
		if (sPlayer != null)
			m_sPlayer = sPlayer;
		else
			m_sPlayer = "";		
	}
	
	public String getName()
	{
		return m_sPlayer;		
	}
}
