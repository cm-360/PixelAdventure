package pixadv.world.storage.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import pixadv.registry.Registry;
import pixadv.world.storage.WorldEventHandler;
import pixadv.world.types.entities.EntityObject;
import pixadv.world.types.tiles.Chunk;
import pixadv.world.types.tiles.TileObject;

public class World {

	@SuppressWarnings("unused")
	private final Registry REG;
	
	protected final HashMap<String, String> INFO;
	protected final int WIDTH, HEIGHT;
	
	protected Chunk[][] CHUNKS;
	protected double cameraX = 0, cameraY = 16;
	protected ArrayList<String> chunkUpdates = new ArrayList<String>();
	
	protected final HashMap<String, EntityObject> ENTITIES = new HashMap<String, EntityObject>();
	
	private final WorldEventHandler EVENTS;
	
	// Constructor
	public World(Registry registry, HashMap<String, String> info) {
		REG = registry;
		INFO = info;
		// Parse info map
		String[] sizeSplit = info.get("size").split("x");
		WIDTH = Integer.parseInt(sizeSplit[0]); HEIGHT = Integer.parseInt(sizeSplit[1]);
		CHUNKS = new Chunk[WIDTH][HEIGHT];
		EVENTS = new WorldEventHandler(this);
	}
	
	// Tile access methods
	public TileObject getTile(int x, int y, int layer) {
		// Check if the chunk is loaded
		
		// Return the requested tile
		Point chunk = getChunkOf(x, y), coord = getChunkCoordOf(x, y);
		return CHUNKS[chunk.x][chunk.y].getTile(coord.x, coord.y, layer);
	}
	
	public void setTile(TileObject tile, int x, int y, int layer) {
		// Check if the chunk is loaded
		
		// Set the given tile at the requested location
		Point chunk = getChunkOf(x, y), coord = getChunkCoordOf(x, y);
		CHUNKS[chunk.x][chunk.y].setTile(tile, coord.x, coord.y, layer);
		String chunkName = String.format("%d_%d", chunk.x, chunk.y);
		if (!chunkUpdates.contains(chunkName))
			chunkUpdates.add(chunkName);
	}
	
	public Chunk getChunk(int cx, int cy) {
		return CHUNKS[cx][cy];
	}
	
	public boolean isTileLoaded(int x, int y) {
		Point chunkPos = getChunkOf(x, y);
		return isChunkLoaded(chunkPos.x, chunkPos.y);
	}
	
	public boolean isChunkLoaded(int cx, int cy) {
		return CHUNKS[cx][cy] != null;
	}
	
	public Point getChunkOf(int x, int y) {
		int xNew = x;
		if (x < 0)
			while (xNew < 0)
				xNew += getWidth();
		else if (x >= getWidth())
			while (xNew >= getWidth())
				xNew -= getWidth();
		return new Point(xNew / Chunk.getSize(), y / Chunk.getSize());
	}
	
	public Point getChunkCoordOf(int x, int y) {
		int xNew = x;
		if (x < 0)
			while (xNew < 0)
				xNew += getWidth();
		else if (x >= getWidth())
			while (xNew >= getWidth())
				xNew -= getWidth();
		return new Point(xNew % Chunk.getSize(), y % Chunk.getSize());
	}
	
	public ArrayList<String> getChunkUpdates() {
		return chunkUpdates;
	}
	
	
	
	// Entity access methods
	public ArrayList<EntityObject> getEntities() {
		ArrayList<EntityObject> entities = new ArrayList<EntityObject>();
		for (EntityObject entity : ENTITIES.values())
			entities.add(entity);
		return entities;
	}
	
	public EntityObject getEntity(String entityID) {
		return ENTITIES.get(entityID);
	}
	
	// Other access methods
	public Registry getRegistry() {
		return REG;
	}
	
	public WorldEventHandler getEvents() {
		return EVENTS;
	}
	
	public double getCameraX() {
		return cameraX;
	}
	
	public double getCameraY() {
		return cameraY;
	}
	
	public void setCameraX(double x) {
		cameraX = x;
	}
	
	public void setCameraY(double y) {
		cameraY = y;
	}
	
	// Info methods
	public String getInfo(String infoKey) {
		return INFO.get(infoKey);
	}
	
	public int getWidth() {
		return WIDTH * Chunk.getSize();
	}
	
	public int getHeight() {
		return HEIGHT * Chunk.getSize();
	}

}
