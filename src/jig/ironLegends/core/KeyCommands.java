package jig.ironLegends.core;

import java.awt.event.KeyEvent;
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

	public void addAlphabet()
	{
		addCommand("a", KeyEvent.VK_A);
		addCommand("b", KeyEvent.VK_B);
		addCommand("c", KeyEvent.VK_C);
		addCommand("d", KeyEvent.VK_D);
		addCommand("e", KeyEvent.VK_E);
		addCommand("f", KeyEvent.VK_F);
		addCommand("g", KeyEvent.VK_G);
		addCommand("h", KeyEvent.VK_H);
		addCommand("i", KeyEvent.VK_I);
		addCommand("j", KeyEvent.VK_J);
		addCommand("k", KeyEvent.VK_K);
		addCommand("l", KeyEvent.VK_L);
		addCommand("m", KeyEvent.VK_M);
		addCommand("n", KeyEvent.VK_N);
		addCommand("o", KeyEvent.VK_O);
		addCommand("p", KeyEvent.VK_P);
		addCommand("q", KeyEvent.VK_Q);
		addCommand("r", KeyEvent.VK_R);
		addCommand("s", KeyEvent.VK_S);
		addCommand("t", KeyEvent.VK_T);
		addCommand("u", KeyEvent.VK_U);
		addCommand("v", KeyEvent.VK_V);
		addCommand("w", KeyEvent.VK_W);
		addCommand("x", KeyEvent.VK_X);
		addCommand("y", KeyEvent.VK_Y);
		addCommand("z", KeyEvent.VK_Z);
		addCommand(" ", KeyEvent.VK_SPACE);
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

