package jig.ironLegends.screens;

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.ironLegends.IronLegends;
import jig.ironLegends.PlayerInfo;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ui.Button;
import jig.ironLegends.core.ui.TextEditBox;

public class SplashScreen extends GameScreen {
	protected PlayerInfo m_playerInfo;
	
	List<ImageResource> bg;
	List<ImageResource> header;
	List<ImageResource> csbox;
	Button hbutton;
	Button mbutton;
	Button sbutton;
	
	protected TextEditBox csEntryBox;
		
	public SplashScreen(int name, Fonts fonts) {
		super(name);

		// TODO Sprite-ify these instead of dynamically loading
		bg = ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/background.png");
		header = ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/header.png"); 
		csbox = ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/callsignbox.png"); 
		
		hbutton = new Button(-1, 0, 535,
			ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/helpbutton.png"));
		mbutton = new Button(-2, 0, 491,
			ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/mpbutton.png"));
		sbutton = new Button(-3, 0, 447,
			ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/spbutton.png"));
		
		// TODO Once spritified, change this sprite to reflect the correct box
		csEntryBox = new TextEditBox(fonts.textFont, -2, 390, 368, 
				IronLegends.SPRITE_SHEET + "#testEditBox");
		csEntryBox.setText("Ace");
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.get(0).render(rc, AffineTransform.getTranslateInstance(0, 0));
		header.get(0).render(rc, AffineTransform.getTranslateInstance(0, 0));
		csbox.get(0).render(rc, AffineTransform.getTranslateInstance(266, 300));
		
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
			System.out.println("Multiplayer");
		sbutton.update(mouse, deltaMs);
		if (sbutton.wasLeftClicked())
			System.out.println("Singleplayer");
		
		csEntryBox.update(mouse, deltaMs);
		if (csEntryBox.isActive())
		{
			csEntryBox.processInput(keyCmds);
		}
		
		return name();		
	}
}