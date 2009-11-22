package jig.ironLegends.screens;

import java.util.List;

import jig.engine.Mouse;
import jig.engine.ViewableLayer;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.Bullet;
import jig.ironLegends.IronLegends;
import jig.ironLegends.Obstacle;
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
	IronLegends game;
	
	public GamePlay_GS(int name, IronLegends gm) {
		super(name);
		this.game = gm;

		addViewableLayer(game.m_bgLayer);
		addViewableLayer(game.m_tankObstacleLayer);
		addViewableLayer(game.m_tankBulletObstacleLayer);
		addViewableLayer(game.m_powerUpLayer);
		addViewableLayer(game.m_tankLayer);
		addViewableLayer(game.m_bulletLayer);
	}

	@Override
	public void activate(int prevScreen) {
		game.newGame();
	}

	@Override
	public void populateLayers(List<ViewableLayer> gameObjectLayers) {
		super.populateLayers(gameObjectLayers);

		game.m_physicsEngine
				.manageViewableSet(game.m_tankLayer);
		game.m_physicsEngine
				.manageViewableSet(game.m_bulletLayer);

		// Register Collision Handlers
		// Tank to Tank
		ISink_CPB_CPB htanktank = new ISink_CPB_CPB() {
			@Override
			public boolean onCollision(ConvexPolyBody main,
					ConvexPolyBody other, Vector2D vCorrection) {
				if (!main.equals(other)) {
					Tank t = (Tank) main;
					t.setPosition(t.getPosition().translate(vCorrection));
					t.stop();
				}
				return false;
			}
		};
		game.m_physicsEngine
				.registerCollisionHandler(new Handler_CPBLayer_CPBLayer(
						game.m_tankLayer,
						game.m_tankLayer, htanktank));

		// Bullets and Tanks
		ISink_CPB_Body htankbull = new ISink_CPB_Body() {
			@Override
			public boolean onCollision(ConvexPolyBody main, Body other,
					Vector2D vCorrection) {
				Tank t = (Tank) main;
				Bullet b = (Bullet) other;
				Tank bo = (Tank) b.getOwner();
				if (!t.equals(bo)) { // not killing self
					if (t.getTeam() != bo.getTeam()) { // don't damage team mate
						t.causeDamage(b.getDamage());
						bo.addPoints(b.getDamage());
					}
					
					b.setActivation(false);
					return true;
				}

				return false;
			}
		};

		game.m_physicsEngine
				.registerCollisionHandler(new Handler_CPBLayer_BodyLayer(
						game.m_polygonFactory,
						game.m_tankLayer,
						game.m_bulletLayer, 4, 27, htankbull));

		// Tank & Destroyable
		game.m_physicsEngine
				.registerCollisionHandler(new Handler_CPB_CPBLayer(
						game.m_tank,
						game.m_tankBulletObstacleLayer,
						new Sink_CPB_CPB_Default()));

		// Bullets & Destroyable
		ISink_CPB_Body bulldestroyable = new ISink_CPB_Body() {
			@Override
			public boolean onCollision(ConvexPolyBody main, Body other,
					Vector2D vCorrection) {
				Obstacle o = (Obstacle) main;
				Bullet b = (Bullet) other;

				if (o.getDestructible().causeDamage(b.getDamage())) {
					o.setActivation(false);
				}

				Tank bo = (Tank) b.getOwner();
				bo.addPoints(b.getDamage());
				b.setActivation(false);
				return true;
			}
		};

		game.m_physicsEngine
				.registerCollisionHandler(new Handler_CPBLayer_BodyLayer(
						game.m_polygonFactory,
						game.m_tankBulletObstacleLayer,
						game.m_bulletLayer, 4, 27, bulldestroyable));

		// Tank & Obstacles
		game.m_physicsEngine
		.registerCollisionHandler(new Handler_CPB_CPBLayer(
				game.m_tank,
				game.m_tankObstacleLayer,
				new Sink_CPB_CPB_Default()));

		// Bullet & Obstacles
		ISink_CPB_Body bullobstacles = new ISink_CPB_Body() {
			@Override
			public boolean onCollision(ConvexPolyBody main, Body other,
					Vector2D vCorrection) {
				Obstacle o = (Obstacle) main;
				if (!o.isTree()) { // if tree -> let bullet pass thru
					Bullet b = (Bullet) other;
					b.setActivation(false);
				}
				return true;
			}
		};

		game.m_physicsEngine
				.registerCollisionHandler(new Handler_CPBLayer_BodyLayer(
						game.m_polygonFactory,
						game.m_tankObstacleLayer,
						game.m_bulletLayer, 4, 27, bullobstacles));

	}

	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse,
			final long deltaMs) {
		game.m_tank.controlMovement(keyCmds, mouse);
		if (mouse.isLeftButtonPressed() || keyCmds.isPressed("fire")) {
			game.m_tank.fire(game.getBullet());
		}

		return name();
	}
}
