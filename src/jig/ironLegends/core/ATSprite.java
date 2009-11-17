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
	private boolean m_bAbsRotation = false;
	private Vector2D m_offset = new Vector2D(0, 0);
	private Vector2D m_rotationOffset = new Vector2D(0, 0);

	ATSprite(final List<ImageResource> frameset) {
		super(frameset);
		setoffsetToRotation();
	}

	public ATSprite(final String rsc) {
		super(rsc);
		setoffsetToRotation();
	}

	public void setAbsRotation(boolean bAbsolute) {
		m_bAbsRotation = bAbsolute;
	}

	// set the location where the ATSprite's rotation center will be attached to
	// the MultiSpriteBody
	public void setOffset(Vector2D vOffset) {
		m_offset = vOffset;
	}

	public void setRotation(double rotRad) {
		m_rotationRad = rotRad;
	}

	public double getRotation() {
		return m_rotationRad;
	}

	// computes the "center" of the sprite as the starting point for where to
	// rotate
	private void setoffsetToRotation() {
		this.m_offsetToRotation = new Vector2D(width / 2.0, height / 2.0);
	}

	// offset from sprite's center to the rotation point
	public void setRotationOffset(Vector2D v) {
		m_rotationOffset = v;
	}

	public void render(final RenderingContext rc, double dBaseRotationRad) {
		double prevRotation = m_rotationRad;
		if (!m_bAbsRotation)
			m_rotationRad += dBaseRotationRad;
		render(rc);
		m_rotationRad = prevRotation;
	}

	@Override
	public void render(final RenderingContext rc) {
		// adjust at
		// offset to center of rotation
		// rotate
		// offset back rotation center
		// offset to offset

		AffineTransform at = AffineTransform.getTranslateInstance(position
				.getX()
				+ m_offsetToRotation.getX(), position.getY()
				+ m_offsetToRotation.getY());
		at.rotate(m_rotationRad);
		at.translate(-m_rotationOffset.getX(), -m_rotationOffset.getY());
		at.translate(-m_offsetToRotation.getX(), -m_offsetToRotation.getY());
		at.translate(m_offset.getX(), m_offset.getY());

		frames.get(visibleFrame).render(rc, at);
	}

	public Vector2D getOffset() {
		return m_offset;
	}
}