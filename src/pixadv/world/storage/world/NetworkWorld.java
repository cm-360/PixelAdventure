package pixadv.world.storage.world;

import java.util.HashMap;

import pixadv.network.Client;
import pixadv.registry.Registry;

public class NetworkWorld extends World {

	private Client client;
	
	// Constructor
	private NetworkWorld(Registry registry, Client client, HashMap<String, String> info) {
		super(registry, info);
		this.client = client;
	}
	
	public static NetworkWorld connect(Registry registry, Client client, HashMap<String, String> info) {
		NetworkWorld world = new NetworkWorld(registry, client, info);
		// Request all necessary chunks
		System.out.println("Requesting chunk data...");
		for (int cx = 0; cx < world.WIDTH; cx++)
			for (int cy = 0; cy < world.HEIGHT; cy++) {
				world.CHUNKS[cx][cy] = client.requestChunk(world, cx, cy);
				System.out.printf("  Received chunk (%d, %d)!\n", cx, cy);
			}
		// TODO request entities
		
		// Return now loaded world
		return null;
	}

}
