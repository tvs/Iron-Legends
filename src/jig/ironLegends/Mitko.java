package jig.ironLegends;

import java.awt.geom.AffineTransform;

import jig.engine.RenderingContext;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.MultiSpriteBody;
import jig.ironLegends.mapEditor.MapCalc;

//public class Mitko extends VanillaAARectangle 
//public class Mitko extends VanillaPolygon
public class Mitko extends MultiSpriteBody
{
	Vector2D m_resetPosition;
	//static final int MIN_SPEED = 50;
	//static final int MAX_SPEED = 200;
	//static final int MIN_TO_MAX_MS = 1000;
	static final int MIN_SPEED = 125;
	static final int MAX_SPEED = 125;
	static final int MIN_TO_MAX_MS = 0;

	protected static final long WIDTH = 16;
	protected static final long HEIGHT = 14;
	protected static final int MITKO_FRAMES = 4;
	protected static final long FRAME_DURATION_MS = 150;
	protected static final long FEINT_FRAME_DURATION_MS = 400;
	
	protected static final long FRAME_DURATION_PIXELS = WIDTH/MITKO_FRAMES;
	MapCalc m_mapCalc;
	
	public Mitko(ConvexPolygon shape, MapCalc mapCalc)
	{
		super(shape, IronLegends.SPRITE_SHEET + "#mitko");
		m_mapCalc = mapCalc;
		
		int antHandle = super.addSprite(IronLegends.SPRITE_SHEET + "#ant");
		super.setSpriteRotation(antHandle, Math.toRadians(90));
		super.setSpriteVisible(antHandle, false);
		
		position = shape.getPosition();

		m_animator 	= new Animator(MITKO_FRAMES,FRAME_DURATION_MS, 0);
		m_engine 	= new GroundEngine(MIN_SPEED, MAX_SPEED, MIN_TO_MAX_MS);
		
		m_resetPosition = position;
		m_powerupRemainingMs = 0;
		m_storedPowerUps = 0;
	}
	public void setResetPosition(Vector2D pos)
	{
	
		m_resetPosition = pos;
	}
	
	public void reset()
	{
		setPosition(m_resetPosition);
		
		velocity = new Vector2D(0,0);
		
		m_bFeinting 	= false;
		m_bWaitForReset = false;
		m_powerupRemainingMs = 0;
		
		m_animator.setFrameBase(0);
		m_animator.setFrameDurationMs(FRAME_DURATION_MS);

		setFrame(m_animator.getFrame());
	}
	public void newGame()
	{
		m_storedPowerUps = 0;
		reset();
	}
	
	public void move(boolean left, boolean right, boolean up, boolean down, long deltaMs)
	{
		m_engine.move(this, left, right, up, down, deltaMs);	
	}

	protected double m_powerupRemainingMs;
	public double getPowerUpRemainingMs()
	{
		return m_powerupRemainingMs;
	}
	public void activatePowerup(int powerup)
	{
		m_powerupRemainingMs = 8000;
	}
	public boolean canTrap()
	{
		return m_powerupRemainingMs > 0?true:false;
	}
	
	@Override
	public void render(RenderingContext rc) 
	{
		if (!active) return;
		super.render(rc);
	}
	
	public void update(long deltaMs) 
	{
		
		if (m_bFeinting)
		{
			if (m_bWaitForReset)
				return;
			
			if (m_animator.update(deltaMs, new Vector2D(0,0)))
			{	
				if (m_animator.getFrame() == 7)
					m_bWaitForReset = true;
				setFrame(m_animator.getFrame());
			}	
			return;
		}
		
		if (m_powerupRemainingMs > 0)
			m_powerupRemainingMs -= deltaMs;
		
		//*		
		boolean moving = velocity.getX() != 0 || velocity.getY() != 0;

		Vector2D translateVec = null;
		if (moving)
		{
			translateVec = velocity.scale(deltaMs/1000.0);
			System.out.println("Pos: " + position);
			position = position.translate(translateVec);
			position = position.clampX(0, m_mapCalc.getWorldBounds().getWidth() - getWidth());
			position = position.clampY(0, m_mapCalc.getWorldBounds().getHeight() - getHeight());
			setPosition(position);
			if (translateVec.getY() > 0)
				setRotation(Math.toRadians(90));
			else if (translateVec.getY() < 0)
				setRotation(Math.toRadians(270));
			else if (translateVec.getX() < 0)
				setRotation(Math.toRadians(180));

			else if (translateVec.getX() > 0)
				setRotation(Math.toRadians(0));
		}
		//*/
		/*
		// debugging rotations of VanillaPolygon when constructing a sphere
		m_deltaMs += deltaMs;
		if (m_deltaMs > 4000)
		{
			m_deltaMs = 0;
			setRotation(Math.toRadians(0));
		}
		else if (m_deltaMs > 3000)
		{
			setRotation(Math.toRadians(90));
		}
		else if (m_deltaMs > 2000)
		{
			setRotation(Math.toRadians(180));
		}
		else if (m_deltaMs > 1000)
		{
			setRotation(Math.toRadians(270));
		}
		*/
		// set appropriate frame set (currently just 1)
		// advance frame
		if (m_animator.update(deltaMs, translateVec))
		{			
			setFrame(m_animator.getFrame());
		}	
	}
	// for debugging animations
	protected double m_deltaMs;
	
	public boolean isFainting()
	{
		return m_bFeinting;
	}
	public boolean doneFainting()
	{
		return m_bWaitForReset;
	}
	public void activateFaint()
	{	
		if (m_bFeinting)
			return;

		m_bFeinting = true;
		m_animator.setFrame(0);
		m_animator.setFrameBase(4);
		m_animator.setFrameDurationMs(FEINT_FRAME_DURATION_MS);
	}
	
	public void collectPowerUp()
	{
		m_storedPowerUps += 1;		
	}
	public int getStoredPowerUps()
	{
		return m_storedPowerUps;
	}
	public void smoke()
	{
		if (m_storedPowerUps > 0)
		{
			m_storedPowerUps -= 1;
			activatePowerup(1);
		}
	}
	
	Animator m_animator;
	GroundEngine m_engine;
	
	protected boolean m_bWaitForReset = false;
	protected boolean m_bFeinting = false;
	protected int m_storedPowerUps;
}
