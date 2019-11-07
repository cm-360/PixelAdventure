package modules.pixadv.layouts.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import modules.pixadv.layouts.components.generic.Button;
import pixadv.graphics.layouts.MenuLayout;
import pixadv.graphics.swing.components.GamePanel;

public class StartMenu extends MenuLayout {

	private String playerID = "0faa-ec35";
	private String playerKey = "abcde";
	
	public StartMenu(GamePanel parent) {
		super();
		// Singleplayer button
		HashMap<String, String> boundExpressionsSingleplayer = new HashMap<String, String>();
		boundExpressionsSingleplayer.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsSingleplayer.put("y", "(ph / 2) + (16 * scale) + 8");
		boundExpressionsSingleplayer.put("w", "240 * scale");
		boundExpressionsSingleplayer.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsSingleplayer = new HashMap<KeyCombo, Runnable>();
		eventsSingleplayer.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				if (parent.loadUniverse(parent.getRegistry(), new File("./data/saves/Universe Zero"))) {
					parent.setID(playerID);
					parent.setMenu(new InGameMenu());
				}
			}
		});
		Button buttonSingleplayer = new Button(boundExpressionsSingleplayer, eventsSingleplayer);
		buttonSingleplayer.setTexture("pixadv/gui/start-menu/singleplayer");
		children.add(buttonSingleplayer);
		// Multiplayer button
		HashMap<String, String> boundExpressionsMultiplayer = new HashMap<String, String>();
		boundExpressionsMultiplayer.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsMultiplayer.put("y", "(ph / 2) - (16 * scale)");
		boundExpressionsMultiplayer.put("w", "240 * scale");
		boundExpressionsMultiplayer.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsMultiplayer = new HashMap<KeyCombo, Runnable>();
		eventsMultiplayer.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				if (parent.loadUniverse(parent.getRegistry(), "127.0.0.1:43234", String.format("%s:%s", playerID, playerKey))) {
					parent.setID(playerID);
					parent.setMenu(new InGameMenu());
				}
			}
		});
		Button buttonMultiplayer = new Button(boundExpressionsMultiplayer, eventsMultiplayer);
		buttonMultiplayer.setTexture("pixadv/gui/start-menu/multiplayer");
		children.add(buttonMultiplayer);
		// Options button
		HashMap<String, String> boundExpressionsOptions = new HashMap<String, String>();
		boundExpressionsOptions.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsOptions.put("y", "(ph / 2) - (48 * scale) - 8");
		boundExpressionsOptions.put("w", "(120 * scale) - 4");
		boundExpressionsOptions.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsOptions = new HashMap<KeyCombo, Runnable>();
		eventsOptions.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				// TODO options menu
			}
		});
		Button buttonOptions = new Button(boundExpressionsOptions, eventsOptions);
		buttonOptions.setTexture("");
		children.add(buttonOptions);
		// Quit button
		HashMap<String, String> boundExpressionsQuit = new HashMap<String, String>();
		boundExpressionsQuit.put("x", "(pw / 2) + 4");
		boundExpressionsQuit.put("y", "(ph / 2) - (48 * scale) - 8");
		boundExpressionsQuit.put("w", "(120 * scale) - 4");
		boundExpressionsQuit.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsQuit = new HashMap<KeyCombo, Runnable>();
		eventsQuit.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				parent.exit();
				System.exit(0);
			}
		});
		Button buttonQuit = new Button(boundExpressionsQuit, eventsQuit);
		buttonQuit.setTexture("");
		children.add(buttonQuit);
	}

}
