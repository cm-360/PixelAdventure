package pixadv.world.storage.universe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import pixadv.network.Client;
import pixadv.registry.Registry;
import pixadv.world.storage.world.NetworkWorld;

public class NetworkUniverse extends Universe {

	private Client client;
	
	// Constructors
	private NetworkUniverse(Registry registry) {
		super(registry);
	}
	
	public static NetworkUniverse connect(Registry registry, String ip, String token) {
		System.out.println("** Universe loading **");
		System.out.printf("Connecting to universe at '%s'...\n", ip);
		String response;
		try {
			// Connect to remote server
			String[] ipSplit = ip.split(":(?=\\d+$)", 2);
			Socket socket = new Socket(ipSplit[0], Integer.parseInt(ipSplit[1]));
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
			// Authenticate with player ID and key
			socketOut.println(token);
			socketOut.flush();
			response = socketIn.readLine();
			if (response.equals("allow")) {
				System.out.println("Client authenticated!");
				// Send local registry and wait for response
				System.out.println("Sending local registry and awaiting server response...");
				socketOut.println(registry.toString());
				socketOut.flush();
				response = socketIn.readLine();
				// Check if registry was accepted
				if (response.startsWith("allow")) {
					String[] responseSplit = response.split(",", 2);
					System.out.println("  Server accepted local registry!");
					System.out.printf("  Remote registry: '%s'\n", responseSplit[1]);
					NetworkUniverse universe = new NetworkUniverse(registry);
					universe.playerID = token.split(":", 2)[0];
					// Create a client object to handle world loading
					universe.client = new Client(socket, universe);
					// Download the remote world
					response = socketIn.readLine();
					String[] worldData = response.split(":", 3);
					System.out.printf("  Name: '%s'\n", worldData[0]);
					System.out.println("  Size: " + worldData[1]);
					System.out.printf("  Type: '%s'\n", worldData[2]);
					// Create a world object
					HashMap<String, String> worldDataMap = new HashMap<String, String>();
					worldDataMap.put("name", worldData[0]);
					worldDataMap.put("size", worldData[1]);
					worldDataMap.put("type", worldData[2]);
					universe.client.start();
					NetworkWorld world = NetworkWorld.connect(registry, universe.client, worldDataMap);
					// Return the now connected universe
					universe.currentWorld = worldData[0];
					universe.WORLDS.put(worldData[0], world);
					return universe;
				} else {
					// Client registry was denied
					String[] responseSplit = response.split(",", 3);
					System.out.println("  Server denied local registry!");
					System.out.printf("  Reason: '%s'", responseSplit[1]);
					System.out.printf("  Remote registry: '%s'", responseSplit[2]);
					socket.close();
					return null;
				}
			} else {
				// Client token was denied
				System.out.println("Client login token was denied!");
				socket.close();
				return null;
			}
		} catch (Exception e) {
			System.out.printf("Connection to '%s' failed!\n", ip);
			System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
			return null;
		}
	}
	
	// Closing method
	public void disconnect() {
		client.disconnect();
	}

}
