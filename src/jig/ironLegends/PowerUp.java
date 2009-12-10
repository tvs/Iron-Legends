package jig.ironLegends;

import jig.engine.physics.vpe.VanillaAARectangle;

public class PowerUp extends VanillaAARectangle {
	public static enum Type {
		UPGRADE, REPAIR, SHIELD, LIFE, DOUBLECANNON
	};

	private int MAX_ACTIVE_TIME = 10000;
	private Type type;
	private int[] points = { 20, 15, 15, 30, 10 };
	private long timer = 0;
	
	public PowerUp() {
		this(getRandomType());
	}

	public PowerUp(Type type) {
		super(IronLegends.SPRITE_SHEET + "#powerups");
		setType(type);
	}
	
	public void reset() {
		active = true;
		timer = 0;
	}
	
	@Override
	public void update(long deltaMs) {
		if (!active) {
			return;
		}
		
		timer += deltaMs;
		if (timer >= MAX_ACTIVE_TIME) {
			active = false;
		}
	}

	public void setType(Type type) {
		this.type = type;
		setFrame(type.ordinal() + 1);
	}

	public Type getType() {
		return type;
	}

	public static Type getType(int t) {
		Type[] types = Type.values();
		return types[t];
	}
	
	public static Type getRandomType() {
		Type[] types = Type.values();
		return types[(int) (Math.random() * types.length)];
	}

	public int getPoint() {
		return points[type.ordinal()];
	}

	public void setPoints(int[] points) {
		this.points = points;
	}

	public int[] getPoints() {
		return points;
	}

	public void executePower(Tank t) {
		switch (type) {
		case UPGRADE:
			t.upgrade();
			break;

		case REPAIR:
			t.repair();
			break;

		case SHIELD:
			t.setShield(true);
			break;

		case LIFE:
			// TODO: add life
			break;

		case DOUBLECANNON:
			t.doubleCannon();
			break;
		}
	}
}
