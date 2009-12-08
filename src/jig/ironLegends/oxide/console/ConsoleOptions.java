package jig.ironLegends.oxide.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jig.engine.Console;
import jig.engine.ConsoleCommandHandler;

/**
 * @author Travis Hall
 */
public class ConsoleOptions {
	
	private Map<String, String> serverOptions;
	private Map<String, String> clientOptions;
	
	public List<String> validStrings;
	
	public ConsoleOptions() {
		this.serverOptions = new HashMap<String, String>();
		this.clientOptions = new HashMap<String, String>();
		
		this.populateStrings();
		this.populateServerOptions();
		this.populateClientOptions();
	}
	
	/**
	 * Get the entire map of server options
	 * @return Map of server option to argument
	 */
	public Map<String, String> getServerOptions() {
		return this.serverOptions;
	}
	
	/**
	 * Get the entire map of client options
	 * @return Map of client option to argument
	 */
	public Map<String, String> getClientOptions() {
		return this.clientOptions;
	}
	
	/**
	 * Get an individual server option's arguments
	 * @param name Name of the server option
	 * @return The option's argument in the form of a string
	 */
	public String getServerOption(String name) {
		return this.serverOptions.get(name);
	}
	
	/**
	 * Get an individual client option's arguments
	 * @param name Name of the client option
	 * @return The option's argument in the form of a string
	 */
	public String getClientOption(String name) {
		return this.clientOptions.get(name);
	}
	
	/**
	 * Sets a server's option. If the server option did not already exist,
	 * it is created
	 * @param name Name of the server option
	 * @param arg The argument associated with the server option
	 */
	public void setServerOption(String name, String arg) {
		this.serverOptions.put(name, arg);
	}
	
	/**
	 * Sets a client's option. If the client option did not already exist,
	 * it is created
	 * @param name Name of the client option
	 * @param arg The argument associated with the client option
	 */
	public void setClientOption(String name, String arg) {
		this.clientOptions.put(name, arg);
	}
	
	private void populateStrings() {
		validStrings = new ArrayList<String>();
		validStrings.add("sv_tickrate");
		validStrings.add("cl_updaterate");
		validStrings.add("cl_commandrate");
		validStrings.add("cl_interpolate");
		validStrings.add("cl_predict");
	}
	
	/**
	 * Default server options
	 */
	private void populateServerOptions() {
		this.serverOptions.put("sv_tickrate", "33");
	}
	
	/**
	 * Default client options
	 */
	private void populateClientOptions() {
		this.clientOptions.put("cl_updaterate", "20");
		this.clientOptions.put("cl_commandrate", "30");
		this.clientOptions.put("cl_interpolate", "1");
		this.clientOptions.put("cl_predict", "1");
	}
	
	public static void addCommandHandlers(Console console, final ConsoleCommandHandler handler) {
		console.addCommandHandler("sv_tickrate", handler, "sv_tickrate <number> : Server tickrate (ticks / second). Default: 33");
		console.addCommandHandler("cl_updaterate", handler, "cl_updaterate <number> : Number of updates to receive per second. Default: 20");
		console.addCommandHandler("cl_commandrate", handler, "cl_commandrate <number> : Number of command updates to send per second. Default: 30");
		console.addCommandHandler("cl_interpolate", handler, "cl_interpolate <0-2> : 0 - off, 1 - dead reckoning, 2 - interpolation");
		console.addCommandHandler("cl_predict", handler, "cl_predict <flag> : 0 - off, 1 - on");
	}

}
