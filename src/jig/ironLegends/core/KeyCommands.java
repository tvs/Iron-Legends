package jig.ironLegends.core;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

public class KeyCommands 
{

	public KeyCommands()
	{
		m_keys = new TreeMap<String, KeyState>();
	}
	
	public void addCommand(String command, int key)
	{
		if (m_keys.containsKey(command))
			return;
		
		m_keys.put(command, new KeyState(key));		
	}
	
	public void update(jig.engine.Keyboard keyboard)
	{
		Iterator<Map.Entry<String, KeyState> > iter = m_keys.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<String, KeyState> en = iter.next();
			KeyState ks = (KeyState)en.getValue();
			ks.updateState(keyboard);			
		}
	}
	
	public class FinalPair<T1, T2>
	{
		public FinalPair(T1 o1, T2 o2)
		{
			m_o1 = o1;
			m_o2 = o2;
		}
		
		public final T1 m_o1;
		public final T2 m_o2;
	}
	
	public Vector<FinalPair<String, KeyState> > collectPressedKeyCodes()
	{
		
		Vector<FinalPair<String, KeyState>> keyCodes = new Vector<FinalPair<String, KeyState>>();
		Iterator<Map.Entry<String, KeyState> > iter = m_keys.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<String, KeyState> en = iter.next();
			
			KeyState ks = (KeyState)en.getValue();
			if (ks.wasPressed())
			{
				keyCodes.add(new FinalPair<String, KeyState>(en.getKey(), ks));
			}
		}
		
		return keyCodes;		
	}
	
	public boolean isPressed(String command)
	{
		KeyState ks = m_keys.get(command);
		if (ks == null)
			return false;
		return ks.isPressed();		
	}
	public boolean wasPressed(String command)
	{
		KeyState ks = m_keys.get(command);
		if (ks == null)
			return false;
		return ks.wasPressed();				
	}
	public boolean wasReleased(String command)
	{
		KeyState ks = m_keys.get(command);
		if (ks == null)
			return false;
		return ks.wasReleased();		
	}
	public boolean wasToggled(String command)
	{
		KeyState ks = m_keys.get(command);
		if (ks == null)
			return false;
		return ks.wasToggled();				
	}
	
	protected SortedMap<String,KeyState> m_keys;
}

