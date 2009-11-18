package jig.ironLegends;

import jig.engine.Sprite;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.MultiSpriteBody;
import jig.ironLegends.mapEditor.MapItemPersist;
import jig.misc.sat.PolygonFactory;

public class Obstacle extends MultiSpriteBody
{
	private String m_name;
	
	public String name(){return m_name;}
	Obstacle(String cellInfo, PolygonFactory pf)
	{
		super(null);
		
		MapItemPersist item = new MapItemPersist(cellInfo);
		
		m_name = item.name();
		
		Vector2D pos = item.centerPosition();
		

		String tokens[] = cellInfo.split(":");
		
		int h = addSprite(item.resource());
		Sprite s = getSprite(h);
		super.setShape(pf.createRectangle(pos, s.getWidth(), s.getHeight()));
		super.setCenterPosition(pos);
		setRotation(Math.toRadians(item.rotDeg()));
		super.setCenterPosition(pos);
		
		
		/*
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
			Vector2D pos = IronLegends.bodyPosToPolyPos(s.getWidth(), s.getHeight(), new Vector2D(x, y));
			super.setShape(pf.createRectangle(pos, s.getWidth(), s.getHeight()));
			setRotation(rotation);
			super.setPosition(getShape().getPosition());
		}
			*/
	}

}
