package jig.ironLegends.screens;

import jig.engine.RenderingContext;
import jig.ironLegends.GameProgress;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextLayer;
import jig.ironLegends.core.TextWriter;

public class CustomizePlayerTextLayer  extends ScreenTextLayer
{
	protected GameProgress m_gameProgress;
	private String m_sCollectedName;
	
	public CustomizePlayerTextLayer(Fonts fonts, GameProgress gameProgress)
	{
		super(fonts);
		m_gameProgress = gameProgress;
	}
	@Override
	public void render(TextWriter text) 
	{
		text.setFont(m_fonts.instructionalFont);
		
		text.setY(10);
		text.setLineStart(200);
		
		text.println("Enter Name:");
		if (m_sCollectedName != null)
			text.println(m_sCollectedName);
		
		text.println("");
		text.println("");
		text.setFont(m_fonts.smInstructionalFont);
		text.println("You may use the backspace to delete the name");
		text.println("Press the escape key to cancel");
		text.println("Press enter to save");
		text = null;
	}
	public void setCollectedName(String sCollectedName) 
	{
		m_sCollectedName = sCollectedName;		
	}
}
