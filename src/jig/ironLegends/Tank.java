package jig.ironLegends;

import java.awt.Point;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ATSprite;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.MultiSpriteBody;
import jig.misc.sat.PolygonFactory;

public class Tank extends MultiSpriteBody {
	private static final int SPEED = 150;
	private static final double TURN_RATE = 2.0;
	private static final double MAX_HEALTH = 100.0;
	private static final int DELAY_SHOTS = 300;
	
	private ATSprite turret;
	private double rotation;
	private double speed;
	private double angularVelocity;
	private double health = MAX_HEALTH;
	private String team = "unkown";
	private long delayfire = 0; 

	public Tank(PolygonFactory pf, Vector2D pos) {
		super(pf.createRectangle(pos, 50, 50), IronLegends2.SPRITE_SHEET + "#ptank");

		setPosition(pos);
		rotation = 0.0;
		speed = 0.0;
		
		turret = getSprite(addSprite(IronLegends2.SPRITE_SHEET + "#turret"));
//		turret.setoffsetToRotation(new Vector2D(10, 10));
	}
	
	public Tank(PolygonFactory pf) {
		this(pf, new Vector2D(100, 100));		
	}

	public double getTurretRotation() {
		return turret.getRotation();
	}

	public void setTurretRotation(double rot) {
		turret.setRotation(rot);
	}
	
	@Override
	public void render(RenderingContext rc) {
		if (!isActive()) {
			return;
		}
		
		super.render(rc);
	}
	
	@Override
	public void update(long deltaMs) {
		if (!isActive()) {
			return;
		}
		
		rotation += angularVelocity * deltaMs / 1000.0;
		Vector2D t = Vector2D.getUnitLengthVector(rotation);
		Vector2D p = position.translate(t.scale(speed * deltaMs / 1000.0));
		/*
		// need to subtract width and height otherwise will move off the map
		position = position.clampX(0, IronLegends.WORLD_WIDTH - getWidth());
		position = position.clampY(0, IronLegends.WORLD_HEIGHT - getHeight());
		*/
		p = p.clamp(IronLegends2.WORLD_BOUNDS);
		
		setPosition(p);
		setRotation(rotation);
	}	

	public void controlMovement(KeyCommands m_keyCmds, Mouse mouse, Vector2D screenCenter) {
		if (m_keyCmds.isPressed("up")) {
			speed = SPEED;
		}

		if (m_keyCmds.isPressed("down")) {
			speed = -SPEED;
		}
		
		if (!m_keyCmds.isPressed("up") && !m_keyCmds.isPressed("down")) {
			speed = 0.0;
		}

		if (m_keyCmds.isPressed("left")) {
			angularVelocity = -TURN_RATE;
		}
		
		if (m_keyCmds.isPressed("right")) {
			angularVelocity = TURN_RATE;
		}
		
		if (!m_keyCmds.isPressed("left") && !m_keyCmds.isPressed("right")) {
			angularVelocity = 0.0;
		}
		
		Point loc = mouse.getLocation();
		Vector2D c = screenCenter;
		setTurretRotation(Math.atan2(c.getY() - loc.y, c.getX() - loc.x));
		//System.out.printf("c = %s, mouse = %s, turretrotation = %.2f\n", c, loc, Math.toDegrees(getTurretRotation()));
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(double health) {
		this.health = health;
	}

	/**
	 * @return the health
	 */
	public double getHealth() {
		return health;
	}
	
	/**
	 * @return the MAX health
	 */
	public double getMaxHealth() {
		return MAX_HEALTH;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}	
}
