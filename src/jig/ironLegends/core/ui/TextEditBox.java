package jig.ironLegends.core.ui;

import java.util.Iterator;
import java.util.Vector;

import jig.engine.FontResource;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.KeyState;
import jig.ironLegends.core.KeyCommands.FinalPair;

public class TextEditBox extends Button 
{
	protected String m_sCollectedText;
	
	// onEnter start collecting characters
	// onLeave store characters
	// backspace-> delete a character
	// enter->save ? or ?
	
	public TextEditBox(FontResource font, int id, int sx, int sy, String rsc) 
	{
		super(id, sx, sy, rsc);
		super.setFont(font);
		
		m_sCollectedText = "";
	}
	
	@Override
	public void setText(String text)
	{
		m_sCollectedText = text;
		super.setText(text);
	}
	public String getText()
	{
		return m_sCollectedText;
	}			
	public void processInput(KeyCommands keyCmds)
	{
		if (!hasFocus())
			return;
			
		Vector<FinalPair<String, KeyState>> pressedKeys = keyCmds.collectPressedKeyCodes();
		Iterator<FinalPair<String,KeyState> > iter = pressedKeys.iterator();
		if (iter.hasNext())
		{
			while (iter.hasNext())
			{
				FinalPair<String,KeyState> k = iter.next();
				if (k.m_o1.equals("enter"))
				{
					/*
					if (m_sCollectedText.length() > 0)
					{
						m_sPlayer = m_sCollectedText;
						m_playerInfo.setName(m_sPlayer);
					}
					*/
					break;
				}
				
				else if (k.m_o1.equals("backspace") || 
						k.m_o1.equals("left"))
				{
					// remove last character
					if (m_sCollectedText.length() > 0)
					{
						m_sCollectedText = m_sCollectedText.substring(0, m_sCollectedText.length()-1);
						super.setText(m_sCollectedText);
					}
				}
				// escape -> don't save, only save on enter
				else if (k.m_o1.length() == 1)
				{
					m_sCollectedText += k.m_o1;
					super.setText(m_sCollectedText);
					//System.out.print(k.m_o1);
				}
				else if (k.m_o1.equals("space"))
				{
					m_sCollectedText += " ";
					super.setText(m_sCollectedText);					
				}
			}
			//System.out.println("");
		}
	}
}
