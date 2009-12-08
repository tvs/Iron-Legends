package jig.ironLegends.oxide.console;

import jig.engine.ConsoleCommandHandler;


/**
 * @author Travis Hall
 */
public class ILConsoleCommandHandler implements ConsoleCommandHandler {
	private ConsoleOptions options;
	
	
	public ILConsoleCommandHandler(ConsoleOptions options) {
		this.options = options;
	}


	/* (non-Javadoc)
	 * @see jig.engine.ConsoleCommandHandler#handle(java.lang.String, java.lang.String)
	 */
	public boolean handle(String cmd, String rest) {
		if (options.validStrings.contains(cmd)) {
			// Only takes one argument separated by a space
			String[] arg = rest.split(" ");
			if (arg[0].length() > 1) 
				options.setClientOption(cmd, arg[0]);
			return true;
		}
		return false;
	}
	
}
