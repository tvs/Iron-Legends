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
	private static final int SPEED = 150;
	private static final double TURN_RATE = 2.0;
	private static final int MAX_HEALTH = 100;
	private static final int FIRE_DELAY = 300;
	private static final int RESPAWN_DELAY = 1000;
	
	private Vector2D initialPosition;
	private ATSprite turret;
	private Animator m_animator;
	
	private double speed;
	private double angularVelocity;
	private int health = MAX_HEALTH;
	private int damageAmount = 20;
	private int bulletRange = 300;
	private String team = "unkown";
	private long timeSinceFired = 0;
	private long timeSinceDied = 0;
	private MapCalc m_mapCalc = null;

	public Tank(PolygonFactory pf, Vector2D pos, String team, MapCalc mapCalc) {
		super(pf.createRectangle(pos, 85, 101), IronLegends2.SPRITE_SHEET + "#tank");
		m_mapCalc = mapCalc;
		turret = getSprite(addSprite(IronLegends2.SPRITE_SHEET + "#cannon"));
//		turret.setoffsetToRotation(new Vector2D(22.5, 79.0));
		// this sets the location at which the turret will rotate
		// the rotation point will stay center over the MultiSpriteBody
		turret.setRotationOffset(new Vector2D(0, turret.getHeight()/2.0-20));
		
		turret.setAbsRotation(true);
		// this would move the turret's position by this offset from the rotation center 
		//turret.setOffset(new Vector2D(turret.getHeight()/2-20, 0));
		
		m_animator = new Animator(2, 75, 0);
		initialPosition = pos;
		setTeam(team);
		respawn();
	}
	
	public Tank(PolygonFactory pf, String team, MapCalc mapCalc) {
		this(pf, new Vector2D(1000, 1000), team, mapCalc);
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
		Vector2D translateVec = Vector2D.getUnitLengthVector(rotation + Math.toRadians(270)).scale(speed * deltaMs / 1000.0);
		Vector2D p = position.translate(translateVec);
		//p = p.clamp(IronLegends2.WORLD_BOUNDS);
		p = p.clampX(0, IronLegends2.WORLD_WIDTH - getWidth());
	    p = p.clampY(0, IronLegends2.WORLD_HEIGHT - getHeight());		
		
		setPosition(p);
		setRotation(rotation);
		if ((speed != 0 || angularVelocity != 0) && m_animator.update(deltaMs, translateVec)) {
			getSprite(0).setFrame(m_animator.getFrame());
		}		
	}

	public void fire(Bullet b) {
		if (timeSinceFired > FIRE_DELAY) {
			timeSinceFired = 0;
			b.reload(damageAmount, bulletRange);
			/*
			b.fire(this, turret.getCenterPosition().translate(
					new Vector2D(37, 0).rotate(getTurretRotation())),
					getTurretRotation());
			*/
			b.fire(this, getShapeCenter().translate(
					new Vector2D(0,-37).rotate(getTurretRotation()))
					, getTurretRotation());
			
		}
	}
	
	public void controlMovement(KeyCommands m_keyCmds, Mouse mouse, Vector2D screenCenter) {
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
		setTurretRotation(rot+Math.toRadians(90));
		//System.out.print("MapCalc center: " + m_mapCalc.screenToWorld(m_mapCalc.getCenter()));
		//System.out.println("AngleTo Angle: " + Math.toDegrees(rot));
		
		//setTurretRotation(screenCenter.angleTo(new Vector2D(loc.getX(), loc.getY())) + Math.toRadians(90));
//		System.out.printf("p = %s, c = %s, mouse = %s, turretrotation = %.2f, width = %d, height = %d\n",
//				getPosition(), screenCenter, loc, Math.toDegrees(getTurretRotation()), getWidth(), getHeight());
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
		active = true;
	}
	
	public void causeDamage(int damage) {
		health -= damage;
		if (health <= 0) {
			explode();
		}
	}
	
	/**
	 * @param health the health to set
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

	public double getSpeed() {
		// TODO Auto-generated method stub
		return speed;
	}	
}
