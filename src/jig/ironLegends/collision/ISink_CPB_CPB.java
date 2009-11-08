package jig.ironLegends.collision;

import jig.engine.util.Vector2D;
import jig.ironLegends.core.ConvexPolyBody;
import jig.ironLegends.core.MultiSpriteBody;

public interface ISink_CPB_CPB 
{
	/// return true to continue processing collisions
	public boolean onCollision(ConvexPolyBody main, ConvexPolyBody other, Vector2D vCorrection);
}
