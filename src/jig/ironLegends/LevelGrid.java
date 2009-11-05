package jig.ironLegends;

import jig.engine.util.Vector2D;

public class LevelGrid 
{
	protected int m_tileWidth;
	protected int m_tileHeight;
	
	public LevelGrid(int tileWidth, int tileHeight)
	{
		m_cells = null;
		m_tileWidth = tileWidth;
		m_tileHeight = tileHeight;
	}

	public void clear()
	{
		m_rows = 0;
		m_cols = 0;
		m_cells = null;
	}
	
	public void setDim(int rows, int cols)
	{
		m_rows = rows;
		m_cols = cols;
		m_cells = new GridCell[m_rows*m_cols];
	}
	public int getRows(){return m_rows;}
	public int getCols(){return m_cols;}

	public GridCell getCell(Vector2D pos)
	{
		return getCell(pos.getX(), pos.getY());
	}
	public GridCell getCell(double x, double y)
	{
		int col = (int)x/m_tileWidth;
		int row = (int)y/m_tileHeight;
		
		if (col > m_cols || col < 0)
			return null;
		if (row > m_rows || row < 0)
			return null;

		return getCell(col, row);
	}
	public GridCell getCell(int col, int row)
	{
		return getCell(row*m_cols + col);		
	}
	
	public GridCell getCell(int cell)
	{
		if (cell < 0 || cell >= m_cells.length)
			return null;
		
		return m_cells[cell];
	}
	
	public void setCell(int col, int row, String cellInfo)
	{
		m_cells[m_cols*row + col] = new GridCell(cellInfo, col, row);
	}
	
	protected int m_cols;
	protected int m_rows;
	GridCell m_cells[];
}
