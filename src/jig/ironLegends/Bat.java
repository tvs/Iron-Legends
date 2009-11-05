package jig.ironLegends;

import java.util.Iterator;
import java.util.Random;

import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.util.Vector2D;

public class Bat extends Creature 
{
	protected BodyLayer<Body> m_caves;
	protected BodyLayer<Body> m_creatures;
	
	static final int MIN_SPEED = 25;
	static final int MAX_SPEED = 75;
	static final int MIN_TO_MAX_MS = 6000;
	
	static final long MIN_HUNT_MS = 2000;
	static final long MAX_HUNT_MS = 5000;	
		
	protected double m_maxHuntTimeMs;
	
	Bat(boolean bHorizontal, ConvexPolygon shape, Mitko mitko, BodyLayer<Body> caves, BodyLayer<Body> creatures)
	{
		super("bat", FRAMES, FRAME_DURATION_MS, bHorizontal, shape, mitko);
		setOrtho(false);

		m_engine 	= new GroundEngine(MIN_SPEED, MAX_SPEED, MIN_TO_MAX_MS);
		
		m_bInMission = false;
		m_bIdleMs = 0;

		m_rand = new Random();
		m_caves = caves;
		m_engine.m_curSpeed = Math.sqrt(getVelocity().magnitude2());
		m_huntTimeMs = 0;
		m_maxHuntTimeMs = 0;
		m_meal = null;
		m_creatures = creatures;
		m_lastCave = null;
		m_bAte = false;
	}
	
	@Override
	int getTrapScore()
	{
		return 25;
	}
	@Override
	public void setScared(boolean bScared)
	{
		// bats never get scared
		/*
		if (bScared)
			m_animator.setFrameBase(m_frames);
		else
			m_animator.setFrameBase(0);
		*/
	}

	protected int m_missionStep;
	protected double m_huntTimeMs;
	public Body getMeal(){ return m_meal;}
	
	public void move(long deltaMs)
	{
		if (m_bInMission)
		{
			switch (m_missionStep)
			{
				case 0:
				{
					if (m_engine.move(this, m_dest1, deltaMs))
					{
						// reached destination
						m_missionStep += 1;
						// select creature or mitko
						//System.out.println("halfway point");
						m_maxHuntTimeMs = 0;
					}
				}
				break;
				case 1:
				{
					// either go for another creature or return to cave
					// 2 -5 seconds
					boolean selectNewMeal = false;
					if (m_maxHuntTimeMs == 0)
					{
						// 
						selectNewMeal = true;
					}
					else
					{
						m_huntTimeMs += deltaMs;
						if (m_huntTimeMs > m_maxHuntTimeMs || m_bAte)
						{
							m_huntTimeMs = 0;
							m_bAte = false;
							// 50% new meal, 50% finish hunt
							double d = m_rand.nextDouble();
							if (d < 0.35)
							{
								//System.out.println("chance to cave");
								m_missionStep += 1;
							}
							else
							{
								selectNewMeal = true;
							}
						}
					}
					
					if (selectNewMeal)
					{
						//System.out.println("selecting new meal");
						// hunt for 2-3 seconds
						m_bAte = false;
						m_maxHuntTimeMs = MIN_HUNT_MS + m_rand.nextDouble()*(MAX_HUNT_MS-MIN_HUNT_MS);
						m_huntTimeMs = 0;
						//m_meal = m_mitko;
						
						int countCreatures = 0;
						{
							Iterator<Body> iter = m_creatures.iterator();
							while (iter.hasNext())
							{
								Body b = iter.next();
								if (b.isActive())
								{
									countCreatures++;
								}
							}
						}

						// try not to have mitko continuously chased
						double d = m_rand.nextDouble();
						if (countCreatures < 1 || (d > .5 && m_meal != m_mitko))
						{
							m_meal = m_mitko;
							//System.out.println("hunting mitko");
						}
						else
						{
							d = m_rand.nextDouble();
							int i = (int) (countCreatures*d);
							Iterator<Body> iter = m_creatures.iterator();
							int j = 0;
							while (iter.hasNext())
							{
								Body b = iter.next();
								if (b.isActive())
								{
									if (j == i)
									{
										m_meal = b;
										//System.out.println("hunting: " + b);
										break;
									}
									j++;
								}
								else
								{
									//System.out.println("found inactive ant");
								}
							}
							
						}
						// 50% mitko, 50% some other creature
						if (m_meal == null)
						{
							m_missionStep += 1;
							//System.out.println("next=cave");
						}
					}
					
					if (m_missionStep == 1 && m_meal != null)
					{
						//m_dest1 = new Vector2D(m_dest1.getX() + m_meal.getWidth()/2, m_dest1.getY() + m_meal.getHeight()/2);
						m_dest1 = m_meal.getCenterPosition();
						m_engine.move(this,m_dest1, deltaMs);
					}
				}
				break;
				case 2:
					if (m_engine.move(this, m_caveLoc, deltaMs))
					{
						// reached destination
						m_bInMission = false;
						setVelocity(new Vector2D(0,0));
						m_engine.m_curSpeed = 0;
						rotation = 0;
						m_bIdleMs = 0;
						m_animator.setFrameBase(2);
					}			
				break;
			}
		}
	}
	
	public void setAte(boolean bAte)
	{
		m_bAte = bAte;
	}
	//*
	@Override
	public void update(long deltaMs) 
	{
		// updates position, velocity, orientation, and animation
		super.update(deltaMs);
		
		// every 5-10 seconds, if not in "mission", choose next mission
		// choose cave, move towards cave, when 1/2 - 3/4 way there
		// then choose a meal, hunt for meal for 2-4 seconds before
		// heading towards chosen cave.
		if (!m_bInMission)
		{
			m_bIdleMs += deltaMs;

			double per = m_rand.nextDouble();

			// every 3-10 seconds
			if (m_bIdleMs > 3000 + per*7000)
			{
				//System.out.println("On the prowl");
				m_bInMission = true;
				m_animator.setFrameBase(0);

				m_missionStep = 0;
				m_bIdleMs = 0;
				// select target cave location
				int cave = (int) (per*m_caves.size());
				
				Body b = m_caves.get(cave);
				
				if (m_caves.size() > 1 && m_lastCave != null)
				{
					if (b == m_lastCave)
					{
						if (cave > 0)
							cave--;
						else
							cave++;
					}
					b = m_caves.get(cave);
				}
				
				m_caveLoc = b.getCenterPosition();
				m_startLoc = getCenterPosition();
				Vector2D diff = m_caveLoc.difference(m_startLoc);
				m_dest1 = new Vector2D(m_startLoc.getX() + diff.getX()/2.0, m_startLoc.getY() + diff.getY()/2.0);
				m_lastCave = b;
			}
		}
		
		move(deltaMs);
	}
	//*/
	protected static final int WIDTH	= 34;
	protected static final int HEIGHT 	= 34;
	protected boolean m_bInMission;
	protected double m_bIdleMs;
	protected Random m_rand;
	protected Vector2D m_caveLoc;
	protected Vector2D m_dest1;
	protected Vector2D m_startLoc;
	protected Body	   m_lastCave;
	protected Body		m_meal;
	protected boolean  m_bAte;
	GroundEngine m_engine;

	protected static final int FRAMES = 2;
	protected static final long FRAME_DURATION_MS = CREATURE_FRAME_DURATION_MS*4;
}
