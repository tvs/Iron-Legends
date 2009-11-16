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
	
	@Override
	String encoding() 
	{
		// name:x:y:rotDeg:spriteName
		String sEncoding = m_sName;
		sEncoding += ":" + m_sprite.getCenterPosition().getX();
		sEncoding += ":" + m_sprite.getCenterPosition().getY();
		sEncoding += ":" + Math.toDegrees(m_sprite.getRotation());
		sEncoding += ":" + m_sResource;
		return sEncoding;
	}
	
	SpriteMapItem(String encoding)
	{
		super();
		String tokens[] = encoding.split(":");
		// TODO error handling
		m_sName = tokens[0];
		double x = Double.parseDouble(tokens[1]);
		double y = Double.parseDouble(tokens[2]);
		double rotDeg = Double.parseDouble(tokens[3]);
		
		m_sResource = tokens[4];
		
		m_sprite = new ATSprite(m_sResource);
		m_sprite.setCenterPosition(new Vector2D(x,y));
		m_sprite.setRotation(Math.toRadians(rotDeg));
	}
	
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


}
