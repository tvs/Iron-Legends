package jig.ironLegends.core;

import java.awt.geom.AffineTransform;

import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;

public class TextWriter 
{
	public TextWriter(RenderingContext rc)
	{
		m_rc = rc;

		m_curFont = null;
		
		m_y = 0;
		m_x = 0;
		m_lineStart = 0;
		
		m_fontHeight = 0;
		m_ulx = 0;
		m_uly = 0;
		
		m_width 	= IronLegends.SCREEN_WIDTH;
		m_height 	= IronLegends.SCREEN_HEIGHT;		
	}

	public RenderingContext getRC(){return m_rc;}
	public int getHeight()
	{
		return m_fontHeight;
	}
	public void setY(int y)
	{
		m_y = y;
	}
	public void setLineStart(int x)
	{
		if (m_x == m_lineStart)
			m_x = x;				
		m_lineStart = x;
	}
	
	public void setFont(FontResource font)
	{
		m_curFont = font;
		m_fontHeight = font.getHeight();
	}
	public void newline()
	{
		m_y += m_fontHeight;
		m_x = m_lineStart;
	}
	public void println(String msg)
	{
		print(msg);
		m_y += m_fontHeight;
		m_x = m_lineStart;
	}
	public int getX(){return m_x;}
	public int getY(){return m_y;}

	public void print(Sprite s)
	{
		int w = s.getWidth();
		int x = m_x;
		
		if (x == -1)
			x = m_width / 2 - w/ 2;

		s.setPosition(new Vector2D(x,m_y));
		s.render(m_rc);
		
		m_x = x + w;		
	}
	
	public void print(String msg, int x, int y)
	{
		m_curFont.render(msg, m_rc, AffineTransform.getTranslateInstance(x, y));
	}
	public void print(String msg)
	{
		int w = m_curFont.getStringWidth(msg);
		int x = m_x;
		
		if (x == -1)
			x = m_width / 2 - m_curFont.getStringWidth(msg) / 2;

		print(msg, x, m_y);
		m_x = x + w;
	}
			
	protected FontResource m_curFont;
	protected RenderingContext m_rc;
	
	protected int m_width;
	protected int m_height;
	protected int m_ulx;
	protected int m_uly;
	
	protected int m_y;
	protected int m_lineStart;
	protected int m_x;
	protected int m_fontHeight;
}
