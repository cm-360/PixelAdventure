package pixadv.world.types.entities;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import pixadv.registry.Registry;
import pixadv.world.storage.WorldEventHandler;
import pixadv.world.types.EventObject;

public class EntityObject implements EventObject {

private final Registry REG;

	@SuppressWarnings("unused")
	private WorldEventHandler events;
	
	private double x = 0, y = 0, xVel = 0, yVel = 0;
	private double width = 1, height = 1;
	private boolean illegalPhysics = false;
	
	private BufferedImage texture;
	
	// Constructor
	@SuppressWarnings("unchecked")
	public EntityObject(Registry registry, String data) {
		REG = registry;
		texture = REG.getTexture("pixadv/entity/missing");
		try {
			HashMap<String, String> dataMap = new Gson().fromJson(data, HashMap.class);
			x = Double.parseDouble(dataMap.getOrDefault("x", "0"));
			y = Double.parseDouble(dataMap.getOrDefault("y", "0"));
			xVel = Double.parseDouble(dataMap.getOrDefault("xVel", "0"));
			yVel = Double.parseDouble(dataMap.getOrDefault("yVel", "0"));
			width = Double.parseDouble(dataMap.getOrDefault("width", "1"));
			height = Double.parseDouble(dataMap.getOrDefault("height", "1"));
		} catch (Exception e) {
			// Do nothing
		}
	}
	
	public void setEventHandler(WorldEventHandler events) {
		this.events = events;
	}
	
	// Info methods
	public static String getID() {
		return "unknown";
	}
	
	public String getData() {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("x", Double.toString(x));
		data.put("y", Double.toString(y));
		data.put("xVel", Double.toString(xVel));
		data.put("yVel", Double.toString(yVel));
		data.put("width", Double.toString(width));
		data.put("height", Double.toString(height));
		return new Gson().toJson(data);
	}
	
	public BufferedImage getTexture() {
		return texture;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s", getID(), getData());
	}
	
	// Event methods
	@SuppressWarnings("unchecked")
	public String dataReceived(EventObject parent, String name, String data) {
		try {
			HashMap<String, String> dataMap = new Gson().fromJson(data, HashMap.class);
			if (name.equals("movement")) {
				x = Double.parseDouble(dataMap.getOrDefault("x", "0"));
				y = Double.parseDouble(dataMap.getOrDefault("y", "0"));
				xVel = Double.parseDouble(dataMap.getOrDefault("xVel", "0"));
				yVel = Double.parseDouble(dataMap.getOrDefault("yVel", "0"));
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return "";
	}

}
