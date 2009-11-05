package jig.ironLegends;

import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.physics.vpe.VanillaPolygon;

public class Spider extends Creature
{

	Spider(boolean bHorizontal, ConvexPolygon shape, Mitko mitko)
	{
		super("spider", FRAMES, FRAME_DURATION_MS, bHorizontal, shape, mitko);
	}
	
	@Override
	int getTrapScore()
	{
		return 25;
	}
		
	protected static final int WIDTH	= 20;
	protected static final int HEIGHT 	= 20;

	protected static final int FRAMES = 4;
	protected static final long FRAME_DURATION_MS = CREATURE_FRAME_DURATION_MS;
}
