package modules.pixadv.layouts.components.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;

import pixadv.graphics.layouts.components.MenuComponent;
import pixadv.registry.Registry;

public class InventoryGrid extends MenuComponent {
	
	private String selected = "";
	
	// Constructor
	public InventoryGrid(int width, int height, HashMap<String, String> boundExpressions) {
		super(boundExpressions, null);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				children.put(String.format("%d_%d", x, y), new InventorySlot(this, x, y));
			}
	}
	
	// Control method
	public void setSelected(String item) {
		selected = item;
	}
	
	// Painting method
	@Override
	public void paint(Graphics g, Registry registry, HashMap<String, Double> variables) {
		// Calculate own bounds
		HashMap<String, Double> selfBounds = getBounds(variables), variablesNew = new HashMap<String, Double>(variables);
		variablesNew.put("px", selfBounds.get("x"));
		variablesNew.put("py", selfBounds.get("y"));
		variablesNew.put("pw", selfBounds.get("w"));
		variablesNew.put("ph", selfBounds.get("h"));
		// Draw grid tiles
		for (String name : children.keySet()) {
			MenuComponent slot = children.get(name);
			slot.paint(g, registry, variablesNew);
			if (name.equals(selected)) {
				Rectangle slotBounds = MenuComponent.makeScreenCoords(g.getClipBounds(), slot.getBounds(variablesNew));
				g.setColor(Color.MAGENTA);
				g.drawRect(slotBounds.x, slotBounds.y, slotBounds.width - 1, slotBounds.height - 1);
			}
		}
	}

}
