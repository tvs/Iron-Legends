package jig.ironLegends;

import java.awt.Rectangle;
import java.util.Random;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

/**
 * Steering Behaviors
 * 
 * @author Bhadresh
 */
public class SteeringBehavior {
	/**
	 * Agent Behavior Types
	 */
	public static enum Behavior {
		NONE, SEEK, ARIVE, FLEE, WANDER, PURSUIT
	}

	/**
	 * The Agent to which behavior is applied to
	 */
	protected Body agent;
	/**
	 * Agent's Behavior
	 */
	protected Behavior behavior;
	/**
	 * Wandering Radius
	 */
	protected double wanderRadius;
	/**
	 * Distance from the Agent to the center of Wandering circle
	 */
	protected double wanderDistance;
	/**
	 * Wander change rate
	 */
	protected double wanderRate;
	/**
	 * Wander Theta/Angle
	 */
	protected double wanderTheta;
	/**
	 * Steering Force
	 */
	protected Vector2D steerForce;
	/**
	 * Maximum Steering Force
	 */
	protected double maxSteerForce;
	/**
	 * Slow Down distance
	 */
	protected double slowingDistance;
	/**
	 * Target Margin/Bounds
	 */
	protected double targetBound;
	/**
	 * Maximum Speed
	 */
	protected double maxSpeed;
	/**
	 * Target location
	 */
	protected Vector2D target;
	/**
	 * Random
	 */
	protected Random rand;	
	/**
	 * World Bounds
	 */
	protected Rectangle worldbounds;
	
	/**
	 * Steering Behavior
	 * 
	 * @param agent
	 * @param behavior
	 */
	public SteeringBehavior(Body agent, Behavior behavior) {
		this.agent = agent;
		this.behavior = behavior;
		target = Vector2D.ZERO;
		steerForce = Vector2D.ZERO;
		targetBound = 0;
		maxSpeed = 100.0;
		maxSteerForce = 100.0;
		slowingDistance = 10.0;
		worldbounds = IronLegends.WORLD_BOUNDS;
		rand = new Random();
	}

	/**
	 * Steering Behavior - Wander
	 * 
	 * @param agent
	 */
	public SteeringBehavior(Body agent) {
		this(agent, 15.0, 50.0, 0.05);
	}

	/**
	 * Steering Behavior - Wander
	 * 
	 * @param agent
	 * @param radius
	 * @param distance
	 * @param jitterRate
	 */
	public SteeringBehavior(Body agent, double radius, double distance,
			double jitterRate) {
		this(agent, Behavior.WANDER);
		wanderRadius = radius;
		wanderDistance = distance;
		wanderRate = jitterRate;
		wanderTheta = (Math.random() * Math.PI * 2.0);
		// initial random target
		target = Vector2D.getUnitLengthVector(wanderTheta).scale(wanderRadius);
		targetBound = 0.0;
	}

	/**
	 * Create Random Vector
	 * 
	 * @param sf
	 * @return Vector2D
	 */
	public Vector2D getRandomVector(double sf) {
		Vector2D v = getRandomUnitVector();
		return v.scale(sf);
	}

	/**
	 * Random Unit Vector
	 * 
	 * @return Vector2D
	 */
	public Vector2D getRandomUnitVector() {
		int x = rand.nextInt();
		int y = rand.nextInt();

		if (rand.nextInt() > 0.5) {
			x *= -1;
		}
		if (rand.nextInt() > 0.5) {
			y *= -1;
		}

		Vector2D v = new Vector2D(x, y);
		return v.unitVector();
	}

	/**
	 * Calculate Wander Target
	 * @param t
	 * @return Vector2D
	 */
	public Vector2D getWanderTarget(Vector2D t) {
		Vector2D circleLocation = t;
		// Normalize to get heading
		if (circleLocation.epsilonEquals(Vector2D.ZERO, 0.01)) {			
			circleLocation = Vector2D.getUnitLengthVector((Math.random()
					* Math.PI * 2.0));
		} else {
			circleLocation = circleLocation.unitVector();
		}

		// Multiply by Wander Distance
		circleLocation = circleLocation.scale(wanderDistance);
		// Make it relative to agent's location
		circleLocation = circleLocation.translate(agent.getCenterPosition());

		Vector2D circleOffset = Vector2D.getUnitLengthVector(wanderTheta);
		// Multiply by Wander Radius
		circleOffset = circleOffset.scale(wanderRadius);

		// New target location		
		circleLocation = circleLocation.translate(circleOffset);
		
		return circleLocation;
	}
	
	/**
	 * Wander: Random steering. Retain steering direction and make random
	 * displacement in each frame.
	 */
	public void wander() {
		targetBound = 0.0;
		
		// Random change
		wanderTheta += (Math.random() * 2 - 1) * wanderRate;

		// Calculate the new location to steer towards on the wander circle
		// Start with velocity
		do {
			target = getWanderTarget(agent.getVelocity());			
			if (!clampToWorld(target, worldbounds)) {
				break;
			}
		} while(true);

		// Steer towards the target
		steer(false);
	}

