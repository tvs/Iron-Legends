package jig.ironLegends.collision;

import java.util.Iterator;

import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.ironLegends.core.ConvexPolyBody;

public class Handler_CPBLayer_CPBLayer extends Handler_CPB_CPBLayer 
{
	protected BodyLayer<Body> m_polyLayer;
	
	public Handler_CPBLayer_CPBLayer(
			BodyLayer<Body> polyLayer
			, BodyLayer<Body> cpbLayer
			, ISink_CPB_CPB collisionSink)
	{
		super(null, cpbLayer, collisionSink);
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
