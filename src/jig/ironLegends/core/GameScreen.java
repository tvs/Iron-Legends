package jig.ironLegends.core;

import java.util.Iterator;
import java.util.LinkedList;

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

	public void activate()
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
	
	public void processInput(KeyCommands keyCmds)
	{
	}
	
	protected LinkedList<ViewableLayer> m_viewableLayers = new LinkedList<ViewableLayer>();
	protected int m_name;
}
