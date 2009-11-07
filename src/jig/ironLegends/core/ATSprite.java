package jig.ironLegends.core;

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.Sprite;

/**
 * Position is top left of sprite
 */
public class ATSprite extends Sprite
{
	public double m_rotationRad = 0;
	public double m_halfWidth;
	public double m_halfHeight;
	
	protected void InitDimensions()
	{
		m_halfWidth = width / 2.0;
		m_halfHeight = height / 2.0;			
	}
	
	ATSprite(final List<ImageResource> frameset)
	{
		super(frameset);
		InitDimensions();
	}
	
	ATSprite(final String rsc)
	{
		super(rsc);
		InitDimensions();
	}
	
	void setRotation(double rotRad)
	{
		m_rotationRad = rotRad;
	}
	double getRotation(){return m_rotationRad;}
	
	public void render(final RenderingContext rc, double dBaseRotationRad)
	{
		double prevRotation = m_rotationRad;
		m_rotationRad += dBaseRotationRad
		;
		render(rc);
		m_rotationRad = prevRotation;
	}
	
	@Override
	public void render(final RenderingContext rc)
	{
		// adjust at
		AffineTransform at = null;
		
		if (m_rotationRad != 0)
		{
			// offset to position
			// offset to center
			// rotate
			// offset back
			at = AffineTransform.getTranslateInstance(position.getX()
					+ m_halfWidth, position.getY() + m_halfHeight);

			at.rotate(m_rotationRad);
			at.translate(-m_halfWidth, -m_halfHeight);
		}
		else
			at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
		
		frames.get(visibleFrame).render(rc,at);
	}
}