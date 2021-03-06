package jig.ironLegends.core.ui;

import java.awt.Point;

import jig.engine.Mouse;

public class MouseState implements IUIEvent 
{
	// mouse is inside button
	protected boolean m_bActive = false;
	
	public boolean isActive(){return m_bActive;}

	protected void updateReset()
	{
		m_bLeftPressedDown = false;
		m_bLeftReleased = false;
		m_bLeftClicked = false;
	}
	
	public MouseState()
	{
		
		m_bActive = false;
		m_bLeftClicked = false;
		m_bLeftDown = false;
	}
	
	public void update(Mouse mouse, final long deltaMs, int btnId)
	{
		Point mousePt = mouse.getLocation();
		
		updateReset();
		
		// go through and update state
		if (mouse.isLeftButtonPressed())
			onLeftDown(btnId, mousePt);
		else 
			onLeftUp(btnId, mousePt);
	}
	
	@Override
	public void onEnter(int btnId, Point mousePt) 
	{
		if (!m_bActive)
		{
			m_bActive = true;
			
			m_bLeftClicked = false;
			m_bLeftDown = false;
			//System.out.println("Button: " + btnId + ". enter");
		}
	}

	@Override
	public void onLeave(int btnId, Point mousePt) 
	{
		if (m_bActive)
		{
			m_bActive = false;
			
			m_bLeftClicked = false;
			m_bLeftDown = false;
			
			//System.out.println("Button: " + btnId + ". leave");
		}
	}

	// point at which button was release
	public void onLeftClick(int btnId, Point mousePt) 
	{
		m_bLeftClicked = true;
		//System.out.println("Button: " + btnId + ". left clicked");
	}

	@Override
	public void onLeftDown(int btnId, Point mousePt) 
	{
		if (!m_bLeftDown)
		{
			m_leftDownPt = mousePt;
			m_bLeftDown = true;
			m_bLeftPressedDown = true;
			//System.out.println("Button: " + btnId + ". left down");
		}
	}

	@Override
	public void onLeftUp(int btnId, Point mousePt) 
	{
		if (m_bLeftDown)
		{
			//System.out.println("Button: " + btnId + ". left up");
			
			m_leftUpPt = mousePt;
			m_bLeftDown = false;
			onLeftClick(btnId, mousePt);	
		}
	}
	
	// reset on update
	public boolean wasLeftClicked()
	{
		return m_bLeftClicked;
	}
	// reset on update
	public boolean wasLeftPressedDown()
	{
		return m_bLeftPressedDown;
	}
	// reset on update
	public boolean wasLeftReleased()
	{
		return m_bLeftReleased;
	}
	public Point LeftDownPt()
	{
		return m_leftDownPt;
	}
	
	// TODO put all these parameters into a inputBtnState?(i.e. clicked, count, etc) so can have left, middle, right, etc
	protected Point m_leftDownPt;
	protected Point m_leftUpPt;
	
	protected boolean m_bLeftDown;	
	
	protected boolean m_bLeftClicked;
	protected boolean m_bLeftPressedDown;
	protected boolean m_bLeftReleased;
}
