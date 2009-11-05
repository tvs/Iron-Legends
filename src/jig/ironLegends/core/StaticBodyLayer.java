package jig.ironLegends.core;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

import jig.engine.RenderingContext;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;

public abstract class StaticBodyLayer<V extends Body> extends AbstractBodyLayer<V> 
{
	protected boolean m_bWorld;
	
	/** Creates a new ViewableLayer. */
	public StaticBodyLayer() 
	{
		super();
		m_bWorld = false;
	}
	
	public StaticBodyLayer(final boolean bWorld) 
	{
		super();
		m_bWorld = bWorld;
	}

	public StaticBodyLayer(final int n) {
		super(n);
		m_bWorld = false;
	}
	
	public void setWorld(boolean bWorld)
	{
		m_bWorld = bWorld;
	}
	
	public boolean isWorld()
	{
		return m_bWorld;
	}
	
	@Override
	public void render(final RenderingContext rc) 
	{
		AffineTransform origTransform = rc.getTransform();
		
		if (m_bWorld == false)
		{
			rc.setTransform(AffineTransform.getTranslateInstance(0,0));
		}
		
		for (Iterator<V> i = members.iterator(); i.hasNext();) 
		{
			i.next().render(rc);
		}
		
		if (m_bWorld == false)
		{
			rc.setTransform(origTransform);
		}
		
	}
	
	public static class NoUpdate<V extends Body> extends StaticBodyLayer<V> 
	{
		
		/**
		 * This method is empty and does nothing.
		 * 
		 * @param deltaMs
		 *            ignored
		 */
		public void update(final long deltaMs) 
		{
		}
	}
	
	public static class IterativeUpdate<V extends Body> extends	StaticBodyLayer<V> 
	{
	
		/**
		 * This method is empty and does nothing.
		 * 
		 * @param deltaMs
		 *            ignored
		 */
		public void update(final long deltaMs) 
		{
			for (Body b : members) {
				b.update(deltaMs);
			}
		}
	}
}
