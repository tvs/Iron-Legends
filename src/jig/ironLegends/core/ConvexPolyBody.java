package jig.ironLegends.core;

import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.physics.Body;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.physics.vpe.PersonsConvexPolygon;
import jig.engine.util.Vector2D;

public class ConvexPolyBody extends Body 
{

	protected ConvexPolygon m_shape = null;
	ConvexPolyBody(ConvexPolygon shape)
	{
		super(getEmptySprite());
		setShape(shape);
	}
	
	protected void setShape(ConvexPolygon shape)
	{
		m_shape = shape;
		if (m_shape != null)
			setPosition(m_shape.getPosition());
	}
	
	private static String getEmptySprite() {
		if (ResourceFactory.getFactory().areFramesLoaded("cpb") == false) {
			PaintableCanvas.loadDefaultFrames("cpb", 4, 4, 1, JIGSHAPE.RECTANGLE, null);
		}	
		return "cpb";
	}

	public Vector2D getOffsetToRotation()
	{
		return ((PersonsConvexPolygon)m_shape).getOffsetToRotation();
	}
	@Override
	public void update(long deltaMs) 
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void render(final RenderingContext rc)
	{
		// nothing to see here, move along
	}
	public ConvexPolygon getShape() 
	{
		return m_shape;
	}

	@Override
	public void setPosition(final Vector2D pos)
	{
		position = pos;
		m_shape.setPosition(pos);		
	}
	
	@Override
	public Vector2D getPosition()
	{
		return m_shape.getPosition();
	}
}