	/**
	 * Steer: Steer the agent towards a specific target
	 * 
	 * Arrive: Set slowdown to true - Arrive near the target
	 * 
	 * @param slowdown
	 *            if true, it slows down as it approaches the target
	 */
	public void steer(boolean slowdown) {
		Vector2D steervector = Vector2D.ZERO;
		Vector2D dv = target.difference(agent.getCenterPosition());
		double d = Math.sqrt(dv.magnitude2()); // distance to travel
		if (d > targetBound + 0.1) {
			dv = new Vector2D(dv.getX() / d, dv.getY() / d); // Unit Vector
			if (slowdown && d <= (slowingDistance + targetBound)) {
				dv = dv.scale(getMaxSpeed()
						* (d / (slowingDistance + targetBound)));
			} else {
				dv = dv.scale(getMaxSpeed());
			}

			// Steering Vector = "Desired Vector" minus "Velocity"
			steervector = dv.difference(agent.getVelocity());
			steervector = limitVector(steervector, maxSteerForce);
		}

		steerForce = steervector;
	}

	/**
	 * Flee: Steer away from a location/target
	 */
	public void flee() {
		Vector2D steervector = Vector2D.ZERO;
		Vector2D dv = agent.getCenterPosition().difference(target);
		double d = Math.sqrt(dv.magnitude2()); // distance to travel
		if (d < targetBound + 0.1) {
			dv = new Vector2D(dv.getX() / d, dv.getY() / d); // Unit Vector
			dv = dv.scale(getMaxSpeed());

			steervector = dv.difference(agent.getVelocity());
		}

		steerForce = steervector;
	}

	/**
	 * Apply Behavior
	 * Only sets the velocity of the agent, caller need to set the position
	 * 
	 * @param deltaMs
	 */
	public void apply(long deltaMs) {
		switch (behavior) {
		case WANDER:
			wander();
			break;

		case SEEK:
			steer(false);
			break;

		case ARIVE:
			steer(true);
			break;

		case FLEE:
			flee();
			break;

		default:
			return;
		}

		// Set Velocity
		Vector2D vel = agent.getVelocity();
		vel = vel.translate(steerForce);
		vel = limitVector(vel, getMaxSpeed());
		vel = vel.scale(deltaMs / 1000.0);
		agent.setVelocity(vel);

		/* Set Position
		Vector2D pos = agent.getCenterPosition();
		pos = pos.translate(vel);
		agent.setCenterPosition(pos);
		//*/
		
		// System.out.printf("Steering: %s Velocity: %s\n", steerForce, vel);
		steerForce = Vector2D.ZERO; // reset the force to zero
	}

	/**
	 * Limit Vector Magnitude
	 * 
	 * @param v
	 * @param mag
	 * @return
	 */
	public Vector2D limitVector(Vector2D v, double mag) {
		if (v.magnitude2() > mag * mag) {
			v = v.unitVector().scale(mag);
		}

		return v;
	}

	/**
	 * Clamp Velocity to keep agent in the World bounds
	 * @param pos
	 * @param r
	 * @return
	 */
	public boolean clampToWorld(Vector2D pos, final Rectangle r) {
		boolean changed = false;
		Vector2D vel = agent.getVelocity();
		double px = pos.getX();
		double py = pos.getY();
		double vx = vel.getX();
		double vy = vel.getY();
		
		if (px < r.getMinX() || px > r.getMaxX()) {
			vx = Math.random();
			changed = true;
		}
		
		if (py < r.getMinY() || py > r.getMaxY()) {
			vy = Math.random();
			changed = true;
		}

		if (changed) {
			agent.setVelocity(new Vector2D(vx, vy));
			wanderTheta = Math.atan2(vy, vx);
		}
		return changed;
	}
	
	public double getVectorAngle(Vector2D v) {
		return Math.atan2(v.getY(), v.getX());
	}

	public void setTarget(Body b) {
		this.target = b.getCenterPosition();
	}

	public Body getAgent() {
		return agent;
	}

	public void setAgent(Body agent) {
		this.agent = agent;
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}

	public double getWanderRadius() {
		return wanderRadius;
	}

	public void setWanderRadius(double wanderRadius) {
		this.wanderRadius = wanderRadius;
	}

	public double getWanderDistance() {
		return wanderDistance;
	}

	public void setWanderDistance(double wanderDistance) {
		this.wanderDistance = wanderDistance;
	}

	public double getWanderRate() {
		return wanderRate;
	}

	public void setWanderRate(double wanderRate) {
		this.wanderRate = wanderRate;
	}

	public double getWanderTheta() {
		return wanderTheta;
	}

	public void setWanderTheta(double wanderTheta) {
		this.wanderTheta = wanderTheta;
	}

	public Vector2D getSteerForce() {
		return steerForce;
	}

	public void setSteerForce(Vector2D steerForce) {
		this.steerForce = steerForce;
	}

	public Vector2D getTarget() {
		return target;
	}

	public void setTarget(Vector2D target) {
		this.target = target;
	}

	public void setMaxSteerForce(double maxSteerForce) {
		this.maxSteerForce = maxSteerForce;
	}

	public double getMaxSteerForce() {
		return maxSteerForce;
	}

	public void setSlowingDistance(double slowingDistance) {
		this.slowingDistance = slowingDistance;
	}

	public double getSlowingDistance() {
		return slowingDistance;
	}

	public void setTargetBound(double targetBound) {
		this.targetBound = targetBound;
	}

	public double getTargetBound() {
		return targetBound;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setWorldbounds(Rectangle worldbounds) {
		this.worldbounds = worldbounds;
	}

	public Rectangle getWorldbounds() {
		return worldbounds;
	}
}