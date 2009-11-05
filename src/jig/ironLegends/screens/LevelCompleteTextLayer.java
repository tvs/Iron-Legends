package jig.ironLegends.screens;

import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextLayer;
import jig.ironLegends.core.TextWriter;
import jig.engine.RenderingContext;

public class LevelCompleteTextLayer  extends ScreenTextLayer
{
	GameProgress m_gameProgress;
	public LevelCompleteTextLayer(Fonts fonts, GameProgress gameProgress)
	{
		super(fonts);
		m_gameProgress = gameProgress;
	}
	
	@Override
	public void render(TextWriter text)
	{
		
		text.setFont(m_fonts.titleFont);
		text.setY(IronLegends.WORLD_HEIGHT/8);
		text.setLineStart(-1);
		
		text.println("Level " + (m_gameProgress.getCurLevel()-1) + " complete");
	}	
}
