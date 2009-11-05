package jig.ironLegends;

public class GridCell 
{
	public GridCell(String info, int col, int row)
	{
		m_col = col;
		m_row = row;
		
		m_info = info;
		m_bContainsWeed = false;
		m_bObstacle = false;
		if (m_info.equals("w"))
		{
			m_bContainsWeed = true;
		}
		else if (m_info.equals("h"))
		{
			m_bObstacle = true;
		}		
	}

	public int col() { return m_col;}
	public int row() { return m_row;}
	
	public boolean isFree()
	{
		return !m_bObstacle;
	}
	
	public String getInfo() { return m_info;}
	protected String m_info;
	protected boolean m_bContainsWeed;
	protected boolean m_bObstacle;
	protected int m_col;
	protected int m_row;
}
