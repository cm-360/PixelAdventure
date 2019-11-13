package pixadv.graphics.layouts.components;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import lib.expr.ExpUtil;
import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.MenuLayout;
import pixadv.registry.Registry;

public class MenuComponent {
	
	protected HashMap<String, String> boundExpressions;
	protected String currentTexture = "";
	
	protected String textContent = "";
	protected Font textFont;
	
	protected HashMap<KeyCombo, Runnable> interactEvents;
	protected boolean focusable = false;
	
	protected HashMap<String, MenuComponent> children = new HashMap<String, MenuComponent>();
	
	// Constructors
	public MenuComponent(HashMap<String, String> boundExpressions, HashMap<KeyCombo, Runnable> interactEvents) {
		this.boundExpressions = boundExpressions;
		this.interactEvents = interactEvents;
	}
	
	public MenuComponent(HashMap<String, String> boundExpressions, HashMap<KeyCombo, Runnable> interactEvents, boolean focusable) {
		this.boundExpressions = boundExpressions;
		this.interactEvents = interactEvents;
		this.focusable = focusable;
	}
	
	// Paint methods
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
			g.drawImage(registry.getTexture(currentTexture),
					selfScreenBounds.x, selfScreenBounds.y,
					selfScreenBounds.width, selfScreenBounds.height, null);
		}
		// Paint text
		if (!textContent.isEmpty()) {
			g.setFont(textFont.deriveFont((float) (textFont.getSize() * MenuLayout.menuScale)));
			FontMetrics fontMetrics = g.getFontMetrics();
			g.drawString(textContent,
					(int) (selfScreenBounds.x + (selfScreenBounds.width - fontMetrics.stringWidth(textContent)) / 2.0),
					(int) (selfScreenBounds.y + (selfScreenBounds.height + fontMetrics.getAscent() * 0.7) / 2.0));
		}
		// Paint children components
		ArrayList<String> childrenNames = new ArrayList<String> (children.keySet());
		Collections.sort(childrenNames);
		for (String childName : childrenNames) {
			children.get(childName).paint(g, registry, variables);
		}
	}
	
	public void setTexture(String textureID) {
		currentTexture = textureID;
	}
	
	public void setText(String text, Font font) {
		textContent = text;
		textFont = font;
	}
	
	// Focus methods
	public String attemptFocus(Rectangle gBounds, Point mousePos, HashMap<String, Double> variables, String myName) {
		// Check if click is on any children
		HashMap<String, Double> selfBounds = updatePBounds(variables, getBounds(variables));
		for (String childName : children.keySet()) {
			// Convert child bounds to on-screen coordinates and check for intersect
			HashMap<String, Double> childBounds = children.get(childName).getBounds(selfBounds);
			if (makeScreenCoords(gBounds, childBounds).contains(mousePos)) {
				String result = children.get(childName).attemptFocus(gBounds, mousePos, selfBounds, childName);
				if (!result.isEmpty())
					return String.format("%s/%s", myName, result);
			}
		}
		// Focus self
		if (focusable && makeScreenCoords(gBounds, getBounds(variables)).contains(mousePos))
			return myName + "/";
		else
			return "";
	}
	
	public void unfocusChildren() {
		for (MenuComponent child : children.values())
			child.unfocusChildren();
	}
	
	public MenuComponent getChild(String childPath, String myName) {
		if (childPath.isEmpty()) {
			// Last child in path
			return this;
		} else {
			// Search other children for correct child
			String[] childPathSplit = childPath.split("\\/", 2);
			return children.get(childPathSplit[0]).getChild(childPathSplit[1], childPathSplit[0]);
		}
		
	}
	
	// Interaction methods
	public void interactClick(Rectangle gBounds, Point mousePos, long clickDuration, KeyCombo keys, HashMap<String, Double> variables) {
		for (KeyCombo combo : interactEvents.keySet())
			if (keys.containsAll(combo))
				interactEvents.get(combo).run();
	}
	
	public void interactHover(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		
	}
	
	public void interactKey(KeyCombo keys) {
		
	}
	
	// Utility methods
	public static HashMap<String, Double> calculateBounds(HashMap<String, String> boundExpressions, HashMap<String, Double> variables) {
		// Iterate over and calculate each expression
		HashMap<String, Double> results = new HashMap<String, Double>();
		for (String variable : boundExpressions.keySet())
			results.put(variable, ExpUtil.calculate(boundExpressions.get(variable), variables));
		return results;
	}
	
	public HashMap<String, Double> getBounds(HashMap<String, Double> variables) {
		return calculateBounds(boundExpressions, variables);
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
