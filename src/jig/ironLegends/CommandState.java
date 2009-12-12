package jig.ironLegends;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jig.ironLegends.oxide.util.PacketBuffer;

public class CommandState {

	public static byte CMD_LEFT  	= 0x01;	// left key is down
	public static byte CMD_RIGHT 	= 0x02;	// right key is down
	public static byte CMD_UP		= 0x04;	// up key is down
	public static byte CMD_DOWN		= 0x08;	// down key is down
	public static byte CMD_FIRE		= 0x10;	// fire key is down
	public static byte CMD_DIE		= 0x20;	// cheat code: die 
	
	private byte m_commandFlag;
	private int m_entityNumber;
	private double m_turretRotationRad;
	
	public CommandState()
	{
		m_commandFlag = 0;
		m_entityNumber = 0;
	}
	
	public CommandState(byte flag, int id) {
		this.m_commandFlag = flag;
		this.m_entityNumber = id;
	}
	
	public CommandState(PacketBuffer data) {		
		this.m_entityNumber = data.getInt();
		this.m_commandFlag = data.getByte();
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
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeInt(this.m_entityNumber);
		dos.write(this.m_commandFlag);
		
		if (this.isActive(CMD_FIRE)) {
			dos.writeFloat((float) this.m_turretRotationRad);
		}
		
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
	}
	
}
