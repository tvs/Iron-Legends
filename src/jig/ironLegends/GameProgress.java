package jig.ironLegends;

public class GameProgress 
{

	public GameProgress(LevelProgress levelProgress)
	{
		m_levelProgress = levelProgress;
		//m_curLevel 		= 1;
		m_totalScore 	= 0;
	}
	public void reset()
	{
		m_totalScore 	= 0;
		m_livesRemaining = IronLegends.START_LIVES;
	}
	
	public int getLivesRemaining()
	{
		return m_livesRemaining;
	}
	
	public void playerDied()
	{
		m_livesRemaining--;
	}

	public LevelProgress getLevelProgress()
	{
		return m_levelProgress;
	}
	
	public int getTotalScore()
	{
		return m_totalScore + m_levelProgress.getScore();
	}
	
	// returns the final total score for the game
	public int gameOver()
	{
		return m_totalScore + m_levelProgress.getScore();
	}
	
	protected int m_totalScore;
	protected int m_livesRemaining;

	public LevelProgress m_levelProgress;
	protected String m_sMapName;
	
	public void tankDestroyed(Tank t) {
		if (t == null)
			return;
		
		m_levelProgress.tankDestroyed();
	}
	public void setMapName(String mapName) {
		m_sMapName = mapName;
	}
	public String getMapName()
	{
		return m_sMapName;
	}
}
