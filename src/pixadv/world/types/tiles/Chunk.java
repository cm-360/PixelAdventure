package pixadv.world.types.tiles;

import java.io.File;

import lib.io.file.TextUtil;
import modules.pixadv.objects.tiles.Air;
import pixadv.registry.Registry;
import pixadv.world.storage.world.World;

public class Chunk {

	private final Registry REG;
	
	private static int chunkSize = 20;
	
	private final TileObject[][][] TILES = new TileObject[chunkSize][chunkSize][3];
	
	// Constructors
	public Chunk(Registry registry) {;
		REG = registry;
		for (int x = 0; x < chunkSize; x++)
			for (int y = 0; y < chunkSize; y++)
				for (int l = 0; l < 3; l++)
					setTile(new Air(REG, ""), x, y, l);
	}
	
	// IO methods
	public static Chunk load(Registry registry, String chunkData, World parent) {
		Chunk chunk = new Chunk(registry);
		// Parse each line of chunk data
		for (String line : chunkData.split("[\\n\\r]+")) {
			try {
				// Parse line into position and tile info
				String[] lineSplit = line.split(":", 3);
				String[] pos = lineSplit[0].split("_", 3);
				chunk.setTile(registry.createTile(lineSplit[1], lineSplit[2], parent),
						Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
			} catch (Exception e) {
				// Improperly formatted line
				System.out.println("  Corrupt entry found while loading chunk!");
				System.out.println("    " + line);
				System.out.printf("    %s: %s\n", e.getClass().getName(), e.getMessage());
			}
		}
		return chunk;
	}
	
	public void saveTo(File chunkFile) {
		StringBuilder output = new StringBuilder();
		for (int x = 0; x < chunkSize; x++)
			for (int y = 0; y < chunkSize; y++)
				for (int l = 0; l < 3; l++) {
					output.append(String.format("%s_%s_%s:%s\n", x, y, l, getTile(x, y, l)));
				}
		TextUtil.write(chunkFile, output.toString());
	}
	
	// Access methods
	public TileObject getTile(int x, int y, int layer) {
		return TILES[x][y][layer];
	}
	
	public void setTile(TileObject tile, int x, int y, int layer) {
		TILES[x][y][layer] = tile;
	}
	
	// Info methods
	public static int getSize() {
		return chunkSize;
	}

}
