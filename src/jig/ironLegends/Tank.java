package jig.ironLegends;

import java.awt.Point;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ATSprite;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.MultiSpriteBody;
import jig.ironLegends.mapEditor.MapCalc;
import jig.misc.sat.PolygonFactory;

public class Tank extends MultiSpriteBody {
	public static enum Type {
		BASIC, SPEEDY, ARMORED
	};

	public static enum Team {
		WHITE, BLUE, RED
	};

	public static enum Weapon {
		CANNON, DOUBLECANNON
	};

	private static final int SPEED = 300;
	private static final double TURN_RATE = 2.0;
	private static final int MAX_HEALTH = 100;
	private static final int FIRE_DELAY = 300;
	private static final int RESPAWN_DELAY = 1000;

	private Vector2D initialPosition;
	private ATSprite turret;
	private Animator m_animator;
	private MapCalc m_mapCalc = null;
	private Type type = Type.BASIC;
	private Team team = Team.WHITE;
	private Weapon weapon = Weapon.CANNON;

	private double speed;
	private double angularVelocity;
	private int health = MAX_HEALTH;
	private int damageAmount = 20;
	private int bulletRange = 300;
	private long timeSinceFired = 0;
	private long timeSinceDied = 0;

	public Tank(MapCalc mapCalc, PolygonFactory pf, Team team, Vector2D pos) {
		super(pf.createRectangle(pos, 85, 101), IronLegends.SPRITE_SHEET
				+ "#tank");
		m_mapCalc = mapCalc;
		setTeam(team);
		
		ATSprite teamflag = getSprite(addSprite(IronLegends.SPRITE_SHEET
				+ "#star"));
		teamflag.setOffset(new Vector2D(0, teamflag.getHeight()/2 + 20));
		teamflag.setFrame(team.ordinal());

		turret = getSprite(addSprite(IronLegends.SPRITE_SHEET + "#cannon"));
		// this sets the location at which the turret will rotate
		// the rotation point will stay center over the MultiSpriteBody
		turret.setRotationOffset(new Vector2D(0,
						turret.getHeight() / 2.0 - 20));

		turret.setAbsRotation(true);
		setTurretRotation(Math.toRadians(90));
		// this would move the turret's position by this offset from the
		// rotation center
		// turret.setOffset(new Vector2D(turret.getHeight()/2-20, 0));

		m_animator = new Animator(2, 75, 0);
		initialPosition = pos;
		respawn();
	}

	public double getTurretRotation() {
		return turret.getRotation();
	}

	public void setTurretRotation(double rot) {
		turret.setRotation(rot);
	}

	@Override
	public int getWidth() {
		return getSprite(0).getWidth();
	}

	@Override
	public int getHeight() {
		return getSprite(0).getHeight();
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
			timeSinceDied += deltaMs;
			if (timeSinceDied > RESPAWN_DELAY) {
				respawn();
			}
			return;
		}

		timeSinceFired += deltaMs;
		double rotation = getRotation() + (angularVelocity * deltaMs / 1000.0);
		Vector2D translateVec = Vector2D.getUnitLengthVector(
				rotation + Math.toRadians(270)).scale(speed * deltaMs / 1000.0);
		Vector2D p = position.translate(translateVec);
		p = p.clampX(0, IronLegends.WORLD_WIDTH - getWidth());
		p = p.clampY(0, IronLegends.WORLD_HEIGHT - getHeight());

		setPosition(p);
		setRotation(rotation);
		if ((speed != 0 || angularVelocity != 0)
				&& m_animator.update(deltaMs, translateVec)) {
			getSprite(0).setFrame(m_animator.getFrame());
		}
	}

	public void fire(Bullet b) {
		if (timeSinceFired > FIRE_DELAY) {
			timeSinceFired = 0;
			b.reload(damageAmount, bulletRange);
			b.fire(this, getShapeCenter().translate(
					new Vector2D(0, 20 - 86).rotate(getTurretRotation())),
					getTurretRotation());
		}
	}

	public void controlMovement(KeyCommands m_keyCmds, Mouse mouse) {
		if (m_keyCmds.isPressed("up")) {
			speed = SPEED;
		}

		if (m_keyCmds.isPressed("down")) {
			speed = -SPEED;
		}

		if (!m_keyCmds.isPressed("up") && !m_keyCmds.isPressed("down")) {
			stop();
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
		Vector2D tankCenterPos = getShapeCenter();

		Vector2D mousePt = m_mapCalc.screenToWorld(new Vector2D(loc.x, loc.y));
		double rot = tankCenterPos.angleTo(mousePt);
		setTurretRotation(rot + Math.toRadians(90));
	}

	public void stop() {
		speed = 0.0;
	}

	public void explode() {
		stop();
		active = false;
		timeSinceDied = 0;
	}

	public void respawn() {
		setRotation(Math.toRadians(90));
		speed = 0.0;
		health = MAX_HEALTH;
		setPosition(initialPosition);
		m_animator.setFrameBase(0);
		getSprite(0).setFrame(m_animator.getFrame());
		setWeapon(Weapon.CANNON);
		active = true;
	}

	public void causeDamage(int damage) {
		health -= damage;
		if (health <= 0) {
			explode();
		}
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return MAX_HEALTH;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Team getTeam() {
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

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
		turret.setFrame(weapon.ordinal());
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
