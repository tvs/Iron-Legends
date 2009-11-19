package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.LevelProgress;
import jig.ironLegends.Mitko;
import jig.ironLegends.PowerUp;
import jig.ironLegends.core.ConvexPolyBody;
import jig.ironLegends.core.SoundFx;

public class CollisionSink_PowerUp implements ISink_CPB_Body 
{
	protected LevelProgress m_levelProgress;
	protected final SoundFx m_sfx;
	
	public CollisionSink_PowerUp(LevelProgress levelProgress, SoundFx sfx)
	{
		m_levelProgress = levelProgress;
		m_sfx = sfx;
	}
	
	@Override
	public boolean onCollision(
			ConvexPolyBody poly
			, Body body
			, Vector2D vCorrection) 
	{
		// TODO: MJPP play sound for picking weed
		PowerUp pu = (PowerUp)body;
		if (pu.isActive())
		{
			pu.setActivation(false);
			Mitko m = (Mitko)poly;
			
			if (pu.isImmediate())
			{
//				m_sfx.play("powerup1");
	
				// go through all creates and set scared? nah, just do it during update
				m.activatePowerup(0);
			}
			else
			{
//				m_sfx.play("collectPowerup1");
				m.collectPowerUp();		
			}
		}
		return false;
	}
	
}
