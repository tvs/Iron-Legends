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


// TODO Scrolling server selector!
/**
 * A menu screen for handling the server selection and joining
 *  
 * @author Travis Hall
 */
public class ServerSelectScreen extends GameScreen {	
	protected Sprite bg;
	protected Sprite banner;
	protected Sprite shader;
	
	protected Sprite server_text;
	protected Sprite map_text;
	protected Sprite players_text;
	protected Sprite separator;
	
	protected RolloverButton up_button;
	protected RolloverButton down_button;
	protected RolloverButton create_button;
	protected RolloverButton connect_button;
	protected RolloverButton bbutton;
		
	public ServerSelectScreen(int name, Fonts fonts) {
		super(name);
		
		bg = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#background");
		bg.setPosition(new Vector2D(0, 0));
		
		banner = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#banner");
		banner.setPosition(new Vector2D(34, 0));
		
		shader = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#big-shader");
		shader.setPosition(new Vector2D(96, 52));
		
		server_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#server-text");
		server_text.setPosition(new Vector2D(201, 28));
		
		map_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#map-text");
		map_text.setPosition(new Vector2D(456, 28));
		
		players_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#players-text");
		players_text.setPosition(new Vector2D(593, 28));
		
		separator = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#separator");
		separator.setPosition(new Vector2D(96, 52));
		
		create_button = new RolloverButton(-1, 593, 491, IronLegends.SCREEN_SPRITE_SHEET + "#create-button");
		connect_button = new RolloverButton(-2, 614, 535, IronLegends.SCREEN_SPRITE_SHEET + "#connect-button");
		bbutton = new RolloverButton(-3, 0, 535, IronLegends.SCREEN_SPRITE_SHEET + "#back-button");
		
		up_button = new RolloverButton(-4, 667, 392, IronLegends.SCREEN_SPRITE_SHEET + "#up-arrow");
		down_button = new RolloverButton(-5, 685, 392, IronLegends.SCREEN_SPRITE_SHEET + "#down-arrow");
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.render(rc);
		banner.render(rc);
		shader.render(rc);
		separator.render(rc);
		server_text.render(rc);
		map_text.render(rc);
		players_text.render(rc);
		
		create_button.render(rc);
		connect_button.render(rc);
		bbutton.render(rc);
		
		up_button.render(rc);
		down_button.render(rc);
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		bbutton.update(mouse, deltaMs);
		if (bbutton.wasLeftClicked())
			return IronLegends.SPLASH_SCREEN;
		
		create_button.update(mouse, deltaMs);
		if (create_button.wasLeftClicked())
			System.out.println("Woah woah woah, not yet implemented. Settle down!");
		
		connect_button.update(mouse, deltaMs);
		if (connect_button.wasLeftClicked())
			// TODO Push this to the correct "Lobby" screen
			return IronLegends.GAMEPLAY_SCREEN;
		
		up_button.update(mouse, deltaMs);
		down_button.update(mouse, deltaMs);
		
		return name();		
	}
}
