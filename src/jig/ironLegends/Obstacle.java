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
	private Destructible m_destructible;
	
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
		
		// set destructible data
		setDestructibleData();
	}

	// allow caller to set damage? or create wrapper methods?
	Destructible getDestructible()
	{
		return m_destructible;
	}
	
	private static int WALL_MAX_HEALTH = 100;
	
	// TODO: override render and set frame based on health?
	// TODO: on collision, get destructible and assign damage
	
	private void setDestructibleData()
	{
		// TODO: lookup from table?
		
		if (m_name.equals("wall"))
		{
			m_destructible = new Destructible(WALL_MAX_HEALTH);
		}
		else if (m_name.equals("rock1"))
		{
			//m_destructible = new Destructible(WALL_MAX_HEALTH);
		}
		else if (m_name.equals("rock2"))
		{
			//m_destructible = new Destructible(WALL_MAX_HEALTH);
		}
		else if (m_name.equals("tree"))
		{
			//m_destructible = new Destructible(WALL_MAX_HEALTH);
		}
		else if (m_name.equals(""))
		{
			//m_destructible = new Destructible(WALL_MAX_HEALTH);
		}
	}
}
