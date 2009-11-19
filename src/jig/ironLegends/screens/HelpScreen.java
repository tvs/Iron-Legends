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

public class HelpScreen extends GameScreen {
	protected PlayerInfo m_playerInfo;
	
	List<ImageResource> bg;
	List<ImageResource> hSign;
	Button bbutton;
		
	public HelpScreen(int name, Fonts fonts) {
		super(name);

		// TODO Sprite-ify these instead of dynamically loading
		bg = ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/background.png");
		hSign = ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/helpsign.png");
		
		bbutton = new Button(-3, 0, 535,
			ResourceFactory.getFactory().getFrames(IronLegends.RESOURCE_ROOT + "screens/backbutton.png"));
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.get(0).render(rc, AffineTransform.getTranslateInstance(0, 0));
		hSign.get(0).render(rc, AffineTransform.getTranslateInstance(70, 33));
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
