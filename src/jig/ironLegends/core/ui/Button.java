package jig.ironLegends.core.ui;

import java.awt.Point;

import jig.engine.Mouse;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;

public class Button extends Sprite 
{
	// NOTE could have button test for active and then just use a single UIState.. to reduce memory, but who cares for now
	protected int m_sx;
	protected int m_sy;
	MouseState m_uis = new MouseState();
	protected int m_id;
	public int getId() { return m_id;}
	
	public Button(int sx, int sy, String rsc, int id)
	{
		super(rsc);
		m_sx = sx;
		m_sy = sy;
		m_id = id;
		
		setPosition(new Vector2D(sx, sy));	
	}
	
	public boolean wasLeftClicked()
	{
		return m_uis.wasLeftClicked();
	}
	
	public void update(Mouse mouse, final long deltaMs)
	{
		Point mousePt = mouse.getLocation();
		
		int width 	= getWidth();
		int height 	= getHeight();
		
		boolean bContains = false;

		if (	mousePt.x > m_sx && mousePt.y > m_sy &&
				mousePt.x < m_sx + width && mousePt.y < m_sy + height)
		{
			bContains = true;
		}
		
		if (bContains)
		{
			if (!m_uis.isActive())
				m_uis.onEnter(m_id, mousePt);
			m_uis.update(mouse, deltaMs, m_id);
		}
		else
		{
			if (m_uis.isActive())
				m_uis.onLeave(m_id, mousePt);
		}			
	}
}
