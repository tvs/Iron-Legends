package jig.ironLegends.screens;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.PlayerInfo;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.TextWriter;
import jig.ironLegends.core.ui.RolloverButton;

public class HelpScreen extends GameScreen {
	protected PlayerInfo m_playerInfo;
	
	protected Sprite bg;
	protected Sprite banner;
	protected Sprite shader;
	protected Sprite help_text;
	protected RolloverButton bbutton;
	protected Fonts m_fonts;
		
	public HelpScreen(int name, Fonts fonts) {
		super(name);
		m_fonts = fonts;
		
		// TODO share common elements between frames
		bg = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#background");
		bg.setPosition(new Vector2D(0, 0));
		
		banner = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#banner");
		banner.setPosition(new Vector2D(34, 0));
		
		shader = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#big-shader");
		shader.setPosition(new Vector2D(96, 52));
		
		help_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#help-text");
		help_text.setPosition(new Vector2D(365, 28));
		
		bbutton = new RolloverButton(-3, 0, 535,
			IronLegends.SCREEN_SPRITE_SHEET + "#back-button");
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.render(rc);
		banner.render(rc);
		shader.render(rc);
		help_text.render(rc);
		bbutton.render(rc);
		
		// temporarily add text
		TextWriter text = new TextWriter(rc);
		
		text.setY(50);
		text.setLineStart(125);
		
		
		text.setFont(m_fonts.instructionalFont);
		text.println("Audio Credits");
		
		text.setFont(m_fonts.smInstructionalFont);
		text.println("");
		text.println("Creative Commons Sampling Plus 1.0 License");
		text.setLineStart(150);
		text.println("tank explosion: funhouse from freesound.org");
		text.println("bullet hits: Rock Savage from freesound.org");
		text.setLineStart(125);
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		bbutton.update(mouse, deltaMs);
		if (bbutton.wasLeftClicked())
			return IronLegends.SPLASH_SCREEN;
		return name();		
	}
}
