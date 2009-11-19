package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.LevelProgress;
import jig.ironLegends.Mitko;
import jig.ironLegends.Weed;
import jig.ironLegends.core.ConvexPolyBody;
import jig.ironLegends.core.SoundFx;

public class CollisionSink_Weed implements ISink_CPB_Body 
{

	public CollisionSink_Weed(LevelProgress levelProgress, SoundFx sfx)
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
		Weed wd = (Weed)body;
		if (wd.isActive())
		{
			boolean isPowerup = wd.isPowerUp();
			wd.setActivation(false);

			if (isPowerup)
			{
				Mitko m = (Mitko)poly;
				m.collectPowerUp();
				// play sound
//				m_sfx.play("collectPowerup1");
			}
			else
			{
//				m_sfx.play("weedPulled1");
			}
			m_levelProgress.addWeedCollected();
		}
		return false;
	}

	protected final LevelProgress m_levelProgress;
	protected final SoundFx m_sfx;
}
