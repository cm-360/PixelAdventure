package modules.pixadv.objects.tiles;

import java.awt.image.BufferedImage;

import pixadv.registry.Registry;
import pixadv.world.types.EventObject;
import pixadv.world.types.tiles.TileObject;

public class Air extends TileObject {

	private final Registry REG;
	
	// Constructor
	public Air(Registry registry, String data) {
		super(registry, data);
		REG = registry;
	}
	
	// Info methods
	public String getID() {
		return "air";
	}
	
	@Override
	public String getTextureName() {
		return "pixadv/tile/" + getID();
	}
	
	@Override
	public BufferedImage getTexture() {
		return REG.getTexture(this.getTextureName());
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
