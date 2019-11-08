package modules.pixadv.layouts.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import modules.pixadv.layouts.components.generic.MenuButton;
import modules.pixadv.layouts.components.generic.MenuImage;
import pixadv.graphics.layouts.MenuLayout;
import pixadv.graphics.swing.components.GamePanel;

public class StartMenu extends MenuLayout {

	private String playerID = "0faa-ec35";
	private String playerKey = "abcde";
	
	public StartMenu(GamePanel parent) {
		super();
		// Logo
		HashMap<String, String> boundExpressionsLogo = new HashMap<String, String>();
		boundExpressionsLogo.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsLogo.put("y", "(ph / 2) + (20 * scale) + 100");
		boundExpressionsLogo.put("w", "240 * scale");
		boundExpressionsLogo.put("h", "32 * scale");
		MenuImage imageLogo = new MenuImage(boundExpressionsLogo, null);
		imageLogo.setTexture("pixadv/gui/start-menu/logo");
		children.add(imageLogo);
		// Singleplayer button
		HashMap<String, String> boundExpressionsSingleplayer = new HashMap<String, String>();
		boundExpressionsSingleplayer.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsSingleplayer.put("y", "(ph / 2) + (20 * scale)");
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
		MenuButton buttonSingleplayer = new MenuButton(boundExpressionsSingleplayer, eventsSingleplayer);
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
		MenuButton buttonMultiplayer = new MenuButton(boundExpressionsMultiplayer, eventsMultiplayer);
		buttonMultiplayer.setTexture("pixadv/gui/start-menu/multiplayer");
		children.add(buttonMultiplayer);
		// Options button
		HashMap<String, String> boundExpressionsOptions = new HashMap<String, String>();
		boundExpressionsOptions.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsOptions.put("y", "(ph / 2) - (52 * scale)");
		boundExpressionsOptions.put("w", "(118 * scale)");
		boundExpressionsOptions.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsOptions = new HashMap<KeyCombo, Runnable>();
		eventsOptions.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				// TODO options menu
			}
		});
		MenuButton buttonOptions = new MenuButton(boundExpressionsOptions, eventsOptions);
		buttonOptions.setTexture("pixadv/gui/start-menu/options");
		children.add(buttonOptions);
		// Quit button
		HashMap<String, String> boundExpressionsQuit = new HashMap<String, String>();
		boundExpressionsQuit.put("x", "(pw / 2) + (2 * scale)");
		boundExpressionsQuit.put("y", "(ph / 2) - (52 * scale)");
		boundExpressionsQuit.put("w", "(118 * scale)");
		boundExpressionsQuit.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsQuit = new HashMap<KeyCombo, Runnable>();
		eventsQuit.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				parent.exit();
				System.exit(0);
			}
		});
		MenuButton buttonQuit = new MenuButton(boundExpressionsQuit, eventsQuit);
		buttonQuit.setTexture("pixadv/gui/start-menu/quit");
		children.add(buttonQuit);
	}

}
