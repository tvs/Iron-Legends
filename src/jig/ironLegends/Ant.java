package jig.ironLegends;

import jig.engine.physics.vpe.ConvexPolygon;

public class Ant extends Creature 
{
	protected Mitko m_mitko;
	
	public Ant(boolean bHorizontal, ConvexPolygon shape, Mitko mitko)
	{
		super("ant", FRAMES, FRAME_DURATION_MS, bHorizontal, shape, mitko);
	}

	@Override
	int getTrapScore()
	{
		return 10;
	}

	protected static final int WIDTH = 18;
	protected static final int HEIGHT = 9;
	
	protected static final int FRAMES = 4;
	protected static final long FRAME_DURATION_MS = CREATURE_FRAME_DURATION_MS;
}
