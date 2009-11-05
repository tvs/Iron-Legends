package jig.ironLegends.core;

import java.awt.geom.AffineTransform;

import jig.engine.RenderingContext;

public class ScreenTextLayer extends TextLayer 
{
	public ScreenTextLayer(Fonts fonts)
	{
		super(fonts);
	}
	
	public void render(TextWriter text)
	{
	}
	
	@Override
	public void render(RenderingContext rc)
	{
		AffineTransform at = rc.getTransform();
		
		rc.setTransform(AffineTransform.getTranslateInstance(0,0));
		
		TextWriter text = new TextWriter(rc);
		render(text);
		text = null;
		rc.setTransform(at);
	}
	
}
