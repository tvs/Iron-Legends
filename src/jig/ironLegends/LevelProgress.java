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
	boolean m_bExitActivated;
	boolean m_bExitComplete;
	
	public void update(double deltaMs)
	{
		if (m_introRemainingMs > 0)
			m_introRemainingMs -= deltaMs;
		
		//if (m_exitRemainingMs > 0)
		//	m_exitRemainingMs -= deltaMs;
	}
	
	public void reset()
	{
		m_levelScore 		= 0;
		m_introRemainingMs = 0;
		//m_exitRemainingMs = 0;
		m_bExitActivated= false;
		m_bExitComplete = false;
	}
	
	public boolean isExitActivated(){return m_bExitActivated;}
	// returns true if no more weeds required for level

	public int getScore() { return m_levelScore;}

	protected int m_levelScore;
}
