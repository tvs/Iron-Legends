package jig.ironLegends.screens;

import java.util.List;

import jig.engine.Mouse;
import jig.engine.ViewableLayer;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.Bullet;
import jig.ironLegends.IronLegends;
import jig.ironLegends.Tank;
import jig.ironLegends.collision.Handler_CPBLayer_BodyLayer;
import jig.ironLegends.collision.Handler_CPBLayer_CPBLayer;
import jig.ironLegends.collision.Handler_CPB_CPBLayer;
import jig.ironLegends.collision.ISink_CPB_Body;
import jig.ironLegends.collision.ISink_CPB_CPB;
import jig.ironLegends.collision.Sink_CPB_CPB_Default;
import jig.ironLegends.core.ConvexPolyBody;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;

public class GamePlay_GS extends GameScreen {

	IronLegends m_ironLegends;

	public GamePlay_GS(int name, IronLegends ironLegends) {
		super(name);
		m_ironLegends = ironLegends;

		addViewableLayer(m_ironLegends.m_bgLayer);
		addViewableLayer(m_ironLegends.m_tankLayer);
		addViewableLayer(m_ironLegends.m_opponentLayer);
		addViewableLayer(m_ironLegends.m_bulletLayer);
		addViewableLayer(m_ironLegends.m_tankObstacleLayer);
		addViewableLayer(m_ironLegends.m_tankBulletObstacleLayer);
		addViewableLayer(m_ironLegends.m_powerUpLayer);
	}

	@Override
	public void activate(int prevScreen) {
		m_ironLegends.newGame();
	}

	@Override
	public void populateLayers(List<ViewableLayer> gameObjectLayers) {
		super.populateLayers(gameObjectLayers);

		m_ironLegends.m_physicsEngine
				.manageViewableSet(m_ironLegends.m_tankLayer);
		m_ironLegends.m_physicsEngine
				.manageViewableSet(m_ironLegends.m_opponentLayer);
		m_ironLegends.m_physicsEngine
				.manageViewableSet(m_ironLegends.m_bulletLayer);

		// Register Collision Handlers
		// Player tank to other tanks.
		ISink_CPB_CPB htankopp = new ISink_CPB_CPB() {
			@Override
			public boolean onCollision(ConvexPolyBody main,
					ConvexPolyBody other, Vector2D vCorrection) {
				Tank t = (Tank) main;
				t.setPosition(t.getPosition().translate(vCorrection));
				t.stop();
				return false;
			}
		};
		m_ironLegends.m_physicsEngine
				.registerCollisionHandler(new Handler_CPBLayer_CPBLayer(
						m_ironLegends.m_tankLayer,
						m_ironLegends.m_opponentLayer, htankopp));

		// Bullets and Opponents
		ISink_CPB_Body htankbull = new ISink_CPB_Body() {
			@Override
			public boolean onCollision(ConvexPolyBody main, Body other,
					Vector2D vCorrection) {
				Tank t = (Tank) main;
				Bullet b = (Bullet) other;
				if (!t.equals(b.getOwner())) {
					t.causeDamage(b.getDamage());
					b.setActivation(false);
					return true;
				}

				return false;
			}
		};
		m_ironLegends.m_physicsEngine
				.registerCollisionHandler(new Handler_CPBLayer_BodyLayer(
						m_ironLegends.m_polygonFactory,
						m_ironLegends.m_opponentLayer,
						m_ironLegends.m_bulletLayer, 4, 27, htankbull));

		// don't hit the obstacles
		m_ironLegends.m_physicsEngine
				.registerCollisionHandler(new Handler_CPB_CPBLayer(
						m_ironLegends.m_tank,
						m_ironLegends.m_tankObstacleLayer,
						new Sink_CPB_CPB_Default()));
		
		m_ironLegends.m_physicsEngine
				.registerCollisionHandler(new Handler_CPB_CPBLayer(
						m_ironLegends.m_tank,
						m_ironLegends.m_tankBulletObstacleLayer,
						new Sink_CPB_CPB_Default()));

	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs) {
		m_ironLegends.m_tank.controlMovement(keyCmds, mouse);
		if (mouse.isLeftButtonPressed() || keyCmds.isPressed("fire")) {
			m_ironLegends.m_tank.fire(m_ironLegends.getBullet());
		}
		
		return name();		
	}	
}
