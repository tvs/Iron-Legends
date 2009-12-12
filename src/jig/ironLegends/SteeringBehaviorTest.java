package jig.ironLegends;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.StaticScreenGame;
import jig.engine.j2d.J2DShapeEngine;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class SteeringBehaviorTest extends StaticScreenGame {
	public static final int WORLD_WIDTH = 650;
	public static final int WORLD_HEIGHT = 650;
	public static final int K_UP = 0;
	public static final int K_DOWN = 1;
	public static final int K_LEFT = 2;
	public static final int K_RIGHT = 3;
	private boolean keys[] = { false, false, false, false };
	protected Agent player;
	private BodyLayer<Body> obstacleLayer;
	private BodyLayer<Body> playerLayer;
	protected SteeringBehavior sb;
	protected J2DShapeEngine shapeEngine;
	
	public SteeringBehaviorTest() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		PaintableCanvas.loadDefaultFrames("player", 25, 15, 1, JIGSHAPE.EAST,
				Color.blue);
		PaintableCanvas.loadDefaultFrames("obstacle", 25, 25, 1, JIGSHAPE.RECTANGLE,
				Color.red);

		shapeEngine = new J2DShapeEngine(gameframe);
		
		playerLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
		player = new Agent(false);
		playerLayer.add(player);
		gameObjectLayers.add(playerLayer);

		obstacleLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
		for (int i = 0; i < 7; i++) {
			Agent ob = new Agent(true);
			obstacleLayer.add(ob);
		}

		gameObjectLayers.add(obstacleLayer);

// *
		sb = new SteeringBehavior(player);
		sb.setWorldbounds(new Rectangle(WORLD_WIDTH, WORLD_HEIGHT));
		sb.setObstacles(obstacleLayer);
		sb.setAvoidDistance(50.0);
/*
		 sb = new SteeringBehavior(e, SteeringBehavior.Behavior.FLEE);
		 sb.setTargetBound(100);
//*/
	}

	public static void main(final String[] args) {
		SteeringBehaviorTest g = new SteeringBehaviorTest();
		g.run();
	}

	@Override
	public void render(RenderingContext rc) {
		super.render(rc);
		renderCircle(rc, player.getCenterPosition(), sb.getAvoidDistance());
	}
	
	@Override
	public void update(long deltaMs) {
		if (keyboard.isPressed(KeyEvent.VK_UP)) {
			keys[K_UP] = true;
		} else {
			if (keys[K_UP]) {
				keys[K_UP] = false;
			}
		}

		if (keyboard.isPressed(KeyEvent.VK_DOWN)) {
			keys[K_DOWN] = true;
		} else {
			if (keys[K_DOWN]) {
				keys[K_DOWN] = false;
			}
		}

		if (keyboard.isPressed(KeyEvent.VK_LEFT)) {
			keys[K_LEFT] = true;
		} else {
			if (keys[K_LEFT]) {
				keys[K_LEFT] = false;
			}
		}

		if (keyboard.isPressed(KeyEvent.VK_RIGHT)) {
			keys[K_RIGHT] = true;
		} else {
			if (keys[K_RIGHT]) {
				keys[K_RIGHT] = false;
			}
		}

		@SuppressWarnings("unused")
		boolean moving = player.processEvents(deltaMs, keys);
		if (sb.getBehavior() != SteeringBehavior.Behavior.WANDER) {
			sb.setTarget(obstacleLayer.get(0).getCenterPosition());
		}
/*		
		if (moving) {
			Vector2D avoid = sb.avoidObstacle();
			if (avoid.magnitude2() > 0.001) {
				System.out.printf("== Avoid: %s\n", avoid);
			}
		}
/*/		
		sb.apply(deltaMs);
		Body agent = sb.getAgent();
		Vector2D vel = agent.getVelocity();
		// Set Rotation
		Agent t = (Agent) agent;
		t.setRotation((int) Math.toDegrees(sb.getVectorAngle(vel)));
	
		// Set Position
		Vector2D pos = agent.getCenterPosition();
		pos = pos.translate(vel);
		agent.setCenterPosition(pos);
//*/		
	}
	
	public void renderCircle(RenderingContext rc, Vector2D pos, double radius) {
		shapeEngine.renderCircle(rc, Color.black, pos.translate(new Vector2D(-radius/2, -radius/2)), radius);
	}	
}

class Agent extends VanillaAARectangle {
	public static final int K_UP = 0;
	public static final int K_DOWN = 1;
	public static final int K_LEFT = 2;
	public static final int K_RIGHT = 3;
	public static final Vector2D V_UP = new Vector2D(0, -1);
	public static final Vector2D V_DOWN = new Vector2D(0, 1);
	public static final Vector2D V_LEFT = new Vector2D(-1, 0);
	public static final Vector2D V_RIGHT = new Vector2D(1, 0);
	protected int type;
	protected int speed;
	protected double rotation;
	protected boolean player = false;

	Agent(boolean player) {
		super((player ? "obstacle" : "player"));
		setPosition(Vector2D.getRandomXY(50, 600, 50, 600));
		setSpeed((player ? 100 : 150));
		setRotation(270);
		setVelocity(V_UP);
		active = true;
		this.player = player;
	}

	public boolean destroy() {
		active = false;
		return true;
	}

	@Override
	public void update(long deltaMs) {
	}

	public boolean processEvents(long deltaMs, boolean keys[]) {
		boolean moving = false;
		if (keys[K_UP]) {
			setRotation(270);
			setVelocity(V_UP);
			moving = true;
		}
		if (keys[K_DOWN]) {
			setRotation(90);
			setVelocity(V_DOWN);
			moving = true;
		}
		if (keys[K_LEFT]) {
			setRotation(180);
			setVelocity(V_LEFT);
			moving = true;
		}
		if (keys[K_RIGHT]) {
			setRotation(0);
			setVelocity(V_RIGHT);
			moving = true;
		}

		if (moving) {
			position = position.translate(velocity.scale(speed * deltaMs
					/ 1000.0));
			position = position.clampX(0, 650);
			position = position.clampY(0, 650);
		}
		
		return moving;
	}

	@Override
	public void render(RenderingContext rc) {
		if (!active) {
			return;
		}

		AffineTransform at = AffineTransform.getTranslateInstance(position
				.getX()
				+ (getWidth() / 2.0), position.getY() + (getHeight() / 2.0));
		at.rotate(rotation);
		at.translate(-(getWidth() / 2.0), -(getHeight() / 2.0));

		super.render(rc, at);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setRotation(int rotation) {
		this.rotation = Math.toRadians(rotation);
	}

	public double getRotation() {
		return rotation;
	}
}