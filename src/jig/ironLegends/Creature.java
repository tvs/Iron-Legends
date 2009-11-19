package jig.ironLegends;

import jig.engine.RenderingContext;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.MultiSpriteBody;

public class Creature extends MultiSpriteBody 
{
	protected Mitko m_mitko;
	Animator m_animator;
	protected double m_lastMoveChangeMs;
	protected int m_frames;
	
	Creature(String sprite, int frames, long frameDurationMs
			, boolean bHorizontal, ConvexPolygon shape, Mitko mitko)
	{
		super(shape, IronLegends.SPRITE_SHEET + "#" + sprite);
		
		if (bHorizontal)
			velocity = new Vector2D(-20,0);
		else
			velocity = new Vector2D(0, 20);
		
		m_animator = new Animator(frames, frameDurationMs, 0);
		m_lastMoveChangeMs = 0;
		m_mitko = mitko;
		m_frames = frames;
		m_bOrtho = true;
		m_trapScore = 1;
	}

	protected void setOrtho(boolean bOrtho)
	{
		m_bOrtho = bOrtho;
	}

	public double getLastMoveChange()
	{
		return m_lastMoveChangeMs;
	}
	public void resetMoveChange()
	{
		m_lastMoveChangeMs = 0;		
	}
	public void setScared(boolean bScared)
	{
		if (bScared)
			m_animator.setFrameBase(m_frames);
		else
			m_animator.setFrameBase(0);
	}

	int getTrapScore()
	{
		return m_trapScore;
	}
	@Override
	public void render(RenderingContext rc) 
	{
		if (!active)
			return;
		super.render(rc);		
	}
	
	@Override
	public void update(long deltaMs) 
	{
		if (m_mitko.canTrap())
			setScared(true);
		else
			setScared(false);
		
		boolean moving = velocity.getX() != 0 || velocity.getY() != 0;

		m_lastMoveChangeMs += deltaMs;
		
		Vector2D translateVec = null;
		if (moving)
		{
			translateVec = velocity.scale(deltaMs/1000.0);
			position = position.translate(translateVec);
			Vector2D preClamp = position; 
			position = position.clampX(0, IronLegends.WORLD_WIDTH - getWidth());
			position = position.clampY(0, IronLegends.WORLD_HEIGHT - getHeight());
			
			// need to adjust movement direction .. (reverse direction of clamp
			if (position.getX() != preClamp.getX())
			{
				velocity = new Vector2D(-velocity.getX(), velocity.getY());
			}
			if (position.getY() != preClamp.getY()	)
			{
				velocity = new Vector2D(velocity.getX(), -velocity.getY());
			}
			
			setPosition(position);
			
			// based on move vector, set orientation
			boolean bOrtho = m_bOrtho;
			
			if (!m_bOrtho)
			{
				//double dBrgRad = Math2D.trueBrg(new Vector2D(0,0), new Vector2D(velocity.getX(), -velocity.getY()));
				//double dBrgRad = MPmath2D.trueBrg(new Vector2D(0,0), velocity);
				double dBrgRad = Math.atan2(0 -velocity.getY(), 
						0	- velocity.getX());				
				dBrgRad += Math.PI/2.0;
				if (dBrgRad < 0)
					dBrgRad += Math.PI*2.0;
				if (dBrgRad > Math.PI*2.0)
					dBrgRad -= Math.PI*2.0;
				setRotation(dBrgRad);
			}
			
			if (bOrtho)
			{
				if (translateVec.getY() > 0)
					setRotation(Math.toRadians(90));
				else if (translateVec.getY() < 0)
					setRotation(Math.toRadians(270));
				else if (translateVec.getX() < 0)
					setRotation(Math.toRadians(180));
				else if (translateVec.getX() > 0)
					setRotation(Math.toRadians(0));
			}
		}
	
		//if (m_animator.update(deltaMs, translateVec))
		m_animator.update(deltaMs, translateVec);
		{
			setFrame(m_animator.getFrame());
		}	
	}
	
	protected static final long CREATURE_FRAME_DURATION_MS = 150;
	protected boolean m_bOrtho;
	protected int m_trapScore;
}
