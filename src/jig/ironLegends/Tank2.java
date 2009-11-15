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
	
	private Turret turret;
	private double rotation;
	private double speed;
	private double angularVelocity;

	public Tank2(PolygonFactory pf, Vector2D pos) {
		super(IronLegends2.SPRITE_SHEET + "#ptank");

		rotation = 0.0;
		speed = 0.0;
		
		turret = new Turret();
		setPosition(pos);
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
	public void setCenterPosition(Vector2D cp) {
		super.setCenterPosition(cp);
		turret.setCenterPosition(cp);		
	}

	@Override
	public void setPosition(Vector2D p) {
		super.setPosition(p);
		setCenterPosition(getCenterPosition());
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
			return;
		}
		
		rotation += angularVelocity * deltaMs / 1000.0;
		Vector2D t = Vector2D.getUnitLengthVector(rotation);
		Vector2D p = position.translate(t.scale(speed * deltaMs / 1000.0));
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
	}
	
	public class Turret extends VanillaSphere {
		public Turret() {
			super(IronLegends2.SPRITE_SHEET + "#turret");
		}

		@Override
		public void update(long deltaMs) {
		}	
	}
}
