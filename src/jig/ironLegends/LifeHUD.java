package jig.ironLegends;

import jig.engine.RenderingContext;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

public class LifeHUD extends Body
{
	IronLegends m_game;
	HealthBar m_healthBar;
	
	public LifeHUD(int sx, int sy, IronLegends game)
	{
		super(IronLegends.SPRITE_SHEET + "#hud-tank");

		int healthBarWidth = 4;
		Vector2D sPos =new Vector2D(sx - getWidth()-healthBarWidth, sy - getHeight()); 
		setPosition(sPos);

		m_game = game;
		m_healthBar = new HealthBar();
	}

	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void render(RenderingContext rc)
	{
		super.render(rc);
		m_healthBar.render(rc, getPosition().getX()+getWidth(), getPosition().getY()+15
				, m_game.m_tank.getHealth()
				, m_game.m_tank.getMaxHealth()
				, 15
				, false
				);
	}

}
