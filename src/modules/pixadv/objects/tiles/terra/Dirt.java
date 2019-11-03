package modules.pixadv.objects.tiles.terra;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.google.gson.Gson;

import pixadv.registry.Registry;
import pixadv.world.types.EventObject;
import pixadv.world.types.tiles.TileObject;

public class Dirt extends TileObject {

	private final Registry REG;
	
	private boolean mud, grass, snow;
	
	// Constructor
	@SuppressWarnings("unchecked")
	public Dirt(Registry registry, String data) {
		super(registry, data);
		REG = registry;
		try {
			HashMap<String, String> dataMap = new Gson().fromJson(data, HashMap.class);
			mud = Boolean.parseBoolean(dataMap.getOrDefault("", "false"));
			grass = Boolean.parseBoolean(dataMap.getOrDefault("", "false"));
			snow = Boolean.parseBoolean(dataMap.getOrDefault("", "false"));
		} catch (Exception e) {
			// Do nothing
		}
	}
	
	// Info method
	public String getID() {
		return "terra/dirt";
	}
	
	@Override
	public String getData() {
		HashMap<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("mud", Boolean.toString(mud));
		dataMap.put("grass", Boolean.toString(grass));
		dataMap.put("snow", Boolean.toString(snow));
		return new Gson().toJson(dataMap);
	}
	
	@Override
	public String getTextureName() {
		return String.format("pixadv/tile/%s/%s", getID(), mud ? "mud" : "dirt");
	}
	
	@Override
	public BufferedImage getTexture() {
		String type = getTextureName();
		BufferedImage texture = REG.getTexture(type + "/basic");
		if (grass)
			texture.getGraphics().drawImage(REG.getTexture(type + "grass"), 0, 0, null);
		if (snow)
			texture.getGraphics().drawImage(REG.getTexture(type + "snow"), 0, 0, null);
		return texture;
	}
	
	// Event methods
	@Override
	public String dataReceived(EventObject parent, String name, String data) {
		return "";
	}
	
	// Method used when saving
	@Override
	public String toString() {
		return String.format("pixadv/%s:%s", getID(), getData());
	}

}
