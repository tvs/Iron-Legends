package jig.ironLegends;

import java.util.BitSet;

public class CommandState {

	// switch to byte once get closer to finalizing
	public static long CMD_LEFT  	= 0x00000001;	// left key is down
	public static long CMD_RIGHT 	= 0x00000002;	// right key is down
	public static long CMD_UP		= 0x00000004;	// up key is down
	public static long CMD_DOWN		= 0x00000008;	// down key is down
	public static long CMD_FIRE		= 0x00000010;	// fire key is down
	public static long CMD_DIE		= 0x00000020;	// cheat code: die 
	
	private long m_commandFlag;
	private int m_entityNumber;
	private double m_turretRotationRad;
	
	public CommandState()
	{
		m_commandFlag = 0;
		m_entityNumber = 0;
	}
	
	public long getState()
	{
		return m_commandFlag;
	}
	
	public boolean isActive(long cmd)
	{
		if ((m_commandFlag & cmd) == cmd)
			return true;
		return false;
	}
	public void setState(long cmd, boolean active)
	{
		if (active)
		{
			m_commandFlag |= cmd;
		}
		else
		{
			m_commandFlag &= ~cmd;
		}
		
		
	}

	public void setEntityNumber(int entityNumber) {
		m_entityNumber = entityNumber;
	}
	public int getEntityNumber()
	{
		return m_entityNumber;
	}

	public void setTurretRotationRad(double turretRotationRad) {
		m_turretRotationRad = turretRotationRad;
	}
	
	public double getTurretRotationRad()
	{
		return m_turretRotationRad;
	}
	
}
