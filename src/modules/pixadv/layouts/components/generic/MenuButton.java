package modules.pixadv.layouts.components.generic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.components.MenuComponent;
import pixadv.registry.Registry;

public class MenuButton extends MenuComponent {

	private HashMap<KeyCombo, Runnable> clickEvents;
	private String currentTexture = "";
	
	// Constructor
	public MenuButton(HashMap<String, String> boundExpressions, HashMap<KeyCombo, Runnable> clickEvents) {
		super(boundExpressions);
		this.clickEvents = clickEvents;
	}
	
	// Utility methods
	public MenuComponent processClick(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		if (MenuComponent.makeScreenCoords(gBounds, getBounds(variables)).contains(p)) {
			boolean handled = false;
			for (KeyCombo combo : clickEvents.keySet())
				if (keys.containsAll(combo)) {
					handled = true;
					clickEvents.get(combo).run();
				}
			if (handled)
				return this;
		}
		return null;
	}
	
	public MenuComponent processHover(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		if (MenuComponent.makeScreenCoords(gBounds, getBounds(variables)).contains(p)) {
			boolean handled = false;
			for (KeyCombo combo : clickEvents.keySet())
				if (keys.containsAll(combo)) {
					handled = true;
					clickEvents.get(combo).run();
				}
			if (handled)
				return this;
		}
		return null;
	}
	
	// Control method
	public void setTexture(String textureID) {
		currentTexture = textureID;
	}
	
	// Paint method
	public void paint(Graphics g, Registry registry, HashMap<String, Double> variables) {
		Rectangle selfBounds = makeScreenCoords(g.getClipBounds(), getBounds(variables));
		if (currentTexture.isEmpty()) {
			g.setColor(Color.WHITE);
			g.fillRect(selfBounds.x, selfBounds.y, selfBounds.width - 1, selfBounds.height - 1);
			g.setColor(Color.MAGENTA);
			g.drawRect(selfBounds.x, selfBounds.y, selfBounds.width - 1, selfBounds.height - 1);
		} else {
			g.drawImage(registry.getTexture(currentTexture), selfBounds.x, selfBounds.y, selfBounds.width, selfBounds.height, null);
			g.setFont(new Font(null, 0, 12));
			g.drawString(currentTexture, selfBounds.x + 3, selfBounds.y + 13);
		}
	}

}
