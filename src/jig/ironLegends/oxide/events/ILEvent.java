package jig.ironLegends.oxide.events;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jig.engine.util.Vector2D;

/**
 * @author Travis Hall
 */
public class ILEvent {

	public static byte BULLET = 0x01;
	public static byte MINE = 0x02;
	public static byte EXPLOSION = 0x03;
	
	public byte type;
	public float direction;
	public byte team;
	public Vector2D position;
	
	private ILEvent(byte type, Vector2D position, byte team, float direction) {
		this.type = type;
		this.position = position;
		this.team = team;
		this.direction = direction;
	}
	
	public static ILEvent getBullet(Vector2D position, byte team, float direction) {
		return new ILEvent(BULLET, position, team, direction);
	}
	
	public static ILEvent getMine(Vector2D position, byte team) {
		return new ILEvent(MINE, position, team, 0.0f);
	}
	
	public static ILEvent getExplosion(Vector2D position) {
		return new ILEvent(EXPLOSION, position, (byte) 0, 0.0f);
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.write(this.type);
		dos.writeFloat((float) this.position.getX());
		dos.writeFloat((float) this.position.getY());
		
		if (type == BULLET || type == MINE) {
			dos.write(this.team);
		}
		
		if (type == BULLET) {
			dos.writeFloat(this.direction);
		}
		
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
	}
}
