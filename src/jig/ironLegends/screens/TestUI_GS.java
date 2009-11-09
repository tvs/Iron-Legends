package jig.ironLegends.screens;

import java.awt.image.renderable.RenderContext;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ui.Button;
import jig.ironLegends.core.ui.TextEditBox;

public class TestUI_GS extends GameScreen 
{

	public TestUI_GS(int name, Fonts fonts) 
	{
		super(name);

		// button test
		// TODO put collection of buttons as part of each GameScreen?..
		m_btnTest = new Button(-1, 200, 100, IronLegends.SPRITE_SHEET + "#powerup");
		m_btnTest.setFont(fonts.smInstructionalFont);
		//m_btnTest.initText(-1,-1, m_fonts.smInstructionalFont);
		m_btnTest.setText("Centered Button Text");
		
		m_editBoxTest = new TextEditBox(fonts.smInstructionalFont, -2, 200, 300, IronLegends.SPRITE_SHEET + "#testEditBox");
		m_editBoxTest.setText("Edit Box");
		
		m_btnExit = new Button(-1, 200, 400, IronLegends.SPRITE_SHEET + "#testEditBox");
		m_btnExit.setFont(fonts.instructionalFont);
		m_btnExit.setText("EXIT");
	}
	
	@Override
	public void render(RenderingContext rc)
	{
		m_btnTest.render(rc);
		m_btnExit.render(rc);
		m_editBoxTest.render(rc);
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		m_btnTest.update(mouse, deltaMs);
		if (m_btnTest.wasLeftClicked())
			System.out.println("button: " + m_btnTest.getId() + " was left clicked");
		
		m_editBoxTest.update(mouse, deltaMs);
		if (m_editBoxTest.isActive())
		{
			m_editBoxTest.processInput(keyCmds);
		}
		m_btnExit.update(mouse, deltaMs);
		if (m_btnExit.wasLeftClicked())
		{
			return IronLegends.SPLASH_SCREEN;
		}
		
		return name();		
	}

	
	protected TextEditBox m_editBoxTest;
	protected Button m_btnTest;
	protected Button m_btnExit;
}
