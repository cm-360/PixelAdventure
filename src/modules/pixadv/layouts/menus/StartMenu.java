package modules.pixadv.layouts.menus;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.MenuLayout;
import pixadv.graphics.layouts.components.MenuComponent;
import pixadv.graphics.swing.components.GamePanel;

public class StartMenu extends MenuLayout {

	private String playerID = "0faa-ec35";
	private String playerKey = "abcde";
	
	public StartMenu(GamePanel parent) {
		super();
		// Main font
		Font menuFont = new Font("SansSerif", 0, 18);
		// Background
		HashMap<String, String> boundExpressionsBackground = new HashMap<String, String>();
		boundExpressionsBackground.put("x", "px");
		boundExpressionsBackground.put("y", "py");
		boundExpressionsBackground.put("w", "pw");
		boundExpressionsBackground.put("h", "ph");
		MenuComponent imageBackground = new MenuComponent(boundExpressionsBackground, null);
		imageBackground.setTexture("pixadv/gui/icon");
		children.put("0-imageBackground", imageBackground);
		// Logo
		HashMap<String, String> boundExpressionsLogo = new HashMap<String, String>();
		boundExpressionsLogo.put("x", "(pw / 2) - (155 * scale)");
		boundExpressionsLogo.put("y", "(ph / 2) + (60 * scale)");
		boundExpressionsLogo.put("w", "310 * scale");
		boundExpressionsLogo.put("h", "110 * scale");
		MenuComponent imageLogo = new MenuComponent(boundExpressionsLogo, null);
		imageLogo.setTexture("pixadv/gui/start-menu/logo");
		children.put("1-imageLogo", imageLogo);
		// Singleplayer button
		HashMap<String, String> boundExpressionsSingleplayer = new HashMap<String, String>();
		boundExpressionsSingleplayer.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsSingleplayer.put("y", "(ph / 2) - (16 * scale)");
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
		MenuComponent buttonSingleplayer = new MenuComponent(boundExpressionsSingleplayer, eventsSingleplayer, true);
		buttonSingleplayer.setText("Singleplayer", menuFont);
		buttonSingleplayer.setTexture("pixadv/gui/start-menu/singleplayer");
		children.put("2-buttonSingleplayer", buttonSingleplayer);
		// Multiplayer button
		HashMap<String, String> boundExpressionsMultiplayer = new HashMap<String, String>();
		boundExpressionsMultiplayer.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsMultiplayer.put("y", "(ph / 2) - (52 * scale)");
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
		MenuComponent buttonMultiplayer = new MenuComponent(boundExpressionsMultiplayer, eventsMultiplayer, true);
		buttonMultiplayer.setText("Multiplayer", menuFont);
		buttonMultiplayer.setTexture("pixadv/gui/start-menu/multiplayer");
		children.put("2-buttonMultiplayer", buttonMultiplayer);
		// Options button
		HashMap<String, String> boundExpressionsOptions = new HashMap<String, String>();
		boundExpressionsOptions.put("x", "(pw / 2) - (120 * scale)");
		boundExpressionsOptions.put("y", "(ph / 2) - (88 * scale)");
		boundExpressionsOptions.put("w", "118 * scale");
		boundExpressionsOptions.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsOptions = new HashMap<KeyCombo, Runnable>();
		eventsOptions.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				// TODO options menu
			}
		});
		MenuComponent buttonOptions = new MenuComponent(boundExpressionsOptions, eventsOptions, true);
		buttonOptions.setText("Options", menuFont);
		buttonOptions.setTexture("pixadv/gui/start-menu/options");
		children.put("2-buttonOptions", buttonOptions);
		// Quit button
		HashMap<String, String> boundExpressionsQuit = new HashMap<String, String>();
		boundExpressionsQuit.put("x", "(pw / 2) + (2 * scale)");
		boundExpressionsQuit.put("y", "(ph / 2) - (88 * scale)");
		boundExpressionsQuit.put("w", "118 * scale");
		boundExpressionsQuit.put("h", "32 * scale");
		HashMap<KeyCombo, Runnable> eventsQuit = new HashMap<KeyCombo, Runnable>();
		eventsQuit.put(new KeyCombo(1, new ArrayList<Integer>()), new Runnable() {
			@Override
			public void run() {
				parent.exit();
				System.exit(0);
			}
		});
		MenuComponent buttonQuit = new MenuComponent(boundExpressionsQuit, eventsQuit, true);
		buttonQuit.setText("Quit Game", menuFont);
		buttonQuit.setTexture("pixadv/gui/start-menu/quit");
		children.put("2-buttonQuit", buttonQuit);
	}

}
