package modules.pixadv.layouts.components.generic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.components.LayoutComponent;
import pixadv.registry.Registry;

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
				if (keys.containsAll(combo)) {
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
	
	// Paint method
	public void paint(Graphics g, Registry registry, HashMap<String, Double> variables) {
		Rectangle selfBounds = makeScreenCoords(g.getClipBounds(), getBounds(variables));
		g.setColor(Color.WHITE);
		g.fillRect(selfBounds.x, selfBounds.y, selfBounds.width, selfBounds.height);
		g.setColor(Color.MAGENTA);
		g.drawRect(selfBounds.x, selfBounds.y, selfBounds.width, selfBounds.height);
	}

}
