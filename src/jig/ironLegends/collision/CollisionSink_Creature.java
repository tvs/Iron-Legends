package jig.ironLegends.collision;

import jig.ironLegends.Bat;
import jig.ironLegends.Creature;
import jig.ironLegends.LevelProgress;
import jig.ironLegends.Mitko;
import jig.ironLegends.core.SoundFx;
import jig.engine.physics.Body;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;

public class CollisionSink_Creature extends CollisionSink_VanillaPolyBodyDefault 
{
	protected LevelProgress m_levelProgress;
	protected final SoundFx m_sfx;
	
	public CollisionSink_Creature(LevelProgress levelProgress, SoundFx sfx)
	{
		m_levelProgress = levelProgress;
		m_sfx = sfx;
	}
	
	@Override
	public boolean onCollision(VanillaPolygon poly, Body body, Vector2D vCorrection)
	{
		// poly = Mitko
		// body = creature
		Mitko m = null;//(Mitko)poly;

		Bat b = null;
		try{
			b = (Bat)body;
		}catch(Exception ex)
		{
			b = null;
		}
		
		if (m.canTrap() && body.isActive())
		{
			if (b == null)
			{
				// make creature inactive
				body.setActivation(false);
				// increment score
				m_levelProgress.trappedCreature((Creature)body);
				m_sfx.play("trapCreature2");
			}
			else
			{
				m_sfx.play("faint2");
			}
		}
		else
		{
			// feint!
			if (!m.isFainting())
				m_sfx.play("faint1");
			m.activateFaint();
			//super.onCollision(poly, body, vCorrection);
		}
		// if mitko eat = true... can eat creatures
		// otherwise faint
		
		return true;
	}
	
}
