package jig.ironLegends;

import jig.engine.physics.Body;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

public class Bullet extends VanillaSphere {
	private static final int BULLET_SPEED = 300;
	
	private int damage;
	private int range;
	private int speed;
	private Vector2D origin;
	private Body owner;
	
	public Bullet() {
		super(IronLegends.SPRITE_SHEET + "#bullet");
		active = false;
	}

	public void reload(int d, int r) {
		damage = d;
		range = r;
		speed = BULLET_SPEED;
		origin = null;
		owner = null;
		velocity = Vector2D.ZERO;
	}

	@Override
	public void update(long deltaMs) {
		if (!isActive()) {
			return;
		}
		
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		if (position.distance2(origin) > range * range) {
			active = false;
		}		
	}

	public void fire(Body owner, Vector2D origin, Vector2D dir) {
		this.owner = owner;
		this.origin = origin;
		setCenterPosition(origin);
		velocity = dir.scale(speed);
		active = true;
	}
	
	public void fire(Body owner, Vector2D origin, double angle) {
		setRotation(angle);
		fire(owner, origin, Vector2D.getUnitLengthVector(angle-Math.toRadians(90)));
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getDamage() {
		return damage;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getRange() {
		return range;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setOrigin(Vector2D origin) {
		this.origin = origin;
	}

	public Vector2D getOrigin() {
		return origin;
	}

	public void setOwner(Body owner) {
		this.owner = owner;
	}

	public Body getOwner() {
		return owner;
	}
}
