package jig.ironLegends;

import java.awt.Point;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ATSprite;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.MultiSpriteBody;

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

	private static final double TURN_RATE = 2.0;
	private static final int MAX_HEALTH = 100;
	private static final int RESPAWN_DELAY = 1000;
	private int MAX_SHIELD_TIME = 10000;
	private int SPEED = 300;
	private int FIRE_DELAY = 300;

	private Vector2D initialPosition;
	private ATSprite sTurret;
	private ATSprite sTeam;
	private ATSprite sSpeed;
	private ATSprite sArmor;
	private ATSprite sShield;
	private Animator m_animator;
	private Type type = Type.BASIC;
	private Team team = Team.WHITE;
	private Weapon weapon = Weapon.CANNON;
	private Body target = null;
	private boolean playerControlled = true;

	private boolean respawn = true;
	private boolean shield = false;
	private boolean fixturret = false;
	private int curSpeed;
	private double angularVelocity;
	private int health = MAX_HEALTH;
	private int damageAmount = 20;
	private int bulletRange = 300;
	private long timeSinceFired = 0;
	private long timeSinceDied = 0;
	private long timeSinceShield = 0;
	private int score = 0;
	private IronLegends game = null;

	public Tank(IronLegends game, Team team, Vector2D pos, Type type) {
		super(game.m_polygonFactory.createRectangle(pos, 85, 101),
				IronLegends.SPRITE_SHEET + "#tank");
		setGame(game);

		// Tank Powers
		sSpeed = getSprite(addSprite(IronLegends.SPRITE_SHEET + "#speed"));
		sSpeed.setActivation(false);
		sArmor = getSprite(addSprite(IronLegends.SPRITE_SHEET + "#armor"));
		sArmor.setActivation(false);
		sShield = getSprite(addSprite(IronLegends.SPRITE_SHEET
				+ "#shield-effect"));
		sShield.setActivation(false);

		// Team star
		sTeam = getSprite(addSprite(IronLegends.SPRITE_SHEET + "#star"));
		sTeam.setOffset(new Vector2D(0, 35));

		// Turret
		sTurret = getSprite(addSprite(IronLegends.SPRITE_SHEET + "#cannon"));
		sTurret.setOffset(new Vector2D(0, -5));
		sTurret.setRotationOffset(new Vector2D(0, 22));
		sTurret.setAbsRotation(true);

		m_animator = new Animator(2, 75, 0);
		initialPosition = pos;
		setTeam(team);
		setType(type);
		respawn();
	}

	public Tank(IronLegends game, Team team, Vector2D pos) {
		this(game, team, pos, Type.BASIC);
	}

	// AI Tank
	public Tank(IronLegends game, Team team, Vector2D pos, boolean AI) {
		this(game, team, pos);
		setPlayerControlled(false);
		allowRespawn(false);
		FIRE_DELAY = 500;
		damageAmount = 10;
	}

	@Override
	public void update(long deltaMs) {
		if (!isActive()) {
			if (respawn) {
				timeSinceDied += deltaMs;
				if (timeSinceDied > RESPAWN_DELAY) {
					respawn();
				}
			}
			return;
		}

		timeSinceFired += deltaMs;
		if (shield) {
			timeSinceShield += deltaMs;
			if (timeSinceShield > MAX_SHIELD_TIME) {
				setShield(false);
			}
		}
		
		if (!playerControlled) {
			AIMovement(deltaMs);
		}

		double rotation = getRotation() + (angularVelocity * deltaMs / 1000.0);
		Vector2D translateVec = Vector2D.getUnitLengthVector(
				rotation + Math.toRadians(270)).scale(
				curSpeed * deltaMs / 1000.0);
		Vector2D p = position.translate(translateVec);
		p = p.clampX(0, IronLegends.WORLD_WIDTH - getWidth());
		p = p.clampY(0, IronLegends.WORLD_HEIGHT - getHeight());

		setPosition(p);
		setRotation(rotation);
		if ((curSpeed != 0 || angularVelocity != 0)
				&& m_animator.update(deltaMs, translateVec)) {
			getSprite(0).setFrame(m_animator.getFrame());
		}
	}

	public void controlMovement(KeyCommands m_keyCmds, Mouse mouse) {
		if (m_keyCmds.isPressed("up") || m_keyCmds.isPressed("w")) {
			curSpeed = SPEED;
		}

		if (m_keyCmds.isPressed("down") || m_keyCmds.isPressed("s")) {
			curSpeed = -SPEED;
		}

		if (!m_keyCmds.isPressed("up") && !m_keyCmds.isPressed("down")
				&& !m_keyCmds.isPressed("w") && !m_keyCmds.isPressed("s")) {
			stopMoving();
		}

		if (m_keyCmds.isPressed("left") || m_keyCmds.isPressed("a")) {
			angularVelocity = -TURN_RATE;
		}

		if (m_keyCmds.isPressed("right") || m_keyCmds.isPressed("d")) {
			angularVelocity = TURN_RATE;
		}

		if (!m_keyCmds.isPressed("left") && !m_keyCmds.isPressed("right")
				&& !m_keyCmds.isPressed("a") && !m_keyCmds.isPressed("d")) {
			stopTurning();
		}

		if (mouse.isLeftButtonPressed() || m_keyCmds.isPressed("fire")) {
			fire();
		}

		if (m_keyCmds.wasReleased("fixturret")) {
			fixturret = !fixturret;
		}

		if (fixturret) {
			setTurretRotation(getRotation());
		} else {
			Point loc = mouse.getLocation();
			Vector2D tankCenterPos = getShapeCenter();
			Vector2D mousePt = game.m_mapCalc.screenToWorld(new Vector2D(loc.x,
					loc.y));
			double rot = tankCenterPos.angleTo(mousePt);
			setTurretRotation(rot + Math.toRadians(90));
		}
	}

	private void AIMovement(long deltaMs) {
		if (target == null || !target.isActive()) {
			stopMoving();
			stopTurning();
			return;
		}

		Vector2D tp = target.getCenterPosition();
		Vector2D sp = getCenterPosition();
		double dist = Math.sqrt(tp.distance2(sp));
		double target_angle = sp.angleTo(tp);
		if (dist <= 1.25 * bulletRange) { // close enough start firing
			stopMoving();
			stopTurning();
			setTurretRotation(target_angle + Math.toRadians(90));
			fire();
		} else if (dist <= 2 * bulletRange) { // go towards the target
			curSpeed = SPEED;
			setRotation(target_angle + Math.toRadians(90));
			setTurretRotation(getRotation()); // fix sTurret
		} else {
			stopMoving();
			stopTurning();
		}
	}

	public void respawn() {
		stopMoving();
		stopTurning();
		setPosition(initialPosition);
		setRotation(Math.toRadians(90));
		setTurretRotation(Math.toRadians(90));
		setHealth(MAX_HEALTH);
		setWeapon(Weapon.CANNON);
		setActivation(true);
		setShield(true);
		MAX_SHIELD_TIME = 2000; // initial temp shield

		m_animator.setFrameBase(0);
		getSprite(0).setFrame(m_animator.getFrame());
	}

	public void fire() {
		if (timeSinceFired > FIRE_DELAY) {
			timeSinceFired = 0;
			Bullet b = game.getBullet();
			b.reload(damageAmount, bulletRange);
			b.fire(this, getShapeCenter().translate(
					new Vector2D(0, 20 - 86).rotate(getTurretRotation())),
					getTurretRotation());
		}
	}

	public void causeDamage(int damage) {
		if (playerControlled) {
			return; // TODO: hack
		}
		
		if (shield) {
			return;
		}
		
		health -= damage;
		if (health <= 0) {
			explode();
		}
	}

	public void explode() {
		stopMoving();
		active = false;
		timeSinceDied = 0;
		if (playerControlled) { // if died loose all power
			setType(Type.BASIC);
		}
	}

	public void setShield(boolean s) {
		sShield.setActivation(s);
		shield = s;
		MAX_SHIELD_TIME = 10000;
		timeSinceShield = 0;
	}
	
	public void upgrade() {
		if (type == Type.BASIC) {
			setType(Type.SPEEDY);
		} else if (type == Type.SPEEDY) {
			setType(Type.ARMORED);
		}
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

	public double getTurretRotation() {
		return sTurret.getRotation();
	}

	public void setTurretRotation(double rot) {
		sTurret.setRotation(rot);
	}

	public void stopMoving() {
		curSpeed = 0;
	}

	public void stopTurning() {
		angularVelocity = 0.0;
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

	public void setTeam(Team t) {
		team = t;
		sTeam.setFrame(team.ordinal());
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
		sTurret.setFrame(weapon.ordinal());
		// TODO: set fire to do multiple shot
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public double getSpeed() {
		return SPEED;
	}

	public void setSpeed(int speed) {
		this.SPEED = speed;
	}

	public void setType(Type type) {
		this.type = type;
		setSpeed((type == Type.SPEEDY ? 500 : 300));
		sSpeed.setActivation(false);
		sArmor.setActivation(false);
		switch (type) {
		case BASIC:
			break;
		case SPEEDY:
			sSpeed.setActivation(true);
			break;
		case ARMORED:
			sArmor.setActivation(true);
			break;
		}
	}

	public Type getType() {
		return type;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	public void addPoints(int p) {
		this.score += p;
	}

	public void setPlayerControlled(boolean playerControlled) {
		this.playerControlled = playerControlled;
	}

	public boolean isPlayerControlled() {
		return playerControlled;
	}

	public void allowRespawn(boolean respawn) {
		this.respawn = respawn;
	}

	public void setTarget(Body target) {
		this.target = target;
	}

	public Body getTarget() {
		return target;
	}

	public void setGame(IronLegends game) {
		this.game = game;
	}

	public IronLegends getGame() {
		return game;
	}
}
