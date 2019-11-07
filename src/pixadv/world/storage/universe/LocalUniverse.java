package pixadv.world.storage.universe;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lib.io.file.FileUtil;
import lib.io.file.TextUtil;
import pixadv.registry.Registry;
import pixadv.world.storage.world.LocalWorld;

public class LocalUniverse extends Universe {

	private File universeDir;
	
	// Constructors
	private LocalUniverse(Registry registry) {
		super(registry);
	}
	
	@SuppressWarnings("unchecked")
	public static LocalUniverse load(Registry registry, File universeDir) {
		System.out.println("** Universe loading **");
		try {
			File universeCanonDir = universeDir.getCanonicalFile();
			System.out.printf("Loading universe '%s' from '%s'...\n", universeCanonDir.getName(), universeCanonDir);
			// Compare to old registry
			System.out.println("Comparing internal registry to 'registry.json'...");
			File registryFile = new File(universeCanonDir + "/registry.json");
			if (registryFile.exists()) {
				try {
					HashMap<String, String> registryData = new Gson().fromJson(TextUtil.read(registryFile), HashMap.class);
					String[] modules = registryData.getOrDefault("modules", "").split(",");
					if (modules.length > 0) {
						for (String moduleInfo : modules) {
							if (registry.listModules().contains(moduleInfo))
								; // TODO compare
						}
					} else {
						System.out.println("  Saved registry lists no modules!");
					}
				} catch (JsonSyntaxException e) {
					System.out.println("  Saved registry is corrupted!");
					System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
				}
			} else {
				System.out.printf("  Unable to locate '%s'!\n", registryFile);
				return null;
			}
			// Find all worlds in this universe
			LocalUniverse universe = new LocalUniverse(registry);
			universe.universeDir = universeCanonDir;
			ArrayList<File> worldDirs = FileUtil.listFiles(new File(universeCanonDir + "/worlds"), new FileFilter() {
				@Override
				public boolean accept(File arg0) {
					return arg0.isDirectory();
				}
			}, false);
			// Load all worlds found
			for (File worldDir : worldDirs) {
				universe.loadWorld(worldDir.getName());
			}
			return universe;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// IO methods
	public void saveAll() {
		for (String worldName : WORLDS.keySet())
			saveWorld(worldName);
	}
	
	public void loadWorld(String name) {
		if (currentWorld == null)
			currentWorld = name;
		WORLDS.put(name, LocalWorld.load(REG, new File(universeDir + "/worlds/" + name)));
	}
	
	public void saveWorld(String name) {
		((LocalWorld) WORLDS.get(name)).save();
	}
	
	// Closing method
	public void close() {
		
	}

}
