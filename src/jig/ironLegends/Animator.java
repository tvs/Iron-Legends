package jig.ironLegends;

import jig.engine.util.Vector2D;

public class Animator 
{
	public Animator(int frames, long frameDurationMs, int curFrame)
	{
		m_frames = frames;
		m_frameDurationMs = frameDurationMs;
		m_curFrame = curFrame;
		m_frameBase = 0;
	}
	public int getFrame(){return m_curFrame + m_frameBase;}
	public void setFrame(int frame){m_curFrame = frame;}
	public void setFrameBase(int frameBase){m_frameBase = frameBase;}
	public void setFrameDurationMs(long durationMs)
	{
		m_frameDurationMs = durationMs;
	}

	// returns true if frame change required
	public boolean update(final long deltaMs, Vector2D translateVec)
	{
		// always incrementing deltaMs will "animate" the character when
		// the character is nudged in a direction so that the character
		// doesn't look like it moved without animation
		m_deltaMs += deltaMs;
		if (translateVec != null)
		{
			//m_deltaMs += deltaMs;
			if (m_deltaMs > m_frameDurationMs)
			{
				m_deltaMs = 0;
				m_curFrame++;
				m_curFrame %= m_frames;
				return true;
			}
		}
		return false;
	}
	
	protected final int m_frames;
	protected long m_frameDurationMs;
	protected long m_deltaMs;
	protected int m_curFrame;
	protected int m_frameBase;
}
