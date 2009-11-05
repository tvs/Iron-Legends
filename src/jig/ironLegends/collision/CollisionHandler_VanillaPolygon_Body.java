package jig.ironLegends.collision;

import java.util.Iterator;

import jig.ironLegends.IronLegends;

import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;
import jig.misc.sat.PolygonFactory;


public class CollisionHandler_VanillaPolygon_Body implements CollisionHandler
{
	public CollisionHandler_VanillaPolygon_Body(
		  PolygonFactory polygonFactory
		, VanillaPolygon poly
		, BodyLayer<Body> bodyLayer
		, int bodyWidth
		, int bodyHeight
		, ICollisionSink_VanillaPolyBody collisionSink
	)
	{
		m_polygonFactory = polygonFactory;
		m_bodyLayer = bodyLayer;
		m_poly 	= poly;	
		m_bodyWidth = bodyWidth;
		m_bodyHeight = bodyHeight;
		
		if (collisionSink == null)
			m_collisionSink = new CollisionSink_VanillaPolyBodyDefault();
		else
			m_collisionSink = collisionSink;
		
	}
	@Override
	public void findAndReconcileCollisions() 
	{
		// traverse bodies and apply collision detection
		// if collide, call to on collided event sink
		// create poly
		boolean bCollisionFound = false;
		
		do
		{
			bCollisionFound = false;
			ConvexPolygon mPoly = m_poly.getShape();
			ConvexPolygon p = m_polygonFactory.createRectangle(new Vector2D(0,0), m_bodyWidth, m_bodyHeight);
			
			Iterator<Body> iter = m_bodyLayer.iterator();
			while (iter.hasNext())
			{
				Body b = iter.next();
				if (!b.isActive())
					continue;
				
				//ConvexPolygon p = m_polygonFactory.createRectangle(b.getPosition(), m_bodyWidth, m_bodyHeight);
				p.setPosition(IronLegends.bodyPosToPolyPos(b.getWidth(), b.getHeight(), b.getPosition()));
				//p.setPosition(b.getPosition());

				Vector2D vCorrection = mPoly.minPenetration(p, false);
				if (vCorrection != null)
				{
					
					// TODO: MJPP-bug? since hedge is modeled as individual tiles
					// if hit the corner of a tile, minPenetration will not always
					// reposition outside of the all the tiles
					/*
					 
					 .........
					 |   |   |
					 |..xx...|
					 the min for the tile on the left may be to move the creature to the right
					 however it will then be touching the next tile.. and this will then transport
					 the character maybe even off the screen
					 
					 */
					bCollisionFound = true;
					if (m_collisionSink.onCollision(m_poly, b, vCorrection))
						break;
				}
			}
			
		}while (false && bCollisionFound);
	}
	
	final protected BodyLayer<Body> m_bodyLayer;
	final protected PolygonFactory m_polygonFactory;
	protected VanillaPolygon m_poly;
	final protected int m_bodyWidth;
	final protected int m_bodyHeight;
	final protected ICollisionSink_VanillaPolyBody m_collisionSink;
}
