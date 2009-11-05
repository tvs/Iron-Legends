package jig.ironLegends.core;

import java.awt.geom.AffineTransform;

import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.ViewableLayer;
import jig.ironLegends.IronLegends;

public class TextLayer implements ViewableLayer
{
	protected void RenderText(String msg, FontResource font, int x, int y,
			final RenderingContext rc) 
	{
		if (x == -1)
			x = IronLegends.SCREEN_WIDTH / 2 - font.getStringWidth(msg) / 2;

		font.render(msg, rc, AffineTransform.getTranslateInstance(x, y));
	}

	protected boolean m_bWorld;
	public TextLayer(Fonts fonts)
	{
		m_fonts = fonts;
		m_bWorld = true;
	}
	public boolean isWorld()
	{
		return m_bWorld;
	}
	@Override
	public void render(RenderingContext rc) 
	{
	}

	@Override
	public void update(long deltaMs) {}

	@Override
	public boolean isActive() {return true;}

	@Override
	public void setActivation(boolean a) {}
	
	protected Fonts m_fonts;
}