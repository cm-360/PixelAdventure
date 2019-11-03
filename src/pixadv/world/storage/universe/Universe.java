package pixadv.world.storage.universe;

import java.util.HashMap;

import pixadv.graphics.picasso.Picasso;
import pixadv.registry.Registry;
import pixadv.sound.beethoven.Beethoven;
import pixadv.world.newton.Newton;
import pixadv.world.storage.world.World;
import pixadv.world.types.entities.EntityObject;

public class Universe {

	protected final Registry REG;

	protected final Picasso RENDER;
	protected final Beethoven SOUND;
	protected final Newton PHYSICS;
	
	protected final HashMap<String, World> WORLDS = new HashMap<String, World>();
	protected String currentWorld;
	
	protected String playerID;
	
	// Constructor
	protected Universe(Registry registry) {
		REG = registry;
		// Create world simulation handlers
		RENDER = new Picasso(registry);
		SOUND = new Beethoven(registry);
		PHYSICS = new Newton(registry, this);
	}
	
	// World methods
	public World getWorld(String name) {
		return WORLDS.get(name);
	}
	
	public World currentWorld() {
		return WORLDS.get(currentWorld);
	}
	
	// Player methods
	public void setID(String playerID) {
		this.playerID = playerID;
	}
	
	public EntityObject getPlayer() {
		return currentWorld().getEntity(playerID);
	}
	
	// Handler methods
	public Registry getRegistry() {
		return REG;
	}
	
	public Picasso getRender() {
		return RENDER;
	}
	
	public Beethoven getSound() {
		return SOUND;
	}
	
	public Newton getPhysics() {
		return PHYSICS;
	}

}
