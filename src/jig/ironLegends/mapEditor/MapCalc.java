package jig.ironLegends.mapEditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

// sorry, took code from ScrollingScreenGame class and wrapped into a "mapcalc"
// so I could perform the transforms when I desired.
public class MapCalc 
{
	// hack portions of scrolling screen game so can gain access to transform
	protected int m_screenWidth;
	protected int m_screenHeight;
	protected Vector2D screenCenter;
	
	private final AffineTransform worldToScreenTransform = new AffineTransform();
	private AffineTransform screenToWorldTransform = new AffineTransform();

	private Rectangle worldBounds;
	
	public MapCalc(int screenWidth, int screenHeight)
	{
		m_screenWidth = screenWidth;
		m_screenHeight = screenHeight;
		screenCenter = new Vector2D(screenWidth/2, screenHeight/2); 
	}
	
	public Vector2D screenToWorld(Point screenPt)
	{
		return screenToWorld(new Vector2D(screenPt.x, screenPt.y));
	}
	
	public Vector2D screenToWorld(Vector2D s)
	{
		Point2D p = new Point2D.Double(s.getX(), s.getY());
		screenToWorldTransform.transform(p,p);
		return new Vector2D(p.getX(), p.getY());
	}
	
	/**
	 * Computes a screen coordinate from a world coordinate.
	 * @param w the world coordinates passed in as a Vector2D
	 * @return the screen coordinate as a Vector2D
	 */
	public Vector2D worldToScreen(Vector2D w)
	{
		Point2D p = new Point2D.Double(w.getX(), w.getY());
		worldToScreenTransform.transform(p,p);
		return new Vector2D(p.getX(), p.getY());
	}
		
	/**
	 * Converts a world location to a screen x location.
	 * 
	 * @param wx
	 * 			the world x location
	 * @param wy
	 * 			the world y location
	 * @return
	 * 			the screen x location of this tile
	 */
	public double worldToScreenX(double wx, double wy) {
		double[] worldToScreenMatrix = new double[6];
		worldToScreenTransform.getMatrix(worldToScreenMatrix);
		return ((wx * worldToScreenMatrix[0]) + 
				(wy * worldToScreenMatrix[2]) +
				worldToScreenMatrix[4]);
	}

	/**
	 * Converts a world location to a screen y location.
	 * 
	 * @param wx
	 * 			the world x location
	 * @param wy
	 * 			the world y location
	 * @return
	 * 			the screen y location of this tile
	 */
	public double worldToScreenY(double wx, double wy) {
		double[] worldToScreenMatrix = new double[6];
		worldToScreenTransform.getMatrix(worldToScreenMatrix);
		return ((wx * worldToScreenMatrix[1]) + 
				(wy * worldToScreenMatrix[3]) +
				worldToScreenMatrix[5]);
	}
	public void setWorldBounds(final int x, final int y, final int width,
			final int height) {
		worldBounds = new Rectangle(x, y, width, height);
	}

	/**
	 * Sets the scrolling boundary of the world. 
	 * You cannot scroll beyond it.
	 * 
	 * @param bounds
	 *            the rectangular region of the world's 'interaction' zone
	 */
	public void setWorldBounds(final Rectangle bounds) {
		this.worldBounds = new Rectangle(bounds);
	}
	
	/**
	 * Returns the bounds of the world as a rectangle
	 * 
	 * @return bounds
	 *            the rectangular region of the world's 'interaction' zone
	 */
	public Rectangle getWorldBounds()
	{
		return worldBounds;
	}

	/**
	 * Will center the screen on a body in the game world.
	 * 
	 * @param b
	 *            The body to center the screen on.
	 */
	public void centerOn(final Body b) {
		centerOnPoint(b.getCenterPosition());
	}

	/**
	 * Will center the screen on a game world point.
	 * 
	 * @param p
	 *            a point in game coordinates.
	 */
	public void centerOnPoint(final Vector2D p) {
		matchPoints(p, getCenter());
	}

	/**
	 * Will center the screen on a game world point.
	 * 
	 * @param x
	 *            x in game coordinates.
	 * @param y
	 *            y in game coordinates.
	 */
	public void centerOnPoint(final int x, final int y) {
		centerOnPoint(new Vector2D(x, y));
	}

	/**
	 * Will sync a screen point with a game world point. If the game point lies
	 * outside of the world bounds it will clamp it to the boundary.
	 * 
	 * @param gamePoint
	 *            the target point (in world coordinates)
	 * @param screenPoint
	 *            the target point (in screen coordinates)
	 */
	private void matchPoints(final Vector2D gamePoint,
			final Vector2D screenPoint) {

		Vector2D n;
		if (worldBounds != null) {
			// clamp the game point to the world's 'interaction' zone
			n = gamePoint.clamp(worldBounds).difference(screenPoint);
		} else {
			// use the unadulterated game point
			n = gamePoint.difference(screenPoint);
		}
		worldToScreenTransform.setToTranslation(-n.getX(), -n.getY());
		try {
			screenToWorldTransform = worldToScreenTransform.createInverse();
		} catch (NoninvertibleTransformException e) {
			System.err.println("Scrolling Screen Game transform must be invertable");
			e.printStackTrace();
		}
	}

	/**
	 * Will return the center of the window, based on it's height and width.
	 * NOTE: This is in screen coordinates, not world coordinates
	 * 
	 * @return The center of the screen.
	 */
	public Vector2D getCenter() {
		return screenCenter;
	}
	
	public AffineTransform getScreenToWorldTransform()
	{
		return screenToWorldTransform;
	}
	public AffineTransform getWorldToScreenTransform()
	{
		return worldToScreenTransform;
	}

}
