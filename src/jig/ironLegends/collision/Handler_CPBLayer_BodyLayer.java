package jig.ironLegends.collision;

import java.util.Iterator;

import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.ironLegends.core.ConvexPolyBody;
import jig.misc.sat.PolygonFactory;

public class Handler_CPBLayer_BodyLayer extends Handler_CPB_BodyLayer 
{
	protected BodyLayer<Body> m_polyLayer;
	
	public Handler_CPBLayer_BodyLayer(
			  PolygonFactory polygonFactory
		  	, BodyLayer<Body> polyLayer
			, BodyLayer<Body> bodyLayer
			, int bodyWidth
			, int bodyHeight
			, ISink_CPB_Body collisionSink)
	{
		super(null, bodyLayer, polygonFactory, bodyWidth, bodyHeight, collisionSink);
		m_polyLayer = polyLayer;
	}
	@Override
	public void findAndReconcileCollisions() 
	{
		Iterator<Body> iter = m_polyLayer.iterator();
		
		while (iter.hasNext())
		{
			m_poly = (ConvexPolyBody)iter.next();
			
			super.findAndReconcileCollisions();
		}		
	}
}
