package jig.ironLegends.screens;

import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;

public class GameWonTextLayer extends ScreenTextLayer
{
	public GameWonTextLayer(Fonts fonts, GameProgress gameProgress)
	{
		super(fonts);
		m_gameProgress = gameProgress;
	}
	
	@Override
	public void render(TextWriter text)
	{
		text.setFont(m_fonts.titleFont);
		
		text.setY(IronLegends.SCREEN_HEIGHT/4);
		text.setLineStart(-1);
		text.println("Congratulations");
		text.println("You WON!!");
	}
	
	protected GameProgress m_gameProgress;
}
