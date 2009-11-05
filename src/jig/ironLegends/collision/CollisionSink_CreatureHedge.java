package jig.ironLegends.collision;

import java.util.Random;

import jig.ironLegends.LevelGrid;
import jig.ironLegends.Navigator;

import jig.engine.physics.Body;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;

public class CollisionSink_CreatureHedge extends CollisionSink_VanillaPolyBodyDefault 
{
	public CollisionSink_CreatureHedge(LevelGrid levelGrid)
	{
		m_levelGrid = levelGrid;
	}
	
	@Override
	public boolean onCollision(
			VanillaPolygon poly
			, Body body
			, Vector2D vCorrection) 
	{
		// collision handler sets velocity to zero, so store before
		Vector2D vel = poly.getVelocity();
		// allow adjustment of position, then determine new direction
		super.onCollision(poly, body, vCorrection);
		if (poly.getPosition().getY() != 64)
		{
			//System.out.println("oops " + poly.getPosition());
			
		}
		// rule out the direction of correction?
		// TODO: MJPP - get grid location, determine which directions are valid, then choose
		if (!Navigator.selectOption(m_levelGrid, poly, vel, m_rand))
		{
			// otherwise don't know what happened :)
			if (vel.getX() == 0)
			{
				// x is zero, moving up or down, select new valid movement
				poly.setVelocity(new Vector2D(0, -vel.getY()));
				
			}
			else
			{
				// y should be 0, moving left or right
				poly.setVelocity(new Vector2D(-vel.getX(), 0));
			}
		}
		
		return true;
	}
	protected LevelGrid m_levelGrid;
	protected Random m_rand = new Random();

}
