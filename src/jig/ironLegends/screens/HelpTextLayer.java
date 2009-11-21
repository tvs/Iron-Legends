package jig.ironLegends.screens;

import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;

public class HelpTextLayer extends ScreenTextLayer
{	
	Sprite m_weed;
	Sprite m_weedPU;
	Sprite m_pu1;
	Sprite m_pu2;
	Sprite m_mitko;
	
	public HelpTextLayer(Fonts fonts)
	{
		super(fonts);
		
		m_weed = new Sprite(IronLegends.HR_SPRITE_SHEET + "#weed1");
		m_weed.setPosition(new Vector2D(0,0));
		m_weed.setFrame(0);
		
		m_weedPU = new Sprite(IronLegends.HR_SPRITE_SHEET + "#weed1");
		m_weedPU.setPosition(new Vector2D(0,0));
		m_weedPU.setFrame(1);
		
		m_pu1 = new Sprite(IronLegends.HR_SPRITE_SHEET + "#powerup");
		m_pu1.setPosition(new Vector2D(0,0));
		m_pu1.setFrame(0);
		
		m_pu2 = new Sprite(IronLegends.HR_SPRITE_SHEET + "#powerup");
		m_pu2.setPosition(new Vector2D(0,0));
		m_pu2.setFrame(1);
		
		m_mitko = new Sprite(IronLegends.HR_SPRITE_SHEET + "#mitko");
		m_mitko.setPosition(new Vector2D(0,0));		
	}
	@Override
	public void render(TextWriter text) 
	{
		int y = IronLegends.SCREEN_HEIGHT/8;
		int x = IronLegends.SCREEN_WIDTH/5;

		text.setLineStart(-1);
		text.setY(y);
		text.setFont(m_fonts.titleFont);
		text.println("IRON LEGENDS");
		
		text.setFont(m_fonts.smInstructionalFont);
		text.setLineStart(x);
		text.println("");
		text.println("");
		// if single player
		text.println("Destroy the enemy tanks!");
		text.println("Collect powerups and ...");
		text.println("Use the arrow keys to navigate your character");
		text.println("");
		text.println("");
		text.println("Press Enter to return to the main screen");
		
		text.println("");
		text.println("");
	}
}
