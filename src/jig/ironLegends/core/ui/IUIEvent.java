package jig.ironLegends.core.ui;

import java.awt.Point;

public interface IUIEvent 
{
	public void onEnter(int btnId,  Point mousePt);
	public void onLeave(int btnId, Point mousePt);
	public void onLeftDown(int btnId, Point mousePt);
	public void onLeftUp(int btnId, Point mousePt);
	
	// left button was pressed down, then released while staying over button
	// should be all we need for events
	//public void onLeftClick(int btnId, Point mousePt);

}
