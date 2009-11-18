package jig.ironLegends.core.ui;

import java.awt.Point;
import java.util.List;

import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.TextWriter;

public class Button extends Sprite 
{
	// TODO add text edit box (extend button?)
	// TODO add optional text to button (placement + optional centering)
	// NOTE easier if add text to button (then edit box can just continually update the text as keys are pressed)
	// NOTE could have button test for active and then just use a single UIState.. to reduce memory, but who cares for now
	protected int m_sx;
	protected int m_sy;
	MouseState m_uis = new MouseState();
	protected int m_id;
	public int getId() { return m_id;}
	protected IUIEvent m_sink;
	protected FontResource m_font;
	protected Point m_textPt;
	protected String m_sText = null;
	
	public Button(int id, int sx, int sy, String rsc)
	{
		super(rsc);
		setData(id, sx, sy);	
	}
	
	public Button(int id, int sx, int sy, List<ImageResource> rsc) {
		super(rsc);
		setData(id, sx, sy);
	}
	
	private void setData(int id, int sx, int sy) {
		m_sx = sx;
		m_sy = sy;
		m_id = id;
		
		setPosition(new Vector2D(sx, sy));
		
		m_sink = null;
		m_font = null;
		m_textPt = new Point(-1, -1);
	}
	
	public boolean hasFocus()
	{
		return m_uis.isActive();
	}
	public void setText(String text)
	{
		m_sText = text;
	}
	public void setFont(FontResource font)
	{
		m_font = font;
	}
	
	// x,y relative to top left of Button's sx, sy (x=-1->center x, y=-1->center y)
	public void initText(int x, int y, FontResource font)
	{
		m_font = font;
		
		m_textPt.x = x;
		m_textPt.y = y;
	}
	
	public void setSink(IUIEvent buttonSink)
	{
		m_sink = buttonSink;
	}
	
	@Override
	public void render(final RenderingContext rc) 
	{
		super.render(rc);
		// now render text
		if (m_sText != null && m_sText.length() > 0)
		{
			// render text
			TextWriter text = new TextWriter(rc);
			text.setFont(m_font);

			// if center, get width of button, width of text
			
			// NOTE x,y might be negative
			int x = m_textPt.x;
			int y = m_textPt.y;
			
			// center x
			if (m_textPt.x == -1)
			{
				int textWidth = m_font.getStringWidth(m_sText);
				int butWidth = getWidth();
				x = (butWidth - textWidth)/2;
			}

			// center y
			if (m_textPt.y == -1)
			{
				int textHeight = m_font.getHeight();
				int butHeight = getHeight();
				y = (butHeight - textHeight)/2;
			}
			
			text.print(m_sText, x + m_sx, y + m_sy);
			text = null;
		}
		
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
			{
				m_uis.onEnter(m_id, mousePt);
				if (m_sink != null)
				{
					m_sink.onEnter(m_id, mousePt);					
				}
			}
			
			m_uis.update(mouse, deltaMs, m_id);
			if (m_sink != null)
			{
				if (m_uis.wasLeftPressedDown())
					m_sink.onLeftDown(m_id, mousePt);
				if (m_uis.wasLeftReleased())
					m_sink.onLeftUp(m_id, mousePt);
				if (m_uis.wasLeftClicked())
					m_sink.onLeftClick(m_id, mousePt);
			}
		}
		else
		{
			if (m_uis.isActive())
			{
				m_uis.onLeave(m_id, mousePt);
				if (m_sink != null)
					m_sink.onLeave(m_id, mousePt);
			}
		}			
	}
}
