package pixadv.graphics.swing.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.MenuLayout;
import pixadv.graphics.layouts.components.MenuComponent;

public class InputProcessor {

	private MenuLayout parent;
	private HashMap<String, MenuComponent> children;
	private String focusedComponent;
	
	private long clickOriginTime = -1;
	private Point clickOriginPoint = new Point();
	
	private boolean dragging = false;
	private boolean pressed = false;
	
	// Constructor
	public InputProcessor(MenuLayout parentLayout, HashMap<String, MenuComponent> childMap) {
		parent = parentLayout;
		children = childMap;
	}
	
	// Mouse methods
	public void mousePressed(Rectangle gBounds, Point mousePos, KeyCombo keys) {
		// Save current mouse info
		pressed = true;
		clickOriginTime = System.currentTimeMillis();
		clickOriginPoint = mousePos;
		// Attempt to focus children components
		for (String childName : children.keySet()) {
			String result = children.get(childName).attemptFocus(gBounds, mousePos, parent.getBounds(gBounds), childName);
			if (!result.isEmpty()) {
				// Save name of now focused child
				focusedComponent = result;
				return;
			}
		}
		// Nothing was focused, unfocus all
		focusedComponent = "";
	}
	
	public void mouseReleased(Rectangle gBounds, Point mousePos, KeyCombo keys) {
		if (!focusedComponent.isEmpty()) {
			// Check for clicks
			long difference = clickOriginTime - System.currentTimeMillis();
			// TODO separate long and short clicks
			if (difference > 0 && difference < 1000) {
				// Normal click
				getChild(focusedComponent).interactClick(gBounds, mousePos, difference, keys, parent.getBounds(gBounds));
			} else {
				// Long click
				getChild(focusedComponent).interactClick(gBounds, mousePos, difference, keys, parent.getBounds(gBounds));
			}
			// Reset mouse state
			dragging = false;
			pressed = false;
		}
	}
	
	public void mouseMoved(Rectangle gBounds, Point mousePos, KeyCombo keys) {
		if (dragging || pressed) {
			// Dragging focused component
			dragging = true;
			
		} 
	}
	
	// TODO Keyboard methods
	
	// Access methods
	public MenuComponent getChild(String childPath) {
		// Search other children for correct child
		if (childPath.isEmpty()) {
			return null;
		} else {
			String[] childPathSplit = childPath.split("\\/", 2);
			return children.get(childPathSplit[0]).getChild(childPathSplit[1], childPathSplit[0]);
		}
	}
	
	public MenuComponent getFocusedComponent() {
		return getChild(focusedComponent);
	}

}
