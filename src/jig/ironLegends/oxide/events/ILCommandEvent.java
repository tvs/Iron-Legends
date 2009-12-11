package jig.ironLegends.oxide.events;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jig.ironLegends.oxide.util.PacketBuffer;

/**
 * Command event Ð based on the KeyCommands class.
 * Corresponds to a single event.
 * To be sent from the client to the server
 * @author Travis Hall
 */
public class ILCommandEvent {
	public static String LEFT = "left\0";
	public static String RIGHT = "right\0";
	public static String UP = "up\0";
	public static String DOWN = "down\0";
	public static String FIX_TURRET = "fixturret\0";
	public static String FIRE = "fire\0";
	
	public int playerID;
	public int numberOfEvents;
	public boolean down;
	public boolean up;
	public boolean left;
	public boolean right;
	public boolean fire;
	public float fireDirection;
	public boolean fixTurret;
	
	
	public ILCommandEvent(int playerID) {
		this.playerID = playerID;
		this.down = false;
		this.up = false;
		this.left = false;
		this.right = false;
		this.fire = false;
		this.fireDirection = 0;
		this.fixTurret = false;
		
		this.numberOfEvents = 0;
	}
	
	/**
	 * @param contentData
	 */
	public ILCommandEvent(PacketBuffer contentData) {
		this.playerID = contentData.getByte();
		this.numberOfEvents = contentData.getInt();
		
		for (int i = 0; i < numberOfEvents; i++) {
			String s = contentData.getString();
			
			if (s == LEFT) this.left = true;
			else if (s == RIGHT) this.right = true;
			else if (s == UP) this.up = true;
			else if (s == DOWN) this.down = true;
			else if (s == FIX_TURRET) this.fixTurret = true;
			else if (s == FIRE) { 
				this.fire = true;
				this.fireDirection = contentData.getFloat();
			}	
		}
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeByte(playerID);
		dos.writeInt(numberOfEvents);
		
		if (this.left) dos.writeBytes(LEFT);
		if (this.right) dos.writeBytes(RIGHT);
		if (this.up) dos.writeBytes(UP);
		if (this.down) dos.writeBytes(DOWN);
		if (this.fixTurret) dos.writeBytes(FIX_TURRET);
		if (this.fire) {
			dos.writeBytes(FIRE);
			dos.writeFloat(this.fireDirection);
		}
		dos.flush();
		dos.close();

		return baos.toByteArray();
	}
	
	public void turnLeft() {
		this.numberOfEvents++;
		this.left = true;
	}
	
	public void turnRight() {
		this.numberOfEvents++;
		this.right = true;
	}
	
	public void moveForward() {
		this.numberOfEvents++;
		this.up = true;
	}
	
	public void moveBackwards() {
		this.numberOfEvents++;
		this.down = true;
	}
	
	public void fixTurret() {
		this.numberOfEvents++;
		this.fixTurret = true;
	}
	
	public void fire(float direction) {
		this.numberOfEvents++;
		this.fire = true;
		this.fireDirection = direction;
	}

}
