package jig.ironLegends;

import java.util.BitSet;

public class CommandState {

	public static long CMD_LEFT  		= 0x00000001;	// left key is down
	public static long CMD_RIGHT 		= 0x00000002;	// right key is down
	public static long CMD_UP			= 0x00000004;	// up key is down
	public static long CMD_DOWN		= 0x00000008;	// down key is down
	public static long CMD_FIRE		= 0x00000010;	// fire key is down
	
	public CommandState()
	{
		m_commandFlag = 0;
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
	
	private long m_commandFlag;
}
