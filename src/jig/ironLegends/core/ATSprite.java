package jig.ironLegends.core;

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;

/**
 * Position is top left of sprite
 */
public class ATSprite extends Sprite {
	private double m_rotationRad = 0;
	private Vector2D m_offsetToRotation;

	ATSprite(final List<ImageResource> frameset) {
		super(frameset);
		setoffsetToRotation();
	}

	public ATSprite(final String rsc) {
		super(rsc);
		setoffsetToRotation();
	}

	public void setRotation(double rotRad) {
		m_rotationRad = rotRad;
	}

	public double getRotation() {
		return m_rotationRad;
	}

	public void setoffsetToRotation() {
		this.m_offsetToRotation = new Vector2D(width / 2.0, height / 2.0);
	}

	public void setoffsetToRotation(Vector2D m_offsetToRotation) {
		this.m_offsetToRotation = m_offsetToRotation;
	}

	public Vector2D getoffsetToRotation() {
		return m_offsetToRotation;
	}

	public void render(final RenderingContext rc, double dBaseRotationRad) {
		double prevRotation = m_rotationRad;
		m_rotationRad += dBaseRotationRad;
		render(rc);
		m_rotationRad = prevRotation;
	}

	@Override
	public void render(final RenderingContext rc) {
		// adjust at
		// offset to position
		// offset to center
		// rotate
		// offset back
		AffineTransform at = AffineTransform.getTranslateInstance(position
				.getX()
				+ m_offsetToRotation.getX(), position.getY()
				+ m_offsetToRotation.getY());

		at.rotate(m_rotationRad);
		at.translate(-m_offsetToRotation.getX(), -m_offsetToRotation.getY());
		frames.get(visibleFrame).render(rc, at);
	}
}