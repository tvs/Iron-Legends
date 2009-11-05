package jig.ironLegends.screens;

import java.util.Iterator;
import java.util.Vector;

import jig.ironLegends.PlayerInfo;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.KeyState;
import jig.ironLegends.core.KeyCommands.FinalPair;

public class CustomizePlayerGS extends GameScreen 
{

	String m_sPlayer;
	PlayerInfo m_playerInfo;
	
	public CustomizePlayerGS(int name, PlayerInfo playerInfo) 
	{
		super(name);
		m_textLayer = null;
		m_sPlayer = playerInfo.getName();
		m_sCollectedName = "";
		m_playerInfo = playerInfo;
	}

	public void setTextLayer(CustomizePlayerTextLayer textLayer)
	{
		m_textLayer = textLayer;
		m_textLayer.setCollectedName(m_sCollectedName);
	}
	
	@Override
	public void deactivate()
	{
		m_sCollectedName = "";
	}

	@Override
	public void activate()
	{
		m_sCollectedName = "";
		m_textLayer.setCollectedName(m_sCollectedName);
	}
	@Override
	public void processInput(KeyCommands keyCmds)
	{
		Vector<FinalPair<String, KeyState>> pressedKeys = keyCmds.collectPressedKeyCodes();
		Iterator<FinalPair<String,KeyState> > iter = pressedKeys.iterator();
		if (iter.hasNext())
		{
			while (iter.hasNext())
			{
				FinalPair<String,KeyState> k = iter.next();
				if (k.m_o1.equals("enter"))
				{
					// save into text layer					
					//System.out.println("Saved name of: " + m_sCollectedName);
					if (m_sCollectedName.length() > 0)
					{
						m_sPlayer = m_sCollectedName;
						m_playerInfo.setName(m_sPlayer);
					}
					break;
				}
				
				else if (k.m_o1.equals("backspace") || 
						k.m_o1.equals("left"))
				{
					// remove last character
					if (m_sCollectedName.length() > 0)
					{
						m_sCollectedName = m_sCollectedName.substring(0, m_sCollectedName.length()-1);
						m_textLayer.setCollectedName(m_sCollectedName);
					}
				}
				// escape -> don't save, only save on enter
				else if (k.m_o1.length() == 1)
				{
					m_sCollectedName += k.m_o1;
					m_textLayer.setCollectedName(m_sCollectedName);
					//System.out.print(k.m_o1);
				}
			}
			//System.out.println("");
		}
	}
	protected String m_sCollectedName;
	
	public CustomizePlayerTextLayer m_textLayer;
}
