package jig.ironLegends;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
/// class to adjust movement (but not actually move)
// based on controls specified
public class GroundEngine 
{
	protected double m_rate;
	public GroundEngine(double minMoveSpeed, double maxMoveSpeed, double minToMaxTimeMs)
	{
		m_minMoveSpeed = minMoveSpeed;
		m_maxMoveSpeed = maxMoveSpeed;
		m_deltaMinMax = m_maxMoveSpeed - m_minMoveSpeed;
		m_minToMaxTimeMs = minToMaxTimeMs;
		
		m_rate = 0;
		if (m_minToMaxTimeMs > 0)
			m_rate = m_deltaMinMax/m_minToMaxTimeMs;
		m_curSpeed = 0;
	}

	// adjust body to intercept 2nd body with pos,vel)
	public void move(Body body, Vector2D pos, Vector2D vel)
	{
	}
	
	// adjust body to reach dest (pt ... non-moving)
	// return true if reached destination
	public boolean move(Body body, Vector2D dest, long deltaMs)
	{
		Vector2D newV = null;
		
		Vector2D p = body.getCenterPosition();
		//Vector2D p = body.getPosition();
		
		Vector2D diff = dest.difference(p);
		double tol = 4;
		boolean bRet = false;
		if (diff.magnitude2() < tol*tol)
		{
			// just keep current pace
			bRet = true;
			return true;
		}
		
		// adjust vel to reach destination, don't bother with slowing down
		if (m_curSpeed < m_minMoveSpeed)
			m_curSpeed = m_minMoveSpeed;
		else if (m_curSpeed < m_maxMoveSpeed)
			m_curSpeed += m_rate*deltaMs;
		
		if (m_curSpeed > m_maxMoveSpeed)
		{
			//System.out.println("Yikes: " + m_curSpeed);
			m_curSpeed = m_maxMoveSpeed;
		}
		// todo limit turn by turn rate
		newV = diff.unitVector().scale(m_curSpeed);
		body.setVelocity(newV);
				
		return bRet;			
	}
	
	// just adjusts movement
	public void move(Body body
			, boolean left, boolean right
			, boolean up, boolean down
			, double deltaMs)
	{
		// adjust the velocity direction
		Vector2D v = body.getVelocity();
		Vector2D newV = null;
		boolean bMoving = true;
		
		if (up && !down)
		{	
			if (v.getY() < 0)
				m_curSpeed += m_rate*deltaMs;
			else
				m_curSpeed = m_minMoveSpeed;

					
			newV = new Vector2D(0,-1);
		}
		else if (down && !up)
		{
			if (v.getY() > 0)
				m_curSpeed += m_rate*deltaMs;
			else
				m_curSpeed = m_minMoveSpeed;
			
			newV = new Vector2D(0,1);
		}
		else if (left && !right)
		{
			if (v.getX() < 0)
				m_curSpeed += m_rate*deltaMs;
			else if (v.getX() > 0)
				m_curSpeed = m_minMoveSpeed;
				
			newV = new Vector2D(-1,0);
		}
		else if (right && !left)
		{
			if (v.getX() > 0)
				m_curSpeed += m_rate*deltaMs;
			else
				m_curSpeed = m_minMoveSpeed;
			
			newV = new Vector2D(1, 0);
		}
		else
		{
			m_curSpeed = 0;
			bMoving = false;
			
			newV = new Vector2D(0,0);
		}

		if (bMoving)
		{
			if (m_curSpeed > m_maxMoveSpeed)
				m_curSpeed = m_maxMoveSpeed;
			else if (m_curSpeed < m_minMoveSpeed)
				m_curSpeed = m_minMoveSpeed;
			
			newV = newV.scale(m_curSpeed);
		}
		
		body.setVelocity(newV);
	}
	// use acceleration to 
	protected double m_curSpeed = 0;
	protected double m_minMoveSpeed = 0;
	protected double m_maxMoveSpeed = 0;
	protected double m_deltaMinMax = 0;
	// take 1 second to go from min to max.. 
	protected double m_minToMaxTimeMs = 2000;
	protected double m_pedalToMetalTimeMs = 0;
}
