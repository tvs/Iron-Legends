package jig.ironLegends.screens;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ui.RolloverButton;
import jig.ironLegends.core.ui.TextEditBox;

/**
 * A splash screen class with the corresponding button "links" and rollovers.
 * @author Travis Hall
 */
public class SplashScreen extends GameScreen {
	Sprite bg;
	Sprite header;
	Sprite csbox;
	RolloverButton hbutton;
	RolloverButton mbutton;
	RolloverButton sbutton;
	
	protected TextEditBox csEntryBox;
		
	public SplashScreen(int name, Fonts fonts) {
		super(name);

		bg = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#background");
		bg.setPosition(new Vector2D(0, 0));
		
		header = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#header");
		header.setPosition(new Vector2D(0, 0));
		
		csbox = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#csbox");
		csbox.setPosition(new Vector2D(266, 300));
		
		sbutton = new RolloverButton(-3, 0, 447,IronLegends.SCREEN_SPRITE_SHEET + "#sp-button");
		mbutton = new RolloverButton(-2, 0, 491,IronLegends.SCREEN_SPRITE_SHEET + "#mp-button");
		hbutton = new RolloverButton(-1, 0, 535,IronLegends.SCREEN_SPRITE_SHEET + "#help-button");
		
		csEntryBox = new TextEditBox(fonts.textFont, -2, 292, 360, 
				IronLegends.SCREEN_SPRITE_SHEET + "#csshader");
		csEntryBox.setText("ace");
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.render(rc);
		header.render(rc);
		csbox.render(rc);
		
		sbutton.render(rc);
		mbutton.render(rc);
		hbutton.render(rc);
		
		csEntryBox.render(rc);
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		hbutton.update(mouse, deltaMs);
		if (hbutton.wasLeftClicked())
			return IronLegends.HELP_SCREEN;
		mbutton.update(mouse, deltaMs);
		if (mbutton.wasLeftClicked())
			System.out.println("Woah woah woah, not yet implemented. Settle down!");
		sbutton.update(mouse, deltaMs);
		if (sbutton.wasLeftClicked())
			// TODO Push this to the correct "Lobby" screen
			return IronLegends.GAMEPLAY_SCREEN;
		
		csEntryBox.update(mouse, deltaMs);
		if (csEntryBox.isActive())
		{
			csEntryBox.processInput(keyCmds);
		}
		
		return name();		
	}
}