package jig.ironLegends.core;

public class HighScore 
{
	private String m_sPlayerName;
	public int m_highScore;
	
	public HighScore()
	{
		m_highScore = 0;
		m_sPlayerName = "Mitko";
	}
	
	public void setHighScore(int highScore)
	{
		m_highScore = highScore;
	}
	public void setPlayer(String playerName)
	{
		m_sPlayerName = playerName;
	}

	public String getPlayer()
	{
		return m_sPlayerName;
	}
	public int getHighScore()
	{
		return m_highScore;
	}

}
