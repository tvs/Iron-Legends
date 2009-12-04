package jig.ironLegends;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.TextWriter;

public class HealthBar {

	Sprite m_bg;
	Sprite m_fg;
	
	public HealthBar()
	{
		m_bg = new Sprite("health_total");
		m_fg = new Sprite("health_remaining");
	}
	public void render(RenderingContext rc, Vector2D pos, int health, int maxHealth, int tickSize, boolean bHorizontal)
	{
		render(rc, pos.getX(), pos.getY(), health, maxHealth, tickSize, bHorizontal);
	}
	
	public void render(RenderingContext rc, double x, double y, int health, int maxHealth, int tickSize, boolean bHorizontal)
	{
		// render based health and max health
		// vertical or horizontal using jig shapes?
		TextWriter text = new TextWriter(rc);
		
		text.setY((int)y);
		text.setLineStart((int)x);
		
		int bgTicks = maxHealth/tickSize;
		int fgTicks = health/tickSize;
		if (bHorizontal)
		{
			for (int i = 0; i < fgTicks; ++i)
			{
				text.print(m_fg);
			}
			for (int i = fgTicks; i < bgTicks; ++i)
			{
				text.print(m_bg);
			}
		}
		else
		{
			for (int i = 0; i < fgTicks; ++i)
			{
				text.println(m_fg);
			}
			for (int i = fgTicks; i < bgTicks; ++i)
			{
				text.println(m_bg);
			}
		}
		
		text = null;
	}
}
