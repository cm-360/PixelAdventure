package pixadv.graphics.layouts;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;

import pixadv.graphics.layouts.components.MenuComponent;
import pixadv.graphics.swing.util.InputProcessor;
import pixadv.registry.Registry;

public class MenuLayout {

	public static double menuScale = 1;
	
	protected InputProcessor inputProcessor;
	protected HashMap<String, MenuComponent> children = new HashMap<String, MenuComponent>();
	
	// Constructor
	public MenuLayout() {
		inputProcessor = new InputProcessor(this, children);
	}
	
	// Paint method
	public void paint(Graphics g, Registry registry) {
		HashMap<String, Double> bounds = getBounds(g.getClipBounds());
		for (String childName : children.keySet())
			children.get(childName).paint(g, registry, bounds);
	}
	
	// Access method
	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}
	
	// Utility method
	public HashMap<String, Double> getBounds(Rectangle gBounds) {
		HashMap<String, Double> variables = new HashMap<String, Double>();
		variables.put("px", (double) gBounds.x);
		variables.put("py", (double) gBounds.y);
		variables.put("pw", (double) gBounds.width);
		variables.put("ph", (double) gBounds.height);
		variables.put("scale", menuScale);
		return variables;
	}

}
