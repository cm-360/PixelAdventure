package pixadv.world.newton;

import java.util.HashMap;

import com.google.gson.Gson;

import pixadv.registry.Registry;
import pixadv.world.storage.universe.Universe;
import pixadv.world.storage.world.World;
import pixadv.world.types.entities.EntityObject;
import pixadv.world.types.tiles.TileObject;

public class Newton {

	private final Universe UNIVERSE;
	
	private long lastRun = -1;
	private final long start = System.currentTimeMillis();
	
	private HashMap<EntityObject, String> manualChanges = new HashMap<EntityObject, String>();
	
	// Constructor
	public Newton(Registry registry, Universe universe) {
		UNIVERSE = universe;
	}
	
	// Main physics method
	@SuppressWarnings("unchecked")
	public void recalculate() {
		long current = System.currentTimeMillis();
		// 2-second delay for debugging
		if (current - start < 2000)
			return;
		// Return if this is the first run
		if (lastRun != -1) {
			long increase = current - lastRun;
			if (increase < 5)
				return;
			World world = UNIVERSE.currentWorld();
			double gravity = 20;
			//double friction = 1.0; // Placeholder, will use friction values from individual tiles later
			// Calculate entity movement
			for (EntityObject entity : world.getEntities()) {
				// Manual velocity changes
				if (manualChanges.containsKey(entity)) {
					entity.dataReceived(null, "movement", manualChanges.get(entity));
					manualChanges.remove(entity);
				}
				// Physics changes
				HashMap<String, String> data = new Gson().fromJson(entity.getData(), HashMap.class);
				// Entity position and velocity
				double x = Double.parseDouble(data.getOrDefault("x", "0"));
				double y = Double.parseDouble(data.getOrDefault("y", "0"));
				double xVel = Double.parseDouble(data.getOrDefault("xVel", "0"));
				double yVel = Double.parseDouble(data.getOrDefault("yVel", "0"));
				// Entity boundaries for collisions
				double rWidth = Double.parseDouble(data.getOrDefault("width", "1")) / 2;
				double rHeight = Double.parseDouble(data.getOrDefault("height", "1")) / 2;
				double tolerance = 0.01;
				int xMinCol = (int) Math.round(x - rWidth + tolerance), xMaxCol = (int) Math.round(x + rWidth - tolerance);
				int yMinCol = (int) Math.round(y - rHeight + tolerance), yMaxCol = (int) Math.round(y + rHeight - tolerance);
				for (int xCol = xMinCol; xCol < xMaxCol; xCol++)
					for (int yCol = yMinCol; yCol < yMinCol; yCol++) {
						TileObject tile = world.getTile(xCol, yCol, 1);
						if (!tile.getID().equals("air")) {

						}
					}
				// Change entity position
				x += xVel * (increase / 1000.0);
				y += yVel * (increase / 1000.0);
				// Save new values to entity
				data.put("x", Double.toString(x));
				data.put("y", Double.toString(y));
				data.put("xVel", Double.toString(xVel));
				data.put("yVel", Double.toString(yVel));
				entity.dataReceived(null, "movement", new Gson().toJson(data));
			}
		}
		lastRun = current;
	}
	
	// Hook method
	public void manualChange(EntityObject entity, String data) {
		manualChanges.put(entity, data);
	}

}
