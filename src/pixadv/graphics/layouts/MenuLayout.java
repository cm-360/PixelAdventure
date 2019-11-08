package pixadv.graphics.layouts;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.components.MenuComponent;
import pixadv.registry.Registry;

public class MenuLayout {

	public static double menuScale = 2;
	
	protected ArrayList<MenuComponent> children = new ArrayList<MenuComponent>();
	
	// Constructor
	public MenuLayout() {
		
	}
	
	// Paint method
	public void paint(Graphics g, Registry registry) {
		HashMap<String, Double> bounds = getBounds(g.getClipBounds());
		for (MenuComponent c : children) {
			// TODO calculate own dimensions
			c.paint(g, registry, bounds);
		}
	}
	
	// Interaction methods
	public MenuComponent processClick(Rectangle gBounds, Point p, KeyCombo keys) {
		// Check if click is on any children
		HashMap<String, Double> selfBounds = getBounds(gBounds);
		for (MenuComponent child : children) {
			// Convert child bounds to on-screen coordinates and check for intersect
			HashMap<String, Double> childBounds = child.getBounds(selfBounds);
			if (MenuComponent.makeScreenCoords(gBounds, childBounds).contains(p)) {
				return child.processClick(gBounds, p, keys, selfBounds);
			}
		}
		return null;
	}
	
	public MenuComponent processHover(Rectangle gBounds, Point p, KeyCombo keys) {
		// Check if mouse is on any children
		HashMap<String, Double> selfBounds = getBounds(gBounds);
		for (MenuComponent child : children) {
			// Convert child bounds to on-screen coordinates and check for intersect
			HashMap<String, Double> childBounds = child.getBounds(selfBounds);
			if (MenuComponent.makeScreenCoords(gBounds, childBounds).contains(p)) {
				return child.processHover(gBounds, p, keys, selfBounds);
			}
		}
		return null;
	}
	
	public String processKey() {
		return "";
	}
	
	public void unfocusChildren() {
		for (MenuComponent child : children)
			child.unfocusChildren();
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
