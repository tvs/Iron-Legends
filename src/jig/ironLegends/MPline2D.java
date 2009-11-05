package jig.ironLegends;

import jig.engine.util.Vector2D;

public class MPline2D 
{
	public Vector2D initial;
	public Vector2D terminal;
	
	public MPline2D(Vector2D a, Vector2D b)
	{
		initial = a;
		terminal = b;
	}
	public boolean isHorizontal()
	{
		if (initial.getY() == terminal.getY())
			return true;
		return false;
	}
	public boolean isVertical()
	{
		if (initial.getX() == terminal.getX())
			return true;
		return false;
	}
}