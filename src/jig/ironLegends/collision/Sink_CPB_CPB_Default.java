package jig.ironLegends.collision;

import jig.engine.util.Vector2D;
import jig.ironLegends.core.ConvexPolyBody;

public class Sink_CPB_CPB_Default implements ISink_CPB_CPB {

	@Override
	public boolean onCollision(ConvexPolyBody main, ConvexPolyBody other,
			Vector2D vCorrection) {
		main.setPosition(main.getPosition().translate(vCorrection));
		main.setVelocity(new Vector2D(0,0));
		return true;
	}

}
