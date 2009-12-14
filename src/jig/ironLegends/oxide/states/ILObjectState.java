package jig.ironLegends.oxide.states;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jig.engine.util.Vector2D;

/**
 * @author Travis Hall
 */
public class ILObjectState {
	public static byte WALL = 0x01;
	public static byte BASE = 0x02;
	public static byte POWERUP = 0x03;
	public static byte CRATE = 0x04;
	
	public byte type;
	public int id;
	public int health; // wall, base, crate become inactive when health = 0
	public Vector2D position;
	
	public ILObjectState(byte type, int id, int health, Vector2D position) {
		this.type = type;
		this.id = id;
		this.health = health;
		this.position = position;
	}
	
	public static ILObjectState getWall(int id, int health) {
		return new ILObjectState(WALL, id, health, new Vector2D(0, 0));
	}
	
	public static ILObjectState getBase(int id, int health) {
		return new ILObjectState(BASE, id, health, new Vector2D(0, 0));
	}
	
	public static ILObjectState getPowerup(Vector2D pos) {
		return new ILObjectState(POWERUP, 0, 0, pos);
	}
	
	public static ILObjectState getCrate(int id, int health) {
		return new ILObjectState(CRATE, id, health, new Vector2D(0, 0));
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.write(type);
		dos.writeInt(id);
		
		if (type == WALL || type == BASE || type == CRATE) {
			dos.writeInt(health);
		} else if (type == POWERUP) {
			dos.writeFloat((float) position.getX());
			dos.writeFloat((float) position.getY());
		}
		
		dos.flush();
		dos.close();
		
		return baos.toByteArray();
	}

}
