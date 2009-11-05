package jig.ironLegends;

public class LevelProgress 
{
	public LevelProgress()
	{		
		reset();
	}
	
	public void setIntro(double introMs)
	{
		m_introRemainingMs = introMs;
	}
	//public void setExit(double exitMs)
	public void setExitComplete(boolean bComplete)
	{
		m_bExitComplete = bComplete;
	}
	public void setExit(boolean bExit)
	{
		if (bExit)
		{
			m_bExitActivated = true;
			m_bExitComplete = false;
		}
		else
		{
			m_bExitActivated = false;
			m_bExitComplete = true;
		}
		//m_exitRemainingMs = exitMs;
	}
	/*
	public double exitRemaining()
	{
		return m_exitRemainingMs;
	}
	*/
	public double introRemaining()
	{
		return m_introRemainingMs;
	}
	public boolean isExitComplete()
	{
		return m_bExitComplete;
		//return m_exitRemainingMs > 0;
	}
	public boolean isIntro()
	{
		return m_introRemainingMs > 0;
	}
	
	double m_introRemainingMs;
	//double m_exitRemainingMs;
	double m_powerupRemainingMs;
	boolean m_bExitActivated;
	boolean m_bExitComplete;
	
	public void trappedCreature(Creature creature)
	{
		m_levelScore += creature.getTrapScore();
	}
	
	public boolean areAntsScared()
	{
		return m_powerupRemainingMs > 0?true:false;
	}
	
	public void update(double deltaMs)
	{
		if (m_powerupRemainingMs > 0)
			m_powerupRemainingMs -= deltaMs;
		if (m_introRemainingMs > 0)
			m_introRemainingMs -= deltaMs;
		
		//if (m_exitRemainingMs > 0)
		//	m_exitRemainingMs -= deltaMs;
	}
	
	public void reset()
	{
		m_weedsCollected 	= 0;	
		m_levelScore 		= 0;
		m_weedsRequired 	= 0;
		m_powerupRemainingMs = 0;
		m_introRemainingMs = 0;
		//m_exitRemainingMs = 0;
		m_bExitActivated= false;
		m_bExitComplete = false;
	}
	
	public boolean isExitActivated(){return m_bExitActivated;}
	// returns true if no more weeds required for level
	public boolean addWeedCollected()
	{
		m_weedsCollected += 1;
		// adjust score
		m_levelScore += 1;
		if (m_weedsCollected >= m_weedsRequired)
			return true;
		return false;
	}
	public int getWeedsRemaining()
	{
		return m_weedsRequired - m_weedsCollected;
	}
	
	public boolean isLevelComplete()
	{
		if (m_weedsCollected >= m_weedsRequired)
			return true;
		return false;		
	}
	
	public void setWeedsRequired(int weedsRequired){ m_weedsRequired = weedsRequired;}
	public int getScore() { return m_levelScore;}

	protected int m_weedsCollected;
	protected int m_weedsRequired;
	protected int m_levelScore;
}
