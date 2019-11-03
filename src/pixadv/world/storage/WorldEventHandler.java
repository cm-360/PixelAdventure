package pixadv.world.storage;

import pixadv.world.storage.world.World;
import pixadv.world.types.EventObject;

public class WorldEventHandler {

	private final World WORLD;
	
	// Constructor
	public WorldEventHandler(World world) {
		WORLD = world;
	}
	
	// Event methods
	public void sendToTileAt(EventObject parent, String name, String data, int x, int y, int layer) {
		WORLD.getTile(x, y, layer).dataReceived(parent, name, data);
	}
	
	public void sendToEntity(EventObject parent, String name, String data, String entityID) {
		WORLD.getEntity(entityID).dataReceived(parent, name, data);
	}

}
