package jig.ironLegends.collision;

import java.util.Iterator;

import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ConvexPolyBody;

public class Handler_CPB_CPBLayer implements CollisionHandler 
{

	protected ConvexPolyBody m_poly;
	protected BodyLayer<Body> m_cpbLayer;
	protected ISink_CPB_CPB m_collisionSink;
	
	// TODO make intermediate abstract class that represent the fact that 
	// the "Body" has a convex polygon
	public Handler_CPB_CPBLayer(ConvexPolyBody poly, BodyLayer<Body> cpbLayer
			, ISink_CPB_CPB collisionSink
)
	{
		m_poly = poly;
		m_cpbLayer = cpbLayer;
		
		//if (collisionSink == null)
		//	m_collisionSink = new CollisionSink_VanillaPolyBodyDefault();
		//else
			m_collisionSink = collisionSink;
	}
	
	@Override
	public void findAndReconcileCollisions() 
	{
		ConvexPolygon mainShape = m_poly.getShape();
		
		Iterator<Body> iter = m_cpbLayer.iterator();
		while (iter.hasNext())
		{
			ConvexPolyBody b = (ConvexPolyBody)iter.next();
			if (!b.isActive())
				continue;
			
			Vector2D vCorrection = mainShape.minPenetration(b.getShape(), false);
			if (vCorrection != null)
			{
				
				// TODO: MJPP-bug? since hedge is modeled as individual tiles
				// if hit the corner of a tile, minPenetration will not always
				// reposition outside of all the tiles
				/*
				 
				 .........
				 |   |   |
				 |..xx...|
				 the min for the tile on the left may be to move the creature to the right
				 however it will then be touching the next tile.. and this will then transport
				 the character maybe even off the screen
				 
				 */
				if (m_collisionSink.onCollision(m_poly, b, vCorrection))
					break;
			}
		}

	}

}
