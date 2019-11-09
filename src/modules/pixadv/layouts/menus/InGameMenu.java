package modules.pixadv.layouts.menus;

import java.util.HashMap;

import modules.pixadv.layouts.components.inventory.InventoryGrid;
import pixadv.graphics.layouts.MenuLayout;

public class InGameMenu extends MenuLayout {
	
	public InGameMenu() {
		super();
		// Inventory hotbar
		HashMap<String, String> boundExpressionsHotbar = new HashMap<String, String>();
		boundExpressionsHotbar.put("x", "(px + (pw / 2)) - (160 * scale)");
		boundExpressionsHotbar.put("y", "10");
		boundExpressionsHotbar.put("w", "320 * scale");
		boundExpressionsHotbar.put("h", "32 * scale");
		children.put("inventoryHotbar", new InventoryGrid(10, 1, boundExpressionsHotbar));
		// Chat terminal
//		HashMap<String, String> boundExpressionsChat = new HashMap<String, String>();
//		boundExpressionsChat.put("x", "0");
//		boundExpressionsChat.put("y", "0");
//		boundExpressionsChat.put("w", "pw / 3");
//		boundExpressionsChat.put("h", "(ph / 3) * scale");
//		children.add();
	}

}
