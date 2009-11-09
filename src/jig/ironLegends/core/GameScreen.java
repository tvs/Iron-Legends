package jig.ironLegends.core;

import java.awt.image.renderable.RenderContext;
import java.util.Iterator;
import java.util.LinkedList;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.ViewableLayer;

public class GameScreen 
{
	public GameScreen(int name)
	{
		m_name = name;
	}

	public void deactivate()
	{
	}

	public void activate(int prevScreen)
	{
	}
	
	public int name() {return m_name;}
	public void addViewableLayer(ViewableLayer layer)
	{
		m_viewableLayers.add(layer);
	}
	public Iterator<ViewableLayer> getViewableLayers()
	{
		return m_viewableLayers.iterator();
	}
	
	// return to indicate screen to transition to (may be self)
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		return name();
	}
	
	public void render(RenderingContext rc)
	{
	}
	
	protected LinkedList<ViewableLayer> m_viewableLayers = new LinkedList<ViewableLayer>();
	protected int m_name;
}
