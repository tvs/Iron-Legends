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
import jig.ironLegends.core.ui.RolloverButton;

public class HelpScreen extends GameScreen {
	protected PlayerInfo m_playerInfo;
	
	Sprite bg;
	Sprite hSign;
	RolloverButton bbutton;
		
	public HelpScreen(int name, Fonts fonts) {
		super(name);

		// TODO Sprite-ify these instead of dynamically loading
		bg = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#background");
		bg.setPosition(new Vector2D(0, 0));
		
		hSign = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#help-frame");
		hSign.setPosition(new Vector2D(70, 33));
		
		bbutton = new RolloverButton(-3, 0, 535,
			IronLegends.SCREEN_SPRITE_SHEET + "#back-button");
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.render(rc);
		hSign.render(rc);
		bbutton.render(rc);
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
