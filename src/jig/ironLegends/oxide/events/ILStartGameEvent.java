package jig.ironLegends.oxide.events;

/**
 * @author Travis Hall
 */
public class ILStartGameEvent {
	public String map;
	
	public ILStartGameEvent(String map) {
		this.map = map;
	}
	
	public String getMap() {
		return this.map;
	}
}
