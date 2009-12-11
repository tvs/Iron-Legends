package jig.ironLegends.screens;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;

public class GameOver_GS extends GameScreen {

	protected IronLegends m_game = null;
	
	public GameOver_GS(int name, IronLegends game) {
		super(name);
		m_game = game;
	}
	
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs){
	
		if (m_game.m_levelProgress.isExitActivated()) {
			if (keyCmds.wasPressed("enter")) {
				m_game.m_levelProgress.setExitComplete(true);
				// transition to SPLASH..
				return IronLegends.SPLASH_SCREEN;
			}
		}
		return name();
	}
	
	@Override
	public void update(long deltaMs) {
		// allow things to keep moving
		m_game.m_physicsEngine.applyLawsOfPhysics(deltaMs);
		m_game.m_sfx.update(deltaMs);
	}
	
	@Override
	public void deactivate()
	{
		m_game.m_server = null;
		m_game.m_client = null;
	}
	@Override
	public void render(RenderingContext rc)
	{
		super.render(rc);
		rc.setTransform(m_game.m_mapCalc.getWorldToScreenTransform());
		m_game.m_sfx.render(rc);

	}
}
