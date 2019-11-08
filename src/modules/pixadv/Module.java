package modules.pixadv;

import java.util.HashMap;

import modules.ModuleLoader;
import modules.pixadv.objects.tiles.Air;
import modules.pixadv.objects.tiles.terra.Dirt;
import pixadv.world.types.tiles.TileObject;

public class Module implements ModuleLoader {

	@Override
	public String getID() {
		return "pixadv";
	}
	
	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public HashMap<String, Class<? extends TileObject>> loadTiles() {
		HashMap<String, Class<? extends TileObject>> tiles = new HashMap<String, Class<? extends TileObject>>();
		tiles.put(new Air(null, null).getID(), Air.class);
		// Add terrain tiles
		tiles.put(new Dirt(null, null).getID(), Dirt.class);
		// Return the completed list
		return tiles;
	}

}
