package jig.ironLegends;

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

public class SpawnInfo {

	private int m_sequence; ///< sequence per color, not global sequence
	private String m_name;	///< e.g. redspawn or bluespawn
	private Vector2D m_centerPos;
	private double m_rotationDeg;
	private Body m_bOccupied;
	private int m_mapItemSeq;
	
	public SpawnInfo(String name, int sequence, int mapItemSeq)
	{
		m_name = name;
		setSequence(sequence);
		m_bOccupied = null;
		setMapItemSeq(mapItemSeq);
	}
	public void setCenterPosition(Vector2D centerPosition) {
		m_centerPos = centerPosition;		
	}
	public void setRotationDeg(double rotDeg) {
		m_rotationDeg = rotDeg;		
	}
	public void setOccupied(Body bOccupied)
	{
		m_bOccupied = bOccupied;
	}
	public boolean isFree() {
		return m_bOccupied == null;
	}
	public String name() {
		return m_name;
	}
	public Vector2D centerPosition() {
		return m_centerPos;
	}
	public double rotDeg() {
		return m_rotationDeg;
	}
	public boolean isOccupied() {
		return m_bOccupied != null;
	}
	public void update() {
		if (m_bOccupied != null) {
			if (!m_centerPos.epsilonEquals(m_bOccupied.getCenterPosition(), m_bOccupied.getHeight())) {
				// free slot
				m_bOccupied = null;
			}
		}
	}
	public void setMapItemSeq(int m_mapItemSeq) {
		this.m_mapItemSeq = m_mapItemSeq;
	}
	public int getMapItemSeq() {
		return m_mapItemSeq;
	}
	public void setSequence(int m_sequence) {
		this.m_sequence = m_sequence;
	}
	public int getSequence() {
		return m_sequence;
	}
}
