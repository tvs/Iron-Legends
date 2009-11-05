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

public class PolyLayer_PolyLayer implements CollisionHandler 
{

	protected ICollisionSink_VanillaPolyBody m_collisionSink;
	
	public PolyLayer_PolyLayer(			  
		  	  BodyLayer<Body> poly1Layer
			, BodyLayer<Body> poly2Layer
			, ICollisionSink_VanillaPolyBody collisionSink)
	{
		m_poly1Layer = poly1Layer;
		m_poly2Layer = poly2Layer;
		m_collisionSink = collisionSink;		
	}

	public void findAndReconcileCollisions(VanillaPolygon poly)
	{
		boolean bCollisionFound = false;
		
		do
		{
			bCollisionFound = false;
			ConvexPolygon mPoly = poly.getShape();
			
			Iterator<Body> iter = m_poly2Layer.iterator();
			while (iter.hasNext())
			{
				VanillaPolygon b = (VanillaPolygon)iter.next();
				if (!b.isActive())
					continue;

				ConvexPolygon p = b.getShape();
				
				Vector2D vCorrection = mPoly.minPenetration(p, false);
				if (vCorrection != null)
				{
					bCollisionFound = true;
					if (m_collisionSink.onCollision(poly, b, vCorrection))
						break;
				}
			}
			
		}while (false && bCollisionFound);
	}
		
	public void findAndReconcileCollisions() 
	{
		Iterator<Body> poly1Iter = m_poly1Layer.iterator();
		while (poly1Iter.hasNext())
		{
			findAndReconcileCollisions((VanillaPolygon) poly1Iter.next());			
		}
	}
	
	protected final BodyLayer<Body> m_poly1Layer;
	protected final BodyLayer<Body> m_poly2Layer;
}
