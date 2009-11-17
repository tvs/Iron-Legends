package jig.ironLegends.mapEditor;

import jig.engine.util.Vector2D;
import jig.ironLegends.core.ATSprite;

public class MapItemPersist 
{
	String m_sEncoding;
	String m_sName;
	String m_sResource;
	double m_centerX;
	double m_centerY;
	double m_rotDeg;
	
	public String encoding(){ return m_sEncoding;}
	public String name(){ return m_sName;}
	public String resource(){return m_sResource;}
	public double centerX(){ return m_centerX;}
	public double centerY(){ return m_centerY;}
	public double rotDeg(){return m_rotDeg;}
	
	public MapItemPersist(String sEncoding)
	{
		// TODO error handling for bad map item encoding
		String tokens[] = sEncoding.split(":");
		m_sName = tokens[0];
		
		m_centerX = Double.parseDouble(tokens[1]);
		m_centerY = Double.parseDouble(tokens[2]);
		m_rotDeg = Double.parseDouble(tokens[3]);
		
		m_sResource = tokens[4];		
	}
	
	public MapItemPersist(String sName, String sResource, double centerX, double centerY, double rotDeg)
	{
		m_sName = sName;
		m_sResource = sResource;
		m_centerX = centerX;
		m_centerY = centerY;
		m_rotDeg = rotDeg;
	
		m_sEncoding = encode(sName, sResource, centerX, centerY, rotDeg);	
	}
	
	public static String encode(String sName, String sResource, double centerX, double centerY, double rotDeg)
	{
		String sEncoding = sName;
		sEncoding += ":" + centerX;
		sEncoding += ":" + centerY;
		sEncoding += ":" + rotDeg;
		sEncoding += ":" + sResource;

		return sEncoding;
	}
	
	
}
