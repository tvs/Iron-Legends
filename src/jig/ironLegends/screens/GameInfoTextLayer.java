package jig.ironLegends.screens;

import java.awt.Color;
import jig.ironLegends.GameProgress;
import jig.ironLegends.Mitko;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.HighScore;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;

import jig.engine.PaintableCanvas;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class GameInfoTextLayer extends ScreenTextLayer 
{
	//protected PaintableCanvas 		m_gameInfoCanvas;
	protected VanillaAARectangle	m_gameInfoArea;
	protected GameProgress m_gameProgress;
	protected Mitko m_mitko;
	protected HighScore m_highScore;

	public GameInfoTextLayer(Fonts fonts, GameProgress gameProgress, Mitko mitko, HighScore highScore) 
	{
		super(fonts);
		
		m_mitko = mitko;
		m_gameProgress = gameProgress;
		m_highScore = highScore;
		
		PaintableCanvas 		m_gameInfoCanvas;
		m_gameInfoCanvas  = new PaintableCanvas(200, 600, 1, new Color(128,128,128));
		
		m_gameInfoCanvas.setWorkingFrame(0);
		m_gameInfoCanvas.fillRectangle(0,0, 200, 600, new Color(128,128,128));
		m_gameInfoCanvas.loadFrames("blankScreen");
		
		m_gameInfoArea = new VanillaAARectangle("blankScreen") 
		{		
			@Override
			public void update(long deltaMs) {
				// TODO Auto-generated method stub
				
			}
		};
		
		m_gameInfoArea.setPosition(new Vector2D(600,0));
	}
	
	@Override
	public void render(TextWriter text) 
	{
		text.setFont(m_fonts.gameInfoFont);
		
		text.setY(10);
		text.setLineStart(650);
		
		text.println("Total Score: " + m_gameProgress.getTotalScore());
		
		//text.println("Map: " + m_gameProgress.getCurLevel());
		text.println("Level Score: " + m_gameProgress.m_levelProgress.getScore());
		
		
		// TODO: put mini tank icons for lives remaining (print the sprite) 
		text.println("Lives Rem: " + m_gameProgress.getLivesRemaining());
				
		text.setY(300);
		text.println("High Score: " + m_highScore.getHighScore());
		text = null;
	}	
}
