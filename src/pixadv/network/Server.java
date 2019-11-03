package pixadv.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lib.io.file.FileUtil;
import lib.io.file.TextUtil;
import pixadv.world.storage.universe.Universe;
import pixadv.world.storage.world.World;

public class Server extends Thread {

	private Socket socket;
	private HashMap<String, String> requests = new HashMap<String, String>();
	
	private Universe universe;
	private File universeDir;
	
	// Constructors
	private Server(Socket socket, Universe universe, File universeDir) {
		super("PixAdv-Server-" + socket.getInetAddress().getHostAddress());
		this.socket = socket;
		this.universe = universe;
		this.universeDir = universeDir;
	}
	
	@SuppressWarnings("unchecked")
	public static Server connect(Socket clientSocket, Universe localUniverse, File localUniverseDir) {
		Server server = new Server(clientSocket, localUniverse, localUniverseDir);
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(server.socket.getInputStream()));
			PrintWriter socketOut = new PrintWriter(server.socket.getOutputStream());
			// TODO Authenticate client
			System.out.printf("** Connection from '%s' **\n", server.socket.getInetAddress().getHostAddress());
			String[] tokenSplit = socketIn.readLine().split(":", 2);
			if (tokenSplit[1].equals("abcde")) {
				System.out.println("Client authenticated!");
				socketOut.println("allow");
				socketOut.flush();
				// Compare client registry to internal
				System.out.println("Awaiting client registry...");
				String clientRegistry = socketIn.readLine();
				System.out.printf("  Response: '%s'\n", clientRegistry);
				try {
					// Parse client registry
					HashMap<String, String> registryData = new Gson().fromJson(clientRegistry, HashMap.class);
					String[] clientModules = registryData.getOrDefault("modules", "").split(",");
					if (clientModules.length > 0) {
						for (String moduleInfo : clientModules) {
							if (server.universe.getRegistry().listModules().contains(moduleInfo))
								; // TODO compare client registry to that of local universe
						}
						// Tell client registry was accepted
						System.out.println("  Client registry accepted!");
						socketOut.printf("allow,%s\n", server.universe.getRegistry().toString());
						socketOut.flush();
						// Check the last world the player was in and send it
						File playerList = new File(server.universeDir + "/worlds/players.txt");
						String worldName = "";
						for (String line : TextUtil.read(playerList).split("[\\n\\r]+")) {
							String[] lineSplit = line.split(":", 2);
							if (lineSplit[0].equals(tokenSplit[0]))
								worldName = lineSplit[1];
						}
						if (worldName == null) {
							// TODO Player has not connected before
							
						} else {
							// Send over relevant world data
							System.out.println("Sending world data...");
							World world = server.universe.getWorld(worldName);
							socketOut.printf("%s:%s:%s\n", worldName, world.getInfo("size"), world.getInfo("type"));
							socketOut.flush();
						}
					} else {
						System.out.println("  Denied: Client registry is empty!");
						// Deny the client and close the socket
						socketOut.printf("denied,Registry is empty!,%s\n", server.universe.getRegistry().toString());
						socketOut.flush();
						server.socket.close();
						return null;
					}
				} catch (JsonSyntaxException e) {
					System.out.println("  Denied: Client registry is corrupted!");
					System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
					// Deny the client and close the socket
					socketOut.printf("denied,Registry is corrupted!", server.universe.getRegistry().toString());
					socketOut.flush();
					server.socket.close();
					return null;
				}
			} else {
				System.out.println("Denied: Client token is not valid!");
				// Deny the client and close the socket
				socketOut.println("denied");
				socketOut.flush();
			}
		} catch (Exception e) {
			System.out.printf("Connection from '%s' failed!\n", server.socket.getInetAddress().getHostAddress());
			System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
			return null;
		}
		// Return now connected instance
		return server;
	}
	
	// Thread method
	@Override
	public void run() {
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
			// Main server loop
			while (!socket.isClosed()) {
				try {
					// Receive and process all requests
					String header;
					while (true) {
						header = socketIn.readLine();
						if (header.equals("~requests")) {
							break;
						} else {
							// Get all lines of request
							String line, request = "";
							while (true) {
								line = socketIn.readLine();
								if (line.equals("~"))
									break;
								else
									request += line + "\n";
							}
							// Process request
							if (header.equals("disconnect")) {
								socketOut.println("~disconnected");
								socketOut.flush();
								socket.close();
								break;
							} else if (header.matches("getChunk\\(.+,[\\d-]+,[\\d-]+\\)")) {
								// Chunk request
								Matcher matcher = Pattern.compile("(?<=\\().+,[\\d-]+,[\\d-]+(?=\\))").matcher(header);
								if (matcher.find()) {
									String[] matchSplit = matcher.group().split(",", 3);
									// Ensure chunk file is saved so it can be read and sent
									File chunkFile = new File(String.format("%s/worlds/%s/chunks/%s_%s.pachunk",
											universeDir, matchSplit[0], matchSplit[1], matchSplit[2]));
									universe.getWorld(matchSplit[0])
											.getChunk(Integer.parseInt(matchSplit[1]), Integer.parseInt(matchSplit[2]))
											.saveTo(chunkFile);
									// Read and sent contents of chunk file
									socketOut.println(TextUtil.read(chunkFile));
								}
							} else {
								// Request header is not recognized
								socketOut.println("?");
							}
							socketOut.println("~response");
							socketOut.flush();
						}
					}
					// Send all waiting data to the client
					if (!socket.isClosed())
						synchronized (requests) {
							for (String request : requests.keySet()) {
								if (requests.get(request) == null) {
									// Send request to client
									socketOut.println(request + "\n~");
									socketOut.flush();
									// Wait for response
									String line, response = "";
									while (true) {
										line = socketIn.readLine();
										if (line.equals("~response"))
											break;
										else
											response += line + "\n";
									}
									requests.put(request, response);
								}
							}
							requests.notifyAll();
						}
				} catch (Exception e) {
					e.printStackTrace();
					socket.close();
					break;
				}
			}
			System.out.printf("Connection from '%s' closed.\n", socket.getInetAddress().getHostAddress());
		} catch (IOException e) {
			System.out.printf("Connection from '%s' failed!\n", socket.getInetAddress().getHostAddress());
			System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
		}
	}

}
