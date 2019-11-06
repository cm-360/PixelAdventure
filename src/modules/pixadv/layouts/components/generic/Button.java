package modules.pixadv.layouts.components.generic;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.components.LayoutComponent;

public class Button extends LayoutComponent {

	private HashMap<KeyCombo, Runnable> clickEvents;
	
	// Constructor
	public Button(HashMap<String, String> boundExpressions, HashMap<KeyCombo, Runnable> clickEvents) {
		super(boundExpressions);
		this.clickEvents = clickEvents;
	}
	
	// Utility methods
	public boolean processClick(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		if (LayoutComponent.makeScreenCoords(gBounds, getBounds(variables)).contains(p)) {
			boolean handled = false;
			for (KeyCombo combo : clickEvents.keySet())
				if (combo.containsAll(keys)) {
					handled = true;
					clickEvents.get(combo).run();
				}
			return handled;
		} else {
			return false;
		}
	}
	
	public boolean processHover(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		return true;
	}

}
