package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ConvexPolyBody;

public class Sink_CPB_Body_Default implements ISink_CPB_Body 
{
	@Override
	public boolean onCollision(ConvexPolyBody poly, Body other,
			Vector2D vCorrection) 
	{
		Vector2D newPos = poly.getPosition().translate(vCorrection);
		
		poly.setPosition(poly.getPosition().translate(vCorrection));
		poly.setVelocity(new Vector2D(0,0));
		return true;
	}

}
