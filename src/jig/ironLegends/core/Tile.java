package jig.ironLegends.core;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;

public class Tile extends VanillaAARectangle
{
	public Tile(int x, int y, String img)
	{
		super(IronLegends.HR_SPRITE_SHEET + "#" + img);
		setPosition(new Vector2D(x,y));
	}

	@Override
	public void update(long deltaMs) 
	{
		// TODO Auto-generated method stub
		
	}
		
	public static final int WIDTH  = 32;
	public static final int HEIGHT = 32;
}
