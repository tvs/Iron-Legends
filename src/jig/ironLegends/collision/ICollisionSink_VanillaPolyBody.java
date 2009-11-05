package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;

public interface ICollisionSink_VanillaPolyBody 
{
	/// return true to continue processing collisions
	public boolean onCollision(VanillaPolygon poly, Body body, Vector2D vCorrection);

}
