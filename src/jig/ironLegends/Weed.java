package jig.ironLegends;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Weed extends VanillaAARectangle
{
 public Weed(int x, int y, String encoding)
 {
	 super(IronLegends.SPRITE_SHEET + "#weed1");
	 setPosition(new Vector2D(x,y));

	 m_courageTimeMs = 0;
	 m_bFixedTime = false;

	 if (encoding.length() > 1)
	 {
		 m_courageTimeMs = 5*1000;
		 //m_courageTimeMs = courageTimeSecs*1000;
		 m_bFixedTime = true;
	 }
 }
 
 public Weed(int x, int y)
 {
	 super(IronLegends.SPRITE_SHEET + "#weed1");
	 setPosition(new Vector2D(x,y));

	 m_courageTimeMs = 0;
	 m_bFixedTime = false;
 }

 public boolean isPowerUp()
 {
	 if (m_bFixedTime)
		 return true;
	 return m_courageTimeMs > 0?true:false;
 }
 
 @Override
 public void update(long deltaMs) 
 {
	 // if blooming, indicate so by setting frame set
	 if (isPowerUp())
	 {
		 setFrame(1);
	 }
	 else
		 setFrame(0);
 }
 
 static final int WIDTH 	= 10;
 static final int HEIGHT 	= 10;
 protected int m_courageTimeMs;
 protected boolean m_bFixedTime;
}
