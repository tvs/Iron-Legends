package jig.ironLegends.screens;

import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.HighScore;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;

public class GameInfoTextLayer extends ScreenTextLayer {
	protected Sprite m_gameInfoArea1;
	protected Sprite m_gameInfoArea2;
	protected Sprite m_gameInfoArea3;
	protected GameProgress m_gameProgress;
	protected HighScore m_highScore;
	protected IronLegends m_game;

	public GameInfoTextLayer(Fonts fonts, GameProgress gameProgress,
			HighScore highScore, IronLegends game) {
		super(fonts);
		m_game = game;
		m_gameProgress = gameProgress;
		m_highScore = highScore;

		m_gameInfoArea1 = new Sprite(IronLegends.SPRITE_SHEET + "#powerup-shader");
		m_gameInfoArea1.setPosition(new Vector2D(1 * (m_gameInfoArea1.getWidth() + 33), IronLegends.SCREEN_HEIGHT - m_gameInfoArea1.getHeight()));

		m_gameInfoArea2 = new Sprite(IronLegends.SPRITE_SHEET + "#powerup-shader");
		m_gameInfoArea2.setPosition(new Vector2D(2 * (m_gameInfoArea2.getWidth() + 33), IronLegends.SCREEN_HEIGHT - m_gameInfoArea2.getHeight()));

		m_gameInfoArea3 = new Sprite(IronLegends.SPRITE_SHEET + "#powerup-shader");
		m_gameInfoArea3.setPosition(new Vector2D(3 * (m_gameInfoArea3.getWidth() + 33), IronLegends.SCREEN_HEIGHT - m_gameInfoArea3.getHeight()));
	}
	
	@Override
	public void render(TextWriter text) {
		m_gameInfoArea1.render(text.getRC());
		m_gameInfoArea2.render(text.getRC());
		m_gameInfoArea3.render(text.getRC());
		text.setFont(m_fonts.scoreboardFont);

		text.setY((int) (m_gameInfoArea1.getPosition().getY() + 1));
		text.setLineStart((int) (m_gameInfoArea1.getPosition().getX() + 10));

		text.println("Map: " + m_gameProgress.getMapName());
		if (m_game.m_godmode) { // God mode on m_tank?
			text.println("** GOD Mode **");
		}
		
		text.setY((int) (m_gameInfoArea2.getPosition().getY() + 1));
		text.setLineStart((int) (m_gameInfoArea2.getPosition().getX() + 10));

		text.println("High Score: " + m_highScore.getHighScore());
		text.println("Score: " + m_gameProgress.m_levelProgress.getScore());


		text.setY((int) (m_gameInfoArea3.getPosition().getY() + 1));
		text.setLineStart((int) (m_gameInfoArea3.getPosition().getX() + 10));
		
		text.println("Lives: " + (m_gameProgress.getLivesRemaining()<0?"You're Dead!":m_gameProgress.getLivesRemaining()));
		text.println("Enemies: " + m_gameProgress.m_levelProgress.getTanksRemaining());

		text = null;
	}
}
