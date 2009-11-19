package jig.ironLegends.screens;

import jig.ironLegends.IronLegends;
import jig.ironLegends.PlayerInfo;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.HighScore;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;

public class SplashTextLayer extends ScreenTextLayer
{
	protected PlayerInfo m_playerInfo;
	
	Sprite m_weed;
	Sprite m_weedPU;
	Sprite m_pu1;
	Sprite m_pu2;
	Sprite m_mitko;
	
	public SplashTextLayer(Fonts fonts, HighScore highScore, PlayerInfo playerInfo)
	{
		super(fonts);
		m_highScore = highScore;
		m_playerInfo = playerInfo;
		// create icons?
		m_weed = new Sprite(IronLegends.SPRITE_SHEET + "#weed1");
		m_weed.setPosition(new Vector2D(0,0));
		m_weed.setFrame(0);
		
		m_weedPU = new Sprite(IronLegends.SPRITE_SHEET + "#weed1");
		m_weedPU.setPosition(new Vector2D(0,0));
		m_weedPU.setFrame(1);
		
		m_pu1 = new Sprite(IronLegends.SPRITE_SHEET + "#powerup");
		m_pu1.setPosition(new Vector2D(0,0));
		m_pu1.setFrame(0);
		
		m_pu2 = new Sprite(IronLegends.SPRITE_SHEET + "#powerup");
		m_pu2.setPosition(new Vector2D(0,0));
		m_pu2.setFrame(1);
		
		m_mitko = new Sprite(IronLegends.SPRITE_SHEET + "#mitko");
		m_mitko.setPosition(new Vector2D(0,0));		
	}
	
	protected int render(Sprite s, String msg, int startX, int y, TextWriter text)
	{
		int x = startX;
		
		s.setPosition(new Vector2D(x, y));
		s.render(text.getRC());
		x += s.getWidth() + 2;
		
		text.print(msg, x,y);
		
		x = startX;
		y += (s.getHeight()>text.getHeight()?s.getHeight():text.getHeight());
		
		return y;
	}
	@Override
	public void render(TextWriter text)
	{
		text.setY(0);
		text.setLineStart(-1);
		
		text.setFont(m_fonts.smInstructionalFont);
		text.println("");

		text.setFont(m_fonts.titleFont);
		// print IRON LEGENDS using sprites for artistic look
		text.println("IRON LEGENDS");
		
		text.setFont(m_fonts.instructionalFont);
		/*
		if (m_highScore.getPlayer() != null)
			text.println("High Score: " + m_highScore.getPlayer() + " - " + m_highScore.getHighScore());
		else
			text.println("High Score: " + "unknown" + "  " + m_highScore.getHighScore());
		 */
		if (m_highScore.getPlayer() != null)
			text.println("High Score: " + m_highScore.getHighScore()+ "  " + m_highScore.getPlayer());
		else
			text.println("High Score: " + m_highScore.getHighScore()+ "  " + "unknown" );
		
		text.setLineStart(-1);
		text.println("");
		text.println("Hello " + m_playerInfo.getName());
		text.println("");
		
		text.setFont(m_fonts.smInstructionalFont);
		text.setLineStart(IronLegends.SCREEN_WIDTH/10);
		text.println("");
		int y = text.getY();
		text.println("Press Enter to Play");
		text.println("Press F1 or 'h' for Help");
		text.println("Press 'c' to enter a player name");
		text.println("Press t to enter test screen");

		{
			int xStart = 7*IronLegends.SCREEN_WIDTH/12;

			text.setY(y);
			text.setLineStart(xStart);
			
			text.println("Required to complete level");
			text.print(m_weed);text.println(" Weed: collect to complete level");			
			text.print(m_weedPU);text.println(" Weed + stash: type 's' to use");

			text.println("");

			text.println("Optional items");
			text.print(m_pu1);text.println(" Courage: activated immediately");
			text.print(m_pu2);text.println(" Stash: collect (type 's' to use)");		
		}
		
		text.setY(3*IronLegends.SCREEN_HEIGHT/4);			
		
		text.setFont(m_fonts.scoreboardFont);
		
		text.setLineStart(30);
		int x = 0;
		text.print("Brought to you by: ");
		x = text.getX();
		text.println("Michael JP Persons");
		text.setLineStart(x);
		text.println("WSU Vancouver CS547 Fall 2009");
		text.setLineStart(30);

		text.setY(IronLegends.SCREEN_HEIGHT-110);
		text.println("And the following");

		text.println("the JIG Engine");
		text.println("JIG Pong Demo, Space Frenzy tutorial");
		text.println("MPpong, SAT");
		text.println("Spider art by Derek Stewart (www.stoobytoons.com)");
	}
	
	protected HighScore m_highScore;
}