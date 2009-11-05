package jig.ironLegends.collision;

import java.util.Iterator;


import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.misc.sat.PolygonFactory;

public class CollisionHandler_PolyLayer_BodyLayer  extends CollisionHandler_VanillaPolygon_Body 
{
	public CollisionHandler_PolyLayer_BodyLayer(
			  PolygonFactory polygonFactory
			  	, BodyLayer<Body> polyLayer
				, BodyLayer<Body> bodyLayer
				, int bodyWidth
				, int bodyHeight
				, ICollisionSink_VanillaPolyBody collisionSink)
	{
		super(polygonFactory, null,bodyLayer, bodyWidth, bodyHeight, collisionSink);
		m_polyLayer = polyLayer;
	}

	@Override
	public void findAndReconcileCollisions() 
	{
		Iterator<Body> iter = m_polyLayer.iterator();
		
		while (iter.hasNext())
		{
			m_poly = (VanillaPolygon)iter.next();
			
			super.findAndReconcileCollisions();
		}		
	}
	
	protected final BodyLayer<Body> m_polyLayer;
}
