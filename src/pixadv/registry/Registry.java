package pixadv.registry;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

import lib.io.file.FileUtil;
import modules.ModuleLoader;
import modules.pixadv.Module;
import pixadv.world.storage.world.World;
import pixadv.world.types.tiles.TileObject;

public class Registry {

	// List of loaded modules
	private final ArrayList<String> MODULES = new ArrayList<String>();
	
	// Object creation maps
	private final HashMap<String, Class<? extends TileObject>> TILES = new HashMap<String, Class<? extends TileObject>>();
	
	// Resource maps
	private final HashMap<String, BufferedImage> TEXTURES = new HashMap<String, BufferedImage>();
	
	// Constructors
	@SuppressWarnings("unchecked")
	public Registry() {
		// Get URL to load builtin classes
		String path = getClass().getResource(getClass().getSimpleName() + ".class").getFile();
		URL codePath = null;
		if (path.startsWith("/")) {
			// Loading from IDE
			codePath = getClass().getProtectionDomain().getCodeSource().getLocation();
			try {
				// Load resources from directory
				File codePathFile = new File(codePath.toURI());
				System.out.printf("Searching directory: '%s'\n", codePathFile);
				TEXTURES.put("pixadv/tile/air", new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
				// Recursive list of all files in directory
				System.out.println("Loading resources from <BUILTIN>...");
				ArrayList<File> files = FileUtil.listFiles(codePathFile, new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isFile() && !pathname.toString().endsWith(".class");
					}
				}, true);
				// Find files that are resources
				for (File f : files) {
					String relativeName = f.toString().replace(codePathFile.toString(), "")
							.replace(File.separatorChar, '/').replaceAll("^\\/modules\\/", "");
					if (relativeName.endsWith(".png")) {
						try {
							String textureID = relativeName.replace("textures/", "").replaceAll("\\.png$", "");
							// Load image and keep transparency
							ImageIcon iconImage = new ImageIcon(f.toString());
							BufferedImage bufferedImage = new BufferedImage(iconImage.getIconWidth(), iconImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
							bufferedImage.getGraphics().drawImage(iconImage.getImage(), 0, 0, null);
							TEXTURES.put(textureID, bufferedImage);
							System.out.printf("  Loaded texture 'modules/%s' (%s)\n", relativeName, textureID);
						} catch (Exception e) {
							System.out.printf("  Error loading texture: 'modules/%s'\n", relativeName);
							System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
						}
					}
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			try {
				// Loading from JAR
				path = ClassLoader.getSystemClassLoader().getResource(path).getFile();
				codePath = new URL(path.substring(0, path.lastIndexOf('!')));
				System.out.printf("Searching JAR: '%s'\n", codePath);
				// TODO Load resources from inside jar
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		try {
			// Create a URLClassLoader to load the builtin module
			URLClassLoader ucl = new URLClassLoader(new URL[] { codePath });
			Class<Module> builtinModuleClass = (Class<Module>) ucl.loadClass("modules.pixadv.Module");
			ModuleLoader builtinModule = builtinModuleClass.getConstructor().newInstance();
			MODULES.add(String.format("%s@%s", builtinModule.getID(), builtinModule.getVersion()));
			// Load tile textures
			System.out.println("Loading tiles from <BUILTIN>...");
			HashMap<String, Class<? extends TileObject>> tiles = builtinModule.loadTiles();
			for (String id : tiles.keySet()) {
				String idNew = String.format("%s/%s", builtinModule.getID(), id);
				TILES.put(idNew, tiles.get(id));
				System.out.printf("  Registered '%s': %s\n", idNew, TILES.get(idNew).getName());
			}
			// Close URLClassLoader
			ucl.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadJar(File resourcePath) {
		try {
			// Create a URLClassLoader for the module
			URLClassLoader ucl = new URLClassLoader(new URL[] { resourcePath.toURI().toURL() });
			Class<Module> loadingModuleClass = (Class<Module>) ucl.loadClass("modules.pixadv.Module");
			// Load resources from the module
			ModuleLoader loadingModule = loadingModuleClass.getConstructor().newInstance();
			HashMap<String, Class<? extends TileObject>> tiles = loadingModule.loadTiles();
			for (String id : tiles.keySet()) {
				String idNew = String.format("%s/%s", loadingModule.getID(), id);
				if (TILES.containsKey(idNew)) {
					// Append number to prevent ID conflicts (should never happen)
					System.out.printf("  Warning! Duplicate ID '%s'\n", id);
					int count = 0;
					while (TILES.containsKey(idNew + count))
						count++;
					System.out.printf("  Registered '%s': %s\n", idNew + count, TILES.get(idNew).getName());
				} else {
					TILES.put(idNew, tiles.get(id));
					System.out.printf("  Registered '%s': %s\n", idNew, TILES.get(idNew).getName());
				}
			}
			// Close URLClassLoader
			ucl.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Tile methods
	public TileObject createTile(String id, String data, World world) {
		try {
			return TILES.get(id).getConstructor(Registry.class, String.class).newInstance(this, data);
		} catch (Exception e) {
			// TODO Could not use constructor
			e.printStackTrace();
			return null;
		}
	}
	
	// Resource methods
	public BufferedImage getTexture(String id) {
		BufferedImage internal = TEXTURES.getOrDefault(id, TEXTURES.get("pixadv/tile/missing"));
		BufferedImage copy = new BufferedImage(internal.getWidth(), internal.getHeight(), BufferedImage.TYPE_INT_ARGB);
		copy.getGraphics().drawImage(internal, 0, 0, null);
		return copy;
	}
	
	// Info methods
	public ArrayList<String> listModules() {
		// Return clone to keep original unmodified
		ArrayList<String> copy = new ArrayList<String>();
		for (String s : MODULES)
			copy.add(s);
		return copy;
	}
	
	@Override
	public String toString() {
		String result = "{\"modules\":\"";
		for (int i = 0; i < MODULES.size(); i++) {
			result += MODULES.get(i);
			if (i != MODULES.size() - 1)
				result += ",";
			else
				result += "\"}";
		}
		return result;
	}

}
