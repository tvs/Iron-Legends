package jig.ironLegends.core.ui;

import java.util.List;

import jig.engine.ImageResource;

/**
 * 
 * A class to handle rollover events on buttons (changes the frame the sprite
 * displays).
 * 
 * <b>Note: Expects that the supplied image resource has at least two frames</b>
 * 
 *  @author Travis Hall
 */
public class RolloverButton extends Button {

	public RolloverButton(int id, int sx, int sy, List<ImageResource> rsc) {
		super(id, sx, sy, rsc);
	}
	
	public RolloverButton(int id, int sx, int sy, String rsc) {
		super(id, sx, sy, rsc);
	}
	
	/**
	 * Swap frames when the mouse is hovering over the button
	 */
	@Override
	protected void mouseEntered() {
		setFrame(1);
	}
	
	/**
	 * Swap back to the normal state when the mouse is no longer hovering
	 */
	@Override 
	protected void mouseLeft() {
		setFrame(0);
	}
	
}
