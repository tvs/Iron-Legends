package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.Bat;
import jig.ironLegends.Creature;
import jig.ironLegends.LevelProgress;
import jig.ironLegends.Mitko;
import jig.ironLegends.core.SoundFx;

public class CollisionSink_BatCreature extends	CollisionSink_VanillaPolyBodyDefault 
{
	protected LevelProgress m_levelProgress;
	protected final SoundFx m_sfx;
	
	public CollisionSink_BatCreature(LevelProgress levelProgress, SoundFx sfx)
	{
		m_levelProgress = levelProgress;
		m_sfx = sfx;
	}
	@Override
	public boolean onCollision(VanillaPolygon poly, Body body, Vector2D vCorrection)
	{
		// poly = bat
		// body = creature
		Bat b = (Bat)poly;

		if (b.getMeal() == body && body != null && body.isActive())
		{
			body.setActivation(false);
			m_sfx.play("trapCreature2");
			b.setAte(true);
		}
		
		return true;
	}

}
