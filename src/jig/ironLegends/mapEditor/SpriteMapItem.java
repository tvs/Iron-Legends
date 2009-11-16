package jig.ironLegends.mapEditor;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ATSprite;

public class SpriteMapItem extends MapItem 
{
	ATSprite m_sprite;
	String m_sName;
	String m_sResource;
	
	SpriteMapItem(Vector2D pos, double rotRad, String name, String rsc)
	{
		super();
		m_sprite = new ATSprite(rsc);
		m_sprite.setCenterPosition(pos);
		//m_sprite.setPosition(pos);
		m_sprite.setRotation(rotRad);
		m_sName = name;
		m_sResource = rsc;
	}

	Vector2D getCenterPosition()
	{
		return m_sprite.getCenterPosition();
	}
	
	@Override
	void render(RenderingContext rc) 
	{
		m_sprite.render(rc);
	}

	@Override
	String encoding() 
	{
		// name:x:y:rotDeg:spriteName
		String sEncoding = m_sName + ":";
		sEncoding += ":" + m_sprite.getCenterPosition().getX();
		sEncoding += ":" + m_sprite.getCenterPosition().getY();
		sEncoding += ":" + Math.toDegrees(m_sprite.getRotation());
		sEncoding += ":" + m_sResource;
		return sEncoding;
	}

}
