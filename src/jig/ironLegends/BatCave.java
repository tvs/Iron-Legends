package jig.ironLegends;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class BatCave  extends VanillaAARectangle
{
	BatCave(int x, int y)
	{
		super(IronLegends.SPRITE_SHEET + "#batCave");
		setPosition(new Vector2D(x,y));
	}
	@Override
	public void update(long deltaMs) 
	{
		// TODO Auto-generated method stub
		
	}
		
	static final int WIDTH  = 32;
	static final int HEIGHT = 32;

}
