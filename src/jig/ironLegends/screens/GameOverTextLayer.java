package jig.ironLegends.screens;

import java.awt.geom.AffineTransform;

import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextLayer;
import jig.ironLegends.core.TextWriter;

import jig.engine.RenderingContext;

public class GameOverTextLayer extends ScreenTextLayer
{
	public GameOverTextLayer(Fonts fonts, GameProgress gameProgress)
	{
		super(fonts);
		m_gameProgress = gameProgress;
	}
	@Override
	public void render(TextWriter text) 
	{
		// TODO Auto-generated method stub
		text.setFont(m_fonts.inYourFaceFont);
		text.print("GAME OVER", -1, IronLegends.SCREEN_HEIGHT/2-60);
		
		String msg = "Your Score was: " + m_gameProgress.getTotalScore();
		text.setFont(m_fonts.instructionalFont);
		text.setY(IronLegends.SCREEN_HEIGHT/2);
		text.setLineStart(-1);
		text.println(msg);
		msg = "Press Enter to Continue";
		text.println(msg);
	}
	protected GameProgress m_gameProgress;
}

