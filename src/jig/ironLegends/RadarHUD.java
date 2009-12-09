package jig.ironLegends;



import java.util.Iterator;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.MultiSpriteBody;

public class RadarHUD extends MultiSpriteBody 
{
	private int m_worldWidth;
	private int m_worldHeight;
	private int m_radiusScreen; // radius of
	private int m_radarRange;
	private IronLegends m_game;
	Sprite m_opponentBase;
	Sprite m_teamBase;
	Sprite m_opponent;
	Sprite m_teammate;
	Sprite m_self;
	private double m_radarRange2;
	private long m_elapsedMs;

	public RadarHUD(int sx, int sy, int radiusScreen, int radarRange, IronLegends game)
	{
		super(game.m_polygonFactory.createNGon(new Vector2D(0,0), radiusScreen, 20));
		super.addSprite(IronLegends.SPRITE_SHEET + "#radar");
		super.addSprite(IronLegends.SPRITE_SHEET + "#radar-sweep");
		super.setCenterPosition(new Vector2D(sx+radiusScreen,sy+radiusScreen)); // position is treated as screen position since in a static screen layer
		//super.setRotation(0);
					
		m_radiusScreen = radiusScreen;
		m_radarRange = radarRange;
		m_radarRange2 = m_radarRange*m_radarRange;
		m_game = game;
		
		m_opponentBase	= new Sprite("radarhud_opponentbase");
		m_teamBase 		= new Sprite("radarhud_teambase");
		m_self 			= new Sprite("radarhud_self");
		m_teammate 		= new Sprite("radarhud_teammate");
		m_opponent 		= new Sprite("radarhud_opponent");
	}

	@Override
	public void render(RenderingContext rc) 
	{
		super.render(rc);
		//miniMapRender(rc);
		radarRender(rc);
	}
	
	private void radarRender(RenderingContext rc) {
		// render "self" in center of radar
		// get bearing to each rendered item and draw if withing range
		
		double sx = 1;
		double sy = 1;
		double dMaxWorld = m_worldWidth;
		if (dMaxWorld < m_worldHeight)
			dMaxWorld = m_worldHeight;
		
		sx = m_radiusScreen/(dMaxWorld/2.0);
		sy = m_radiusScreen/(dMaxWorld/2.0);
		
		double scale = sx;
		
		Vector2D selfPos = m_game.m_tank.getCenterPosition();
		Vector2D center = new Vector2D(getPosition().getX() + m_radiusScreen
									 , getPosition().getY() + m_radiusScreen);
		int startx = (int) center.getX();
		int starty = (int) center.getY();
		
		Iterator<Body> iter = m_game.m_tankLayer.iterator();
		while (iter.hasNext())
		{
			Tank t = (Tank)iter.next();
			if (!t.isActive())
				continue;
			Sprite s = null;
			if (t == m_game.m_tank)
			{
				s = m_self;				
			}
			else if (t.getTeam() != m_game.m_tank.getTeam())
			{
				s = m_opponent;
			}
			else
			{
				s = m_teammate;
			}
			
			Vector2D strobe = t.getCenterPosition().difference(selfPos);
			if (strobe.magnitude2() > m_radarRange2)
				continue;
			
			strobe.unitVector().scale(scale);
			int x = (int) (strobe.getX()*sx);
			int y = (int) (strobe.getY()*sy);
			
			s.setCenterPosition(new Vector2D(startx + x, starty + y));
			s.render(rc);
		}		
	}

	private void miniMapRender(RenderingContext rc) {
		double sx = 1;
		double sy = 1;
		double dMaxWorld = m_worldWidth;
		if (dMaxWorld < m_worldHeight)
			dMaxWorld = m_worldHeight;
		
		sx = m_radiusScreen/dMaxWorld;
		sy = m_radiusScreen/dMaxWorld;

		//Vector2D tl = m_game.m_mapCalc.screenToWorld(getPosition());
		//Vector2D tl = new Vector2D(0,0);
		Vector2D tl = getPosition();
		
		int startx = (int) tl.getX();
		int starty = (int) tl.getY();
		
		//int x = (int) (m_worldWidth/2.0*sx);
		int x = (int)(m_worldWidth*sx);
		int y = 0;

		Iterator<Body> iter = m_game.m_tankLayer.iterator();
		while (iter.hasNext())
		{
			Tank t = (Tank)iter.next();
			if (!t.isActive())
				continue;
			Sprite s = null;
			if (t == m_game.m_tank)
			{
				s = m_self;				
			}
			else if (t.getTeam() != m_game.m_tank.getTeam())
			{
				s = m_opponent;
			}
			else
			{
				s = m_teammate;
			}
			
			x = (int) (t.getCenterPosition().getX()*sx);
			y = (int) (t.getCenterPosition().getY()*sy);
			
			s.setCenterPosition(new Vector2D(startx + x, starty + y));
			s.render(rc);
		}		
	}

	@Override
	public void update(long deltaMs) {
		m_elapsedMs += deltaMs;
		// 360 degrees in 10 seconds
		double deltaDeg = 360.0/(10.0*1000.0);
		if (m_elapsedMs > 10000)
			m_elapsedMs = 0;
		setSpriteRotation(1, Math.toRadians(deltaDeg*m_elapsedMs));
	}

	public void setWorldDim(int width, int height) {
		m_worldWidth = width;
		m_worldHeight = height;
	}

}
