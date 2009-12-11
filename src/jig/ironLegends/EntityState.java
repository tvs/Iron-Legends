package jig.ironLegends;

import jig.engine.util.Vector2D;

public class EntityState 
{
	public int m_entityNumber;
	
	// entity state flags
	public static int ESF_MOVING 	= 0x00000001;
	public static int ESF_ACTIVE 	= 0x00000002;
	public static int ESF_TT_BASIC 	= 0x00000010;
	public static int ESF_TT_SPEED	= 0x00000020;
	public static int ESF_TT_ARMORED= 0x00000040;
	
	public static int ESF_PU_SHIELD 	= 0x00000100;
	public static int ESF_PU_MINE 		= 0x00000200;
	public static int ESF_PU_DBL_CANNON = 0x00000400;
	
	public int m_flags;
	
	public Vector2D m_pos;
	public double m_tankRotationRad;
	public double m_turretRotationRad;
	public int m_team;
	
	public int m_speed;
	public int m_health;
	public int m_maxHealth;
}
