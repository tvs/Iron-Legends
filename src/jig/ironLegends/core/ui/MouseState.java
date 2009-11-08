package jig.ironLegends.core.ui;

import java.awt.Point;

import jig.engine.Mouse;

public class MouseState implements IUIEvent 
{
	// mouse is inside button
	protected boolean m_bActive = false;
	
	public boolean isActive(){return m_bActive;}

	public void update(Mouse mouse, final long deltaMs, int btnId)
	{
		Point mousePt = mouse.getLocation();
		
		m_bLeftClick = false;
		
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
			m_bLeftClick = false;
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
			m_bLeftClick = false;
			m_bLeftDown = false;
			
			//System.out.println("Button: " + btnId + ". leave");
		}
	}

	// point at which button was release
	public void onLeftClick(int btnId, Point mousePt) 
	{
		m_bLeftClick = true;
		//System.out.println("Button: " + btnId + ". left clicked");
	}

	@Override
	public void onLeftDown(int btnId, Point mousePt) 
	{
		// TODO Auto-generated method stub
		if (!m_bLeftDown)
		{
			m_leftDownPt = mousePt;
			m_bLeftDown = true;
			//System.out.println("Button: " + btnId + ". left down");
		}
	}

	@Override
	public void onLeftUp(int btnId, Point mousePt) 
	{
		// TODO Auto-generated method stub
		if (m_bLeftDown)
		{
			//System.out.println("Button: " + btnId + ". left up");
			
			m_leftUpPt = mousePt;
			m_bLeftDown = false;
			onLeftClick(btnId, mousePt);	
		}
		
	}
	
	public boolean wasLeftClicked()
	{
		return m_bLeftClick;
	}
	
	// TODO put all these parameters into a inputBtnState?(i.e. clicked, count, etc) so can have left, middle, right, etc
	protected Point m_leftDownPt;
	protected Point m_leftUpPt;
	
	protected boolean m_bLeftClick;
	protected boolean m_bLeftDown;
	protected int m_bLeftClickCount;
}
