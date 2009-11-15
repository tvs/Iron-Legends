package jig.ironLegends;

import java.util.Random;

import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ConvexPolyBody;

public class Navigator 
{
	protected Random m_rand;
	protected MapGrid m_grid;
	
	public Navigator(MapGrid grid)
	{
		m_grid = grid;
		m_rand = new Random();		
	}
	
	public boolean selectRandom(ConvexPolyBody poly)
	{
		return selectOption(poly, poly.getVelocity(), m_rand);
	}
	
	public boolean selectOption(ConvexPolyBody poly)
	{
		return selectOption(m_grid, poly, poly.getVelocity(), m_rand);
	}
	
	// randomly selects a new direction (doesn't check for traverseable)
	public static boolean selectOption(
			ConvexPolyBody poly
			, Vector2D vel
			, Random rand
			)
	{
		boolean bOptionSelected = true;
		
		int sel = (int)(rand.nextFloat()*4);
		int move[] = new int[4];
		move[0] = moveRight;
		move[1] = moveLeft;
		move[2] = moveUp;
		move[3] = moveDown;
		double mag = Math.abs(vel.getY() + vel.getX());

		switch(move[sel])
		{
			case moveRight:
				poly.setVelocity(new Vector2D(mag, 0));
			break;
			case moveLeft:
				poly.setVelocity(new Vector2D(-mag, 0));
			break;
			case moveUp:
				poly.setVelocity(new Vector2D(0,-mag));
			break;
			case moveDown:
				poly.setVelocity(new Vector2D(0,mag));
			break;
			default:
				bOptionSelected = false;
			break;
		}
		return bOptionSelected;
	}
	
	// selects a random direction to travel based on what is traverseable
	public static boolean selectOption(
			  MapGrid grid
			, ConvexPolyBody poly
			, Vector2D vel  
			, Random rand
			)
	{
		boolean bOptionSelected = false;
		
		Vector2D centerPos = poly.getCenterPosition();
		GridCell cell = grid.getCell(centerPos);
		if (cell != null)
		{
			// select new direction that is free
			int col = cell.col();
			int row = cell.row();
			// check col+1, row. 
			int move[] = new int[4];
			
			int validCount = 0;
			cell = grid.getCell(col + 1, row);
			if (cell != null && cell.isFree())
			{			
				move[validCount] = moveRight;
				validCount++;				
			}
			
			cell = grid.getCell(col, row+1);
			if (cell != null && cell.isFree())
			{
				move[validCount] = moveDown;
				validCount++;
			}
			cell = grid.getCell(col - 1, row);
			if (cell != null && cell.isFree())
			{
				move[validCount] = moveLeft;
				validCount++;	
			}
			cell = grid.getCell(col, row-1);
			if (cell != null && cell.isFree())
			{
				move[validCount] = moveUp;
				validCount++;
			}
			
			if (validCount > 0)
			{
				int sel = (int)(rand.nextFloat()*validCount); 
				if (sel >= validCount)
					sel = validCount;
				
				if (sel >= 0)
				{
					// since either x or y is always 0 this should be fine
					// but we'll handle the sign ourselves
					double mag = Math.abs(vel.getY() + vel.getX());
					bOptionSelected = true;
					switch(move[sel])
					{
						case moveRight:
							poly.setVelocity(new Vector2D(mag, 0));
						break;
						case moveLeft:
							poly.setVelocity(new Vector2D(-mag, 0));
						break;
						case moveUp:
							poly.setVelocity(new Vector2D(0,-mag));
						break;
						case moveDown:
							poly.setVelocity(new Vector2D(0,mag));
						break;
						default:
							bOptionSelected = false;
						break;
					}
				}					
			}
		}
		
		return bOptionSelected;
	}
	static final int moveRight = 1;
	static final int moveLeft = 2;
	static final int moveUp = 4;
	static final int moveDown = 8;

}
