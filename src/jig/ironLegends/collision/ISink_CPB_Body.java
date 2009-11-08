package jig.ironLegends.collision;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ConvexPolyBody;

public interface ISink_CPB_Body 
{
	/// return true to continue processing collisions
	public boolean onCollision(ConvexPolyBody main, Body other, Vector2D vCorrection);
}
