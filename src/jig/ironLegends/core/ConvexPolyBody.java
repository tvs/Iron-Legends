package jig.ironLegends.core;

import jig.engine.RenderingContext;
import jig.engine.physics.Body;
import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.physics.vpe.PersonsConvexPolygon;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;

public class ConvexPolyBody extends Body 
{

	protected ConvexPolygon m_shape = null;
	ConvexPolyBody(ConvexPolygon shape)
	{
		// TODO should create an empty sprite to supply
		super(IronLegends.SPRITE_SHEET + "#cpb");
		m_shape = shape;
		setPosition(m_shape.getPosition());
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
