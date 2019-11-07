package pixadv.world.types.tiles;

import java.awt.image.BufferedImage;

import pixadv.registry.Registry;
import pixadv.world.storage.WorldEventHandler;
import pixadv.world.types.EventObject;

public class TileObject implements EventObject {

	private final Registry REG;
	
	@SuppressWarnings("unused")
	private WorldEventHandler events;
	
	// Constructor
	public TileObject(Registry registry, String data) {
		REG = registry;
	}
	
	public void setEventHandler(WorldEventHandler events) {
		this.events = events;
	}
	
	// Info methods
	public static String getID() {
		return "unknown";
	}
	
	public String getData() {
		return "";
	}
	
	public String getTextureName() {
		return "pixadv/tile/missing";
	}
	
	public BufferedImage getTexture() {
		return REG.getTexture(this.getTextureName());
	}
	
	// Event methods
	public String dataReceived(EventObject parent, String name, String data) {
		return "";
	}
	
	// Method used when saving
	@Override
	public String toString() {
		return String.format("%s:%s", getID(), getData());
	}

}
