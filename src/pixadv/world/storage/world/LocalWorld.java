package pixadv.world.storage.world;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lib.io.file.FileUtil;
import lib.io.file.TextUtil;
import modules.pixadv.objects.entities.Player;
import pixadv.registry.Registry;
import pixadv.world.types.tiles.Chunk;

public class LocalWorld extends World {

	private File worldDir;
	
	// Constructors
	private LocalWorld(Registry registry, File worldDir, HashMap<String, String> info) {
		super(registry, info);
		this.worldDir = worldDir;
	}
	
	public static LocalWorld create(Registry registry, File worldDir, String generator, HashMap<String, String> info) {
		LocalWorld world = new LocalWorld(registry, worldDir, info);
		world.worldDir = worldDir;
		// Create empty chunks for loading world
		for (int w = 0; w < world.WIDTH; w++)
			for (int h = 0; h < world.HEIGHT; h++)
				world.CHUNKS[w][h] = new Chunk(registry);
		// TODO generate world
		System.out.printf("Creating %s world with '%s' generator...\n", info.get("size"), generator);
		// Return newly generated world
		return world;
	}
	
	// IO methods
	@SuppressWarnings("unchecked")
	public static LocalWorld load(Registry registry, File worldDir) {
		try {
			long start = System.currentTimeMillis();
			// Load world info
			HashMap<String, String> info = new Gson().fromJson(TextUtil.read(new File(worldDir + "/worldinfo.json")),
					HashMap.class);
			String[] size = info.getOrDefault("size", "1x1").split("x", 2);
			LocalWorld world = new LocalWorld(registry, worldDir, info);
			System.out.printf("Loading %sx%s world from '%s'...\n", size[0], size[1], worldDir);
			// Find all chunk files
			ArrayList<File> chunkFiles = FileUtil.listFiles(
					new File(worldDir + "/chunks"),
					FileUtil.createExtensionFilter("pachunk"), false);
			for (File chunkFile : chunkFiles) {
				// Only load properly named files
				String chunkName = chunkFile.getName().replaceAll("\\.pachunk$", "");
				if (chunkName.matches("\\d+_\\d+")) {
					String[] chunkPos = chunkName.split("_", 2);
					System.out.printf("  Loading chunk (%s, %s) from '%s.pachunk'\n", chunkPos[0], chunkPos[1], chunkName);
					world.CHUNKS[Integer.parseInt(chunkPos[0])][Integer.parseInt(chunkPos[1])] = Chunk.load(registry, TextUtil.read(chunkFile), world);
				} else {
					System.out.printf("  Invalid chunk file '%s.pachunk'!\n", chunkName);
				}
			}
			// TODO Load entities
			world.ENTITIES.put("0faa-ec35", new Player(registry, "{\"x\":\"3.5\",\"y\":\"12\",\"xVel\":\"0\",\"yVel\":\"0\"}"));
			// Return the loaded world
			System.out.printf("Successfully loaded '%s' in %d ms\n", info.getOrDefault("name", "New World"), System.currentTimeMillis() - start);
			return world;
		} catch (JsonSyntaxException e) {
			System.out.println("Failed to load world!");
			System.out.println("  Error loading 'worldinfo.json'");
			System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
			return null;
		}
	}
	
	public void save() {
		System.out.printf("Saving world '%s' to '%s'...\n", INFO.getOrDefault("name", "New World"), worldDir);
		long start = System.currentTimeMillis();
		// Save info to file
		TextUtil.write(new File(worldDir + "/worldinfo.json"), new Gson().toJson(INFO));
		// Save loaded chunks
		for (int cx = 0; cx < CHUNKS.length; cx++)
			for (int cy = 0; cy < CHUNKS[cx].length; cy++) {
				CHUNKS[cx][cy].saveTo(new File(worldDir + String.format("/chunks/%s_%s.pachunk", cx, cy)));
				System.out.printf("  Saved chunk at (%d, %d) to '%d_%d.pachunk'\n", cx, cy, cx, cy);
			}
		System.out.printf("Saved world '%s' in %d ms\n", INFO.getOrDefault("name", "New World"), System.currentTimeMillis() - start);
	}

}
