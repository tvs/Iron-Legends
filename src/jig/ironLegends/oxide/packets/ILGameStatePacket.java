package jig.ironLegends.oxide.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jig.engine.util.Vector2D;
import jig.ironLegends.EntityState;
import jig.ironLegends.oxide.events.ILEvent;
import jig.ironLegends.oxide.states.ILObjectState;
import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * @author Travis Hall
 */
public class ILGameStatePacket extends ILPacket {
	
	private List<EntityState> tankStates;
	private List<ILObjectState> objectStates;
	private List<ILEvent> events;
	
	/**
	 * @param protocolData
	 */
	public ILGameStatePacket(byte[] protocolData) {
		super(ILPacket.IL_GAME_DATA_HEADER, protocolData);
		this.tankStates = new LinkedList<EntityState>();
		this.objectStates = new LinkedList<ILObjectState>();
		this.events = new LinkedList<ILEvent>();
	}
	
	public ILGameStatePacket(byte[] protocolData, byte[] contentData) {
		this(protocolData);
		this.contentData = new PacketBuffer(contentData);
		
		// Tank data
		int numTanks = this.contentData.getInt();
		for (int i = 0; i < numTanks; i++) {
			EntityState e = new EntityState();
			e.m_entityNumber = this.contentData.getInt();
			e.m_pos = new Vector2D(this.contentData.getFloat(), this.contentData.getFloat());
			e.m_health = this.contentData.getInt();
			e.m_tankRotationRad = this.contentData.getFloat();
			e.m_flags = this.contentData.getInt();
			e.m_turretRotationRad = this.contentData.getFloat();
			this.tankStates.add(e);
		}
		
		// Object data
		int numObjects = this.contentData.getInt();
		for (int i = 0; i < numObjects; i++) {
			byte type = this.contentData.getByte();
			int id = this.contentData.getInt();
			
			if (type == ILObjectState.WALL || type == ILObjectState.BASE || type == ILObjectState.CRATE) {
				this.objectStates.add(new ILObjectState(type, id, this.contentData.getInt(), new Vector2D(0, 0)));
			} else if (type == ILObjectState.POWERUP) {
				Vector2D v = new Vector2D(this.contentData.getFloat(), this.contentData.getFloat());
				this.objectStates.add(new ILObjectState(type, id, 0, v));
			}
		}
		
		// Event data
		int numEvents = this.contentData.getInt();
		for (int i = 0; i < numEvents; i++) {
			byte type = this.contentData.getByte();
			Vector2D v = new Vector2D(this.contentData.getFloat(), this.contentData.getFloat());
			
			if (type == ILEvent.BULLET) {
				byte team = this.contentData.getByte();
				float dir = this.contentData.getFloat();
				
				this.events.add(ILEvent.getBullet(v, team, dir));
			}
			
			if (type == ILEvent.MINE) {
				byte team = this.contentData.getByte();
				this.events.add(ILEvent.getMine(v, team));
			}
		}
		
		this.contentData.rewind();
	}
	
	public void addTankState(EntityState state) {
		this.tankStates.add(state);
	}
	
	public void addObjectState(ILObjectState state) {
		this.objectStates.add(state);
	}
	
	public void addEvent(ILEvent event) {
		this.events.add(event);
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		// Write relevant tank information
		dos.writeInt(this.tankStates.size());
		for (EntityState e : this.tankStates) {
			dos.writeInt(e.m_entityNumber);
			dos.writeFloat((float) e.m_pos.getX());
			dos.writeFloat((float) e.m_pos.getY());
			dos.writeInt(e.m_health);
			dos.writeFloat((float) e.m_tankRotationRad);
			dos.writeInt(e.m_flags);
			dos.writeFloat((float) e.m_turretRotationRad);
		}
		
		// Write relevant object information
		dos.writeInt(this.objectStates.size());
		for (ILObjectState o : this.objectStates) {
			dos.write(o.getBytes());
		}
		
		// Write relevant event information
		dos.writeInt(this.events.size());
		for (ILEvent e : this.events) {
			dos.write(e.getBytes());
		}
		
		dos.flush();
		dos.close();
		
		this.contentData = new PacketBuffer(baos.toByteArray());
		
		return super.getBytes();
	}
	
}
