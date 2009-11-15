package jig.ironLegends;

public class GridCell 
{
	public GridCell(String info, int col, int row)
	{
		m_col = col;
		m_row = row;
		
		m_info = info;
		m_bObstacle = false;
		
		if (m_info.equals("h"))
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
	public void setInfo(String info){ m_info = info;}
	
	protected String m_info;
	protected boolean m_bObstacle;
	protected int m_col;
	protected int m_row;
}
