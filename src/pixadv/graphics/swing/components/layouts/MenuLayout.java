package pixadv.graphics.swing.components.layouts;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import pixadv.graphics.layouts.components.LayoutComponent;
import pixadv.registry.Registry;

public class MenuLayout {

	public final double MENU_SCALE = 2;
	
	protected ArrayList<LayoutComponent> children = new ArrayList<LayoutComponent>();
	
	// Constructor
	public MenuLayout() {
		
	}
	
	// Paint method
	public void paint(Graphics g, Registry registry) {
		HashMap<String, Double> bounds = getBounds(g.getClipBounds());
		for (LayoutComponent c : children) {
			// TODO calculate own dimensions
			c.paint(g, registry, bounds);
		}
	}
	
	// Interaction methods
	public String processClick(Rectangle gBounds, Point p, int button, ArrayList<Character> modifierKeys) {
		// Check if click is on any children
		HashMap<String, Double> selfBounds = getBounds(gBounds);
		for (LayoutComponent child : children) {
			// Convert child bounds to on-screen coordinates and check for intersect
			HashMap<String, Double> childBounds = child.getBounds(selfBounds);
			if (LayoutComponent.makeScreenCoords(gBounds, childBounds).contains(p)) {
				String result = child.processClick(gBounds, p, button, modifierKeys, selfBounds);
				if (!result.isEmpty())
					return result;
			}
		}
		return "";
	}
	
	public String processHover(Rectangle gBounds, Point p, ArrayList<Character> modifierKeys) {
		// Check if mouse is on any children
		HashMap<String, Double> selfBounds = getBounds(gBounds);
		for (LayoutComponent child : children) {
			// Convert child bounds to on-screen coordinates and check for intersect
			HashMap<String, Double> childBounds = child.getBounds(selfBounds);
			if (LayoutComponent.makeScreenCoords(gBounds, childBounds).contains(p)) {
				String result = child.processHover(gBounds, p, modifierKeys, selfBounds);
				if (!result.isEmpty())
					return result;
			}
		}
		return "";
	}
	
	public String processKey() {
		return "";
	}
	
	// Utility method
	public HashMap<String, Double> getBounds(Rectangle gBounds) {
		HashMap<String, Double> variables = new HashMap<String, Double>();
		variables.put("px", (double) gBounds.x);
		variables.put("py", (double) gBounds.y);
		variables.put("pw", (double) gBounds.width);
		variables.put("ph", (double) gBounds.height);
		variables.put("scale", MENU_SCALE);
		return variables;
	}

}
