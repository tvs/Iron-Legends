package jig.ironLegends;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.TextWriter;

public class HealthBar {

	private Sprite m_bg;
	private Sprite m_fg;
	private int m_ticks;
	private boolean m_bHorizontal;
	private int m_maxHealth;
	private Vector2D m_pos;
	
	public HealthBar(Vector2D pos, int maxHealth, int ticks, boolean bHorizontal)
	{
		m_bg = new Sprite("health_total");
		m_fg = new Sprite("health_remaining");
		m_pos = pos;
		m_maxHealth = maxHealth;
		m_ticks = ticks;
		m_bHorizontal = bHorizontal;
	}
	public HealthBar()
	{
		this(new Vector2D(0,0), 1,1,true);
	}
	public void render(RenderingContext rc, int health)
	{
		render(rc, m_pos, health, m_maxHealth, m_ticks, m_bHorizontal);
		
	}
	public void render(RenderingContext rc, Vector2D pos, int health, int maxHealth, int ticks, boolean bHorizontal)
	{
		render(rc, pos.getX(), pos.getY(), health, maxHealth, ticks, bHorizontal);
	}
	
	public void render(RenderingContext rc, double x, double y, int health, int maxHealth, int ticks, boolean bHorizontal)
	{
		// render based health and max health
		// vertical or horizontal using jig shapes?
		TextWriter text = new TextWriter(rc);
		
		text.setY((int)y);
		text.setLineStart((int)x);
		
		int tickSize = maxHealth/ticks;
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
