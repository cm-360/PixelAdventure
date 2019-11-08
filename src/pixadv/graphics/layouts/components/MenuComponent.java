package pixadv.graphics.layouts.components;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.expr.ExpUtil;
import lib.io.user.KeyCombo;
import pixadv.registry.Registry;

public class MenuComponent {
	
	protected HashMap<String, String> boundExpressions;
	protected HashMap<KeyCombo, Runnable> interactEvents;
	protected String currentTexture = "";
	
	protected HashMap<String, MenuComponent> children = new HashMap<String, MenuComponent>();
	
	// Constructor
	public MenuComponent(HashMap<String, String> boundExpressions, HashMap<KeyCombo, Runnable> interactEvents) {
		this.boundExpressions = boundExpressions;
		this.interactEvents = interactEvents;
	}
	
	// Paint method
	public void paint(Graphics g, Registry registry, HashMap<String, Double> variables) {
		// Calculate own dimensions
		HashMap<String, Double> selfBounds = getBounds(variables), variablesNew = new HashMap<String, Double>();
		variablesNew.put("px", selfBounds.get("x"));
		variablesNew.put("py", selfBounds.get("y"));
		variablesNew.put("pw", selfBounds.get("w"));
		variablesNew.put("ph", selfBounds.get("h"));
		// Paint self texture
		Rectangle selfScreenBounds = makeScreenCoords(g.getClipBounds(), selfBounds);
		if (!currentTexture.isEmpty()) {
			g.drawImage(registry.getTexture(currentTexture), selfScreenBounds.x, selfScreenBounds.y, selfScreenBounds.width, selfScreenBounds.height, null);
			// For debugging
			g.setFont(new Font(null, 0, 12));
			g.drawString(currentTexture, selfScreenBounds.x + 3, selfScreenBounds.y + 13);
		}
		// Paint children components
		for (String cName : children.keySet()) {
			children.get(cName).paint(g, registry, variables);
		}
	}
	
	public HashMap<String, Double> getBounds(HashMap<String, Double> variables) {
		return calculateBounds(boundExpressions, variables);
	}
	
	// Interaction methods
	public MenuComponent processClick(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		// Check if click is on any children
		HashMap<String, Double> selfBounds = updatePBounds(variables, getBounds(variables));
		for (MenuComponent child : children.values()) {
			// Convert child bounds to on-screen coordinates and check for intersect
			HashMap<String, Double> childBounds = child.getBounds(selfBounds);
			if (makeScreenCoords(gBounds, childBounds).contains(p))
				return child.processClick(gBounds, p, keys, selfBounds);
		}
		// Check if event can be self-processed
		if (interactEvents == null) {
			return null;
		} else {
			boolean handled = false;
			for (KeyCombo combo : interactEvents.keySet())
				if (keys.containsAll(combo)) {
					handled = true;
					interactEvents.get(combo).run();
				}
			if (handled)
				return this;
			else
				return null;
		}
	}
	
	public MenuComponent processHover(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		// Check if click is on any children
				HashMap<String, Double> selfBounds = updatePBounds(variables, getBounds(variables));
				for (MenuComponent child : children.values()) {
					// Convert child bounds to on-screen coordinates and check for intersect
					HashMap<String, Double> childBounds = child.getBounds(selfBounds);
					if (makeScreenCoords(gBounds, childBounds).contains(p))
						return child.processHover(gBounds, p, keys, selfBounds);
				}
				// Check if event can be self-processed
				if (interactEvents == null) {
					return null;
				} else {
					boolean handled = false;
					for (KeyCombo combo : interactEvents.keySet())
						if (keys.containsAll(combo)) {
							handled = true;
							interactEvents.get(combo).run();
						}
					if (handled)
						return this;
					else
						return null;
				}
	}
	
	public String processKey() {
		return "";
	}
	
	public void unfocusChildren() {
		for (MenuComponent child : children.values())
			child.unfocusChildren();
	}
	
	public void setTexture(String textureID) {
		currentTexture = textureID;
	}
	
	// Utility methods
	public static HashMap<String, Double> calculateBounds(HashMap<String, String> boundExpressions, HashMap<String, Double> variables) {
		// Iterate over and calculate each expression
		HashMap<String, Double> results = new HashMap<String, Double>();
		for (String variable : boundExpressions.keySet())
			results.put(variable, ExpUtil.calculate(boundExpressions.get(variable), variables));
		return results;
	}
	
	public HashMap<String, Double> updatePBounds(HashMap<String, Double> old, HashMap<String, Double> calculated) {
		HashMap<String, Double> newBounds = new HashMap<String, Double>(old);
		newBounds.put("px", calculated.get("x"));
		newBounds.put("py", calculated.get("y"));
		newBounds.put("pw", calculated.get("w"));
		newBounds.put("ph", calculated.get("h"));
		return newBounds;
	}
	
	public static Rectangle makeScreenCoords(Rectangle gBounds, HashMap<String, Double> values) {
		int screenHeight = gBounds.height;
		return new Rectangle(
				(int) Math.round(values.get("x")),
				(int) Math.round(screenHeight - (values.get("y") + values.get("h"))),
				(int) Math.round(values.get("w")),
				(int) Math.round(values.get("h")));
	}

}
