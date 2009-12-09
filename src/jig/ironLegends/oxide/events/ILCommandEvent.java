package jig.ironLegends.oxide.events;

/**
 * Command event Ð based on the KeyCommands class.
 * Corresponds to a single event.
 * @author Travis Hall
 */
public class ILCommandEvent {
	public String command;
	
	public ILCommandEvent(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return this.command;
	}

}
