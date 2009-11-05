package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;

public class CollisionSink_VanillaPolyBodyDefault implements ICollisionSink_VanillaPolyBody
{
	public CollisionSink_VanillaPolyBodyDefault()
	{
	}

	@Override
	 public boolean onCollision(VanillaPolygon poly, Body body, Vector2D vCorrection) 
	{
		Vector2D newPos = poly.getPosition().translate(vCorrection);
		
		poly.setPosition(poly.getPosition().translate(vCorrection));
		poly.setVelocity(new Vector2D(0,0));
		return true;
	}

}
