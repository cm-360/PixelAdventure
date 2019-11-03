package modules;

import java.util.HashMap;

import pixadv.world.types.tiles.TileObject;

public interface ModuleLoader {

	public String getID();
	
	public String getVersion();
	
	public HashMap<String, Class<? extends TileObject>> loadTiles();

}
