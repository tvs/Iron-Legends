package jig.ironLegends;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

public class LifeHUD extends Body {
	IronLegends m_game;
	HealthBar m_healthBar;
	Sprite m_tankHud;

	public LifeHUD(int sx, int sy, IronLegends game) {
		super(IronLegends.SPRITE_SHEET + "#tank-shader");
		setPosition(new Vector2D(sx - getWidth(), sy - getHeight()));

		m_tankHud = new Sprite(IronLegends.SPRITE_SHEET + "#hud-tank");
		m_tankHud.setCenterPosition(getCenterPosition().translate(new Vector2D(-2, 0)));

		m_game = game;
		m_healthBar = new HealthBar();
	}

	@Override
	public void update(long deltaMs) {
	}

	@Override
	public void render(RenderingContext rc) {
		super.render(rc);
		m_tankHud.render(rc);
		m_healthBar.render(rc, m_tankHud.getPosition().getX() + m_tankHud.getWidth() + 2, 
				m_tankHud.getPosition().getY() + 10, 
				m_game.m_tank.getHealth(), 
				m_game.m_tank.getMaxHealth(), 
				15, false);
	}

}
