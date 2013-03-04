package org.bestever.bebot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Server implements Serializable {

	////////////
	// Fields //
	////////////
	
	/**
	 * If the server was created successfully and/or is running, this will be true
	 */
	public boolean active_server;
	
	/**
	 * Contains the port it is run on
	 */
	public short port;
	
	/**
	 * The time the server was started
	 */
	public long time_started;
	
	/**
	 * This is the generated password at the very start for server logs and the password
	 */
	public String server_id;
	
	/**
	 * Username of the person who sent the command to start it
	 */
	public String sender;
	
	/**
	 * The channel it was hosted from
	 */
	public String channel;
	
	/**
	 * Contains the hostname used, this will NOT contain " :: [BE] New York "
	 */
	public String hostname;
	
	/**
	 * This is the login name used
	 */
	public String login;
	
	/**
	 * Contains the entire ".host" command
	 */
	public String host_command;

	/**
	 * This is the iwad used
	 */
	public String iwad;

	/**
	 * Contains the gamemode
	 */
	public String gamemode;

	/**
	 * The name of the config file (like rofl.cfg), will contain ".cfg" on the end of the string
	 */
	public String config;

	/**
	 * This is for additional instructions the user may specify for one to two things to 
	 * circumvent having to make and upload a whole new config
	 */
	public String additional_instructions;
	
	/**
	 * This contains the actual instructions that the server will run at the very end
	 * Ex: "./zandronum-server -file holycrapbatman_no.wad +duel 1 +customcommandhere"
	 */
	public String server_parameters;

	/**
	 * If this is true, it means the wad will be parsed for maps to add to the maplist
	 */
	public boolean mapwad;

	/**
	 * If this is true, that means skulltag data will be enabled
	 */
	public boolean disable_skulltag_data;

	/**
	 * Contains flags for the server
	 */
	public int dmflags;
	
	/**
	 * Contains flags for the server
	 */
	public int dmflags2;
	
	/**
	 * Contains flags for the server
	 */
	public int dmflags3;
	
	/**
	 * Contains flags for the server
	 */
	public int compatflags;
	
	/**
	 * Contains flags for the server
	 */
	public int compatflags2;
	
	///////////////
	// Constants //
	///////////////
	
	/**
	 *  Random generated ID for serialization
	 */
	private static final long serialVersionUID = -2392019434571282715L;
	
	/**
	 * If there's an error with processing of numbers, return this
	 */
	public static final int FLAGS_ERROR = 0xFFFFFFFF;
	
	/////////////////////////////
	// Method and Constructors //
	/////////////////////////////
	
	/**
	 * Default constructor should be an inactive server
	 */
	public Server() {
		this.active_server = false;
	}
	
	/**
	 * This will take ".host ...", parse it and pass it off safely to anything else
	 * that needs the information to create/run the servers and the mysql database
	 * @param The arraylits of servers for us to add on a server if successful
	 * @param channel The channel it was sent from
	 * @param sender The sender
	 * @param login The login of the sender
	 * @param hostname The hostname of the sender
	 * @param message The message sent
	 * @return Null if all went well, otherwise an error message to print to the bot
	 */
	public static String handleHostCommand(ArrayList<Server> servers, String channel, String sender, String login, String hostname, String message) {
		// Initialize server without linking it to the arraylist
		Server server = new Server();
		
		// Input basic values
		server.channel = channel;
		server.login = login;
		server.hostname = hostname;
		server.sender = sender;
		server.host_command = message;
		
		// Break up the message, if we have 1 or less keywords then something is wrong 
		String[] keywords = message.split(" ");
		if (keywords.length < 2)
			return "Not enough parameters";
		
		// Iterate through every single keyword to construct the host thing except the first index since that's just ".host"
		// For sanity's sake, please keep the keywords in *alphabetical* order
		for (int i = 1; i < keywords.length; i++) {
			
			// compatflags
			if (keywords[i].toLowerCase().startsWith("compatflags=")) {
				server.compatflags = handleGameFlags(keywords[i]);
				if (server.compatflags == FLAGS_ERROR)
					return "Problem with parsing compatflags";
				continue;
			}
			
			// compatflags2
			if (keywords[i].toLowerCase().startsWith("compatflags2=")) {
				server.compatflags2 = handleGameFlags(keywords[i]);
				if (server.compatflags2 == FLAGS_ERROR)
					return "Problem with parsing compatflags2";
				continue;
			}
			
			// config
			if (keywords[i].toLowerCase().startsWith("config=")) {
				server.config = handleConfig(keywords[i]);
			}
			
			// data
			if (keywords[i].toLowerCase().startsWith("data=")) {
				server.disable_skulltag_data = handleSkulltagData(keywords[i]);
			}
			
			// dmflags
			if (keywords[i].toLowerCase().startsWith("dmflags=")) {
				server.dmflags = handleGameFlags(keywords[i]);
				if (server.dmflags == FLAGS_ERROR)
					return "Problem with parsing dmflags";
				continue;
			}
			
			// dmflags2
			if (keywords[i].toLowerCase().startsWith("dmflags2=")) {
				server.dmflags2 = handleGameFlags(keywords[i]);
				if (server.dmflags2 == FLAGS_ERROR)
					return "Problem with parsing dmflags2";
				continue;
			}
			
			// dmflags3 
			if (keywords[i].toLowerCase().startsWith("dmflags3=")) {
				server.dmflags3 = handleGameFlags(keywords[i]);
				if (server.dmflags3 == FLAGS_ERROR)
					return "Problem with parsing dmflags3";
				continue;
			}
			
			// gamemode
			
			// hostname
			if (keywords[i].toLowerCase().startsWith("hostname=")) {
				server.hostname = getHostname(keywords[i]);
			}
			
			// iwad
			
			// mapwad
			
			// wad
		}

		// Handle
		
		// If all went well, return null
		return null;
	}
	
	// ** TO BE DONE **
	private static String handleConfig(String string) {
		return null;
	}

	/**
	 * This handles the skulltag data boolean
	 * @param string The keyword to check
	 * @return True if to use it, false if not
	 */
	private static boolean handleSkulltagData(String string) {
		// Split the string
		String[] value = string.split("=");
		
		// If we don't have exactly 2 values, or the 2nd value is unusual, default to on
		if (value.length != 2 || value[1] == "" || value[1] == null)
			return true;
		
		// If the second keyword matches some known keywords, then disable it
		switch (value[1].toLowerCase()) {
			case "off":
			case "false":
			case "no":
			case "disable":
			case "remove":
				return false;
		}
		
		// Otherwise if something is wrong, just assume we need it
		return true;
	}

	/**
	 * This handles dmflags/compatflags, returns 0xFFFFFFFF if there's an error (FLAGS_ERROR)
	 * @param string The keyword to check
	 * @return A number of what it is
	 */
	private static int handleGameFlags(String keyword) {
		// Split it by the equals sign
		String[] data = keyword.split("=");
		
		// There should only be two parts, the left and the right side
		if (data.length != 2)
			return FLAGS_ERROR;
		
		// If the right side is numeric and passes some logic checks, return that as the flag
		int flag = 0;
		if (Functions.isNumeric(data[1]))
			flag = Integer.parseInt(data[1]);
		if (flag >= 0)
			return flag;
		
		// If something went wrong, return an error
		return FLAGS_ERROR;
	}

	
	public static String getHostname(String keyword) {
		return null;
	}
	
	/**
	 * This method serializes a given server to a folder specified (probably in the .ini) so
	 * that servers can be re-initialized at some point in the future
	 * @param server The server object to serialize
	 * @param folderPath The path to the folder where Server objects are writen/read from
	 * @return True if successful in writing the object, false if not
	 */
	public static boolean serializeServer(Server server, String folderPath, String extension) {
		
		// Make sure the server and folderPath are valid
		if (server == null)
			return false;
		
		// Set our file up
		File objectFile = new File(folderPath + Integer.toString(server.port) + extension);
		
		// If the file doesnt exist, create it
		if (!objectFile.exists()) {
			try {
				objectFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		// If we can't write to it, or something is wrong, return false
		if (!objectFile.canRead() || !objectFile.canWrite())
			return false;
	
		// Prepare object output stream for writing
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(objectFile.getPath())));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// Since our objectstream should be functional, write the server
		try {
			oos.writeObject(server);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// Close
		try {
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// Verify if the final exists
		return true;
	}
}