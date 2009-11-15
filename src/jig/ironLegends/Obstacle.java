package jig.ironLegends;

import jig.engine.Sprite;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.MultiSpriteBody;
import jig.misc.sat.PolygonFactory;

public class Obstacle extends MultiSpriteBody
{
	Obstacle(int x, int y, String cellInfo, PolygonFactory pf)
	{
		super(null);
		
		// wall:v - default
		// wall:h
		String tokens[] = cellInfo.split(":");
		double rotation = 0;
		if (tokens[0].endsWith("wall"))
		{
			if (tokens.length > 1)
			{
				if (tokens[1].equals("h"))
					rotation = Math.toRadians(90);
				else
					rotation = 0;
			}
			//int h = addSprite(IronLegends.SPRITE_SHEET + "#testTile2");
			int h = addSprite(IronLegends.SPRITE_SHEET2 + "#wall");
			//setSpriteVisible(h, true);
			Sprite s = getSprite(h);
			s.getWidth();
			s.getHeight();
			Vector2D pos = IronLegends.bodyPosToPolyPos(s.getWidth(), s.getHeight(), new Vector2D(x, y));
			super.setShape(pf.createRectangle(pos, s.getWidth(), s.getHeight()));
			setRotation(rotation);
			super.setPosition(getShape().getPosition());
		}
	}

}
