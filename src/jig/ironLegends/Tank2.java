package jig.ironLegends;

import java.awt.Point;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.KeyCommands;
import jig.misc.sat.PolygonFactory;

public class Tank2 extends VanillaSphere {
	private static final int SPEED = 150;
	private static final double TURN_RATE = 2.0;
	private static final int MAX_HEALTH = 100;
	private static final int FIRE_DELAY = 300;
	private static final int RESPAWN_DELAY = 500;

	private Vector2D initialPosition;
	private Turret turret;
	private double rotation;
	private double speed;
	private double angularVelocity;
	private int health = MAX_HEALTH;
	private int damageAmount = 20;
	private int bulletRange = 300;
	private String team = "unkown";
	private long timeSinceFired = 0;
	private long timeSinceDied = 0;

	public Tank2(PolygonFactory pf, Vector2D pos) {
		super(IronLegends2.SPRITE_SHEET + "#ptank");
		position = pos;
		turret = new Turret();
		initialPosition = pos;
		respawn();
	}

	public Tank2(PolygonFactory pf) {
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
		turret.render(rc);
	}

	@Override
	public void update(long deltaMs) {
		if (!isActive()) {
			timeSinceDied += deltaMs;
			if (timeSinceDied > RESPAWN_DELAY) {
				respawn();
			}
			return;
		}

		timeSinceFired += deltaMs;
		rotation += angularVelocity * deltaMs / 1000.0;
		Vector2D t = Vector2D.getUnitLengthVector(rotation);
		Vector2D p = position.translate(t.scale(speed * deltaMs / 1000.0));
		p = p.clamp(IronLegends2.WORLD_BOUNDS);

		setPosition(p);
		setRotation(rotation);
		turret.update(deltaMs);
	}

	public void fire(Bullet b) {
		if (timeSinceFired > FIRE_DELAY) {
			timeSinceFired = 0;
			b.reload(damageAmount, bulletRange);
			b.fire(this, turret.getCenterPosition().translate(
					new Vector2D(-25, 0).rotate(getTurretRotation())),
					getTurretRotation());
		}
	}

	public void controlMovement(KeyCommands m_keyCmds, Mouse mouse,
			Vector2D screenCenter) {
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
		setTurretRotation(Math.atan2(screenCenter.getY() - loc.y, screenCenter
				.getX()
				- loc.x));
		// System.out.printf("p = %s, c = %s, mouse = %s, turretrotation = %.2f\n",
		// getPosition(), screenCenter, loc,
		// Math.toDegrees(getTurretRotation()));
	}

	public void explode() {
		active = false;
		timeSinceDied = 0;
	}

	public void respawn() {
		rotation = 0.0;
		speed = 0.0;
		health = MAX_HEALTH;
		setPosition(initialPosition);
		active = true;
	}

	public void causeDamage(int damage) {
		health -= damage;
		if (health <= 0) {
			explode();
		}
	}

	/**
	 * @param health
	 *            the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @return the MAX health
	 */
	public int getMaxHealth() {
		return MAX_HEALTH;
	}

	/**
	 * @param team
	 *            the team to set
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

	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}

	public int getDamageAmount() {
		return damageAmount;
	}

	public void setBulletRange(int bulletRange) {
		this.bulletRange = bulletRange;
	}

	public int getBulletRange() {
		return bulletRange;
	}

	public class Turret extends VanillaSphere {
		public Turret() {
			super(IronLegends2.SPRITE_SHEET + "#turret");
			setCenterPosition(Tank2.this.getCenterPosition());
		}

		@Override
		public void update(long deltaMs) {
			setCenterPosition(Tank2.this.getCenterPosition());
		}
	}
}
