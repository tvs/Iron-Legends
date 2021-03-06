package jig.ironLegends.core.ui;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;
import jig.ironLegends.mapEditor.TileButton;

public class ButtonToolbar <B extends Button>
{
	private int m_nextsy = 0;
	// for now just a vertical toolbar
	public ButtonToolbar(int sx, int sy)
	{
		m_sx = sx;
		m_sy = sy;
		
		m_buttons = new Vector<B>();
	}

	public void setPosition(int sx, int sy)
	{
		int prevX = m_sx;
		int prevY = m_sy;
		
		m_sx = sx;
		m_sy = sy;
		// adjust button positions based on top button idx (m_scrolledOffsetY)
		
		{
			Iterator<B> iter = m_buttons.iterator();
			while (iter.hasNext())
			{
				Button b = iter.next();
				//b.setPosition(new Vector2D(m_sx, m_sy + b.getPosition().getY()+m_scrolledOffsetY));
				b.setPosition(new Vector2D(b.getPosition().getX()-prevX+m_sx, b.getPosition().getY()-prevY+m_sy));
			}
		}
	}
	public Point getPosition() 
	{
		return new Point(m_sx, m_sy);
	}

	public int append(B b)
	{
		m_buttons.add(b);
		
		b.setPosition(new Vector2D(m_sx, m_sy + m_nextsy));
		m_totalSpriteHeight += b.getHeight();
		
		if (m_maxSpriteHeight < b.getHeight())
			m_maxSpriteHeight = b.getHeight();
		if (m_maxSpriteWidth < b.getWidth())
			m_maxSpriteWidth = b.getWidth();
		
		m_nextsy  += b.getHeight();
		
		return m_buttons.size()-1;
	}
	
	public B getButton(int id)
	{
		B b = null;
		Iterator<B> iter = iterator();
		while (iter.hasNext())
		{
			b = iter.next();
			if (b.getId() == id)
				return b;			
		}
		return null;		
	}
	
	public Iterator<B> iterator()
	{
		return m_buttons.iterator();
	}
	
	Vector<B> m_buttons;
	int m_sx;
	int m_sy;
	int m_topSpriteIdx = 0;
	int m_maxSpriteWidth = 0;
	int m_maxSpriteHeight = 0;
	int m_totalSpriteHeight = 0;
	
	public void scrollDown(int i) 
	{
		if (m_topSpriteIdx < m_buttons.size()-1)
		{
			B b = m_buttons.get(m_topSpriteIdx);
			int h = b.getHeight();
			
			setPosition(m_sx, m_sy-h);
			m_topSpriteIdx++;
		}
		
	}

	public void scrollUp(int i) 
	{
		if (m_topSpriteIdx > 0)
		{
			m_topSpriteIdx--;
			B b = m_buttons.get(m_topSpriteIdx);
			int h = b.getHeight();
			setPosition(m_sx, m_sy+h);
		}
	}

	public void render(RenderingContext rc) 
	{
		//double y = -m_buttons.get(m_topSpriteIdx).getPosition().getY();
		//double y = 0;
		
		Iterator<B> btIter = m_buttons.iterator();
		while (btIter.hasNext())
		{
			Button b = btIter.next();
			//Vector2D vOrig = b.getPosition();
			//b.setPosition(new Vector2D(vOrig.getX(), vOrig.getY() + y));
			b.render(rc);
			//b.setPosition(vOrig);
		}
	}

	public int getMaxHeight()
	{
		return m_maxSpriteHeight;
	}
	public int getMaxWidth() 
	{
		return m_maxSpriteWidth;
	}

	public int getHeight() {
		return m_totalSpriteHeight;
	}

	public TileButton update(Mouse mouse, long deltaMs) {
		
		return null;
	}

	public void clear() {
		m_nextsy = 0;
		this.m_buttons.clear();
	}
}
