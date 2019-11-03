package modules.pixadv.layouts.menus;

import java.util.HashMap;

import modules.pixadv.layouts.components.inventory.InventoryGrid;
import pixadv.graphics.swing.components.layouts.MenuLayout;

public class InGameMenu extends MenuLayout {
	
	public InGameMenu() {
		super();
		// TODO Auto-generated constructor stub
		HashMap<String, String> boundExpressionsGrid = new HashMap<String, String>();
		boundExpressionsGrid.put("x", "(px + (pw / 2)) - (160 * scale)");
		boundExpressionsGrid.put("y", "10");
		boundExpressionsGrid.put("w", "320 * scale");
		boundExpressionsGrid.put("h", "32 * scale");
		children.add(new InventoryGrid(10, 1, boundExpressionsGrid));
	}

}
