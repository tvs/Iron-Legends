package jig.ironLegends;

import java.util.Vector;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

public class PowerUpHUD extends Body
{
	
	Vector<Sprite> m_powerUps;
	Vector<Integer> m_powerUpNames;
	IronLegends m_game;
	
	public PowerUpHUD(int sx, int sy, IronLegends game) {
		super(IronLegends.SPRITE_SHEET + "#shield-indicator");
		m_powerUps = new Vector<Sprite>();
		m_powerUpNames = new Vector<Integer>();
		m_game = game;
		
		int x = sx;
		int y = sy;
		
		String indicators[] = 
		{
				"shield"	// PU_SHIELD
			,	"speed"		// PU_SPEED
			,	"armor"		// PU_ARMOR
			,	"mine"		// PU_MINE
			,	"damage"	// PU_DAMAGE
		};
		
		
		Sprite s = null;
		
		for (int i = 0; i < indicators.length; ++i)
		{
			s = new Sprite(IronLegends.SPRITE_SHEET + "#" + indicators[i] + "-indicator");
			s.setPosition(new Vector2D(x,y-s.getHeight()));
			s.setFrame(1);
			m_powerUps.add(s);
			m_powerUpNames.add(i);
			x += s.getWidth();
		}		
	}
	
	@Override
	public void render(RenderingContext rc)
	{
		Sprite s = null;
		int name = 0;
		
		for (int i = 0; i < m_powerUps.size(); ++i)
		{
			s = m_powerUps.get(i);

			name = m_powerUpNames.get(i);
			if (m_game.m_tank.isPowerUpActive(name))
				s.setFrame(0);
			else
				s.setFrame(1);
			
			s.render(rc);	
		}
	}

	@Override
	public void update(long deltaMs) {
		
	}
}
