package jig.ironLegends;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class PowerUp extends VanillaAARectangle
{
	protected boolean m_bImmediate;
	PowerUp(int x, int y, String encoded)
	{
		super(IronLegends.SPRITE_SHEET + "#powerup");
		if (encoded.endsWith("2"))
		{
			m_bImmediate = false;
			setFrame(1);
		}
		else
			m_bImmediate = true;
		
		setPosition(new Vector2D(x,y));
	}

	public boolean isImmediate()
	{
		return m_bImmediate;
	}
	
	@Override
	public void update(long deltaMs)
	{
		// TODO Auto-generated method stub
		
	}
	static final int WIDTH = 13;
	static final int HEIGHT = 15;
}
