package jig.ironLegends.screens;

import java.awt.Color;
import java.util.Iterator;

import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.HighScore;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;

import jig.engine.PaintableCanvas;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class GameInfoTextLayer extends ScreenTextLayer {
	// protected PaintableCanvas m_gameInfoCanvas;
	protected VanillaAARectangle m_gameInfoArea;
	protected GameProgress m_gameProgress;
	protected HighScore m_highScore;
	protected IronLegends m_game;

	public GameInfoTextLayer(Fonts fonts, GameProgress gameProgress,
			HighScore highScore, IronLegends game) {
		super(fonts);
		m_game = game;

		m_gameProgress = gameProgress;
		m_highScore = highScore;

		PaintableCanvas m_gameInfoCanvas;
		m_gameInfoCanvas = new PaintableCanvas(200, 600, 1, new Color(128, 128,
				128));

		m_gameInfoCanvas.setWorkingFrame(0);
		m_gameInfoCanvas
				.fillRectangle(0, 0, 200, 600, new Color(128, 128, 128));
		m_gameInfoCanvas.loadFrames("blankScreen");

		m_gameInfoArea = new VanillaAARectangle("blankScreen") {
			@Override
			public void update(long deltaMs) {
				// TODO Auto-generated method stub

			}
		};

		m_gameInfoArea.setPosition(new Vector2D(600, 0));
	}

	@Override
	public void render(TextWriter text) {
		text.setFont(m_fonts.gameInfoFont);

		text.setY(10);
		text.setLineStart(650);

		//text.println("Total Score: " + m_gameProgress.getTotalScore());
		
		text.println("Map: " + m_gameProgress.getMapName());
		text.println("High Score: " + m_highScore.getHighScore());
		text.println("Score: "
				+ m_gameProgress.m_levelProgress.getScore());

		// TODO: put mini tank icons for lives remaining (print the sprite)
		text.println("Lives: " + (m_gameProgress.getLivesRemaining()<0?"You're Dead!":m_gameProgress.getLivesRemaining()));
		text.println("Enemies: " + m_gameProgress.m_levelProgress.getTanksRemaining());
		
		// God mode on m_tank?
		if (m_game.m_godmode)
		{
			text.println("GOD mode active");
		}
		
		{
			text.setLineStart(10);
			text.println("");
			
			Iterator<String> iter = m_game.m_availableMaps.iterator();
			while (iter.hasNext())
			{
				String mapFile = iter.next();
				text.println(mapFile);
			}			
		}

		text.setY(300);
		text = null;
	}
}
