package jig.ironLegends.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;

public class GameScreens 
{
	public GameScreens()
	{
		m_screens = new LinkedList<GameScreen>();
		m_transitions = new LinkedList<ScreenTransition>();
		m_activeScreen = 0;
	}

	public void setActiveScreen(int activeScreen)
	{
		m_activeScreen = activeScreen;		
	}
	
	public boolean addScreen(GameScreen gameScreen)
	{
		if (m_screens.contains(gameScreen))
			return false;
		
		m_screens.add(gameScreen);
		
		return true;
	}
	
	// note: could add a transition that is timed .... but hide that with a separate call
	// and then internally have 2 different types of transitions
	public void addTransition(int from, int to, String trigger)
	{
		m_transitions.add(new ScreenTransition(from, to, trigger));
	}
	
	public GameScreen getActiveScreen()
	{
		return getScreen(m_activeScreen);
	}
	
	public GameScreen getScreen(int screen)
	{
		Iterator<GameScreen> iter = m_screens.iterator();
		while (iter.hasNext())
		{
			GameScreen gs = iter.next();
			if (screen == gs.name())
			{
				return gs;
			}
		}
		
		return null;
	}
	
	public class ScreenTransition
	{
		public ScreenTransition(int from, int to, String trigger)
		{
			m_from = from;
			m_to = to;
			m_trigger = trigger;
		}
		
		public int m_from;
		public int m_to;
		public String m_trigger;
	}
	
	public ScreenTransition transition(KeyCommands keyCmds)
	{
		Iterator<ScreenTransition> iter = m_transitions.iterator();
		
		while (iter.hasNext())
		{
			ScreenTransition t = iter.next();
			if (keyCmds.wasPressed(t.m_trigger))
			{
				t = transition(t.m_trigger);
				if (t != null)
					return t;
			}		
		}
				
		return null;
	}
	
	public ScreenTransition transition(String trigger)
	{
		
		// based on current screen and trigger event, transition to new screen
		Iterator<ScreenTransition> iter = m_transitions.iterator();
		
		while (iter.hasNext())
		{
			ScreenTransition t = iter.next();
			if (t.m_from == m_activeScreen)
			{
				if (t.m_trigger.equals(trigger))
				{
					m_activeScreen = t.m_to;
					return t;
				}
			}
		}
				
		return null;
	}
	
	public int activeScreen(){return m_activeScreen;}
	
	protected LinkedList<ScreenTransition> m_transitions;
	
	protected LinkedList<GameScreen> m_screens;
	
	protected int m_activeScreen;
}
