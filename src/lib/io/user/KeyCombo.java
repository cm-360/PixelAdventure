package lib.io.user;

import java.util.ArrayList;

public class KeyCombo {

	private int mouseButton;
	private ArrayList<Character> keys;
	
	// Constructor
	public KeyCombo(int mouseButton, ArrayList<Character> pressedKeys) {
		this.mouseButton = mouseButton;
		this.keys = pressedKeys;
	}
	
	// Access methods
	public int getMouse() {
		return mouseButton;
	}
	
	public ArrayList<Character> getKeys() {
		return keys;
	}
	
	// Comparison methods
	public boolean equals(KeyCombo otherCombo) {
		return mouseButton == otherCombo.mouseButton && keys.equals(otherCombo.keys);
	}
	
	public boolean containsAll(KeyCombo otherCombo) {
		return mouseButton == otherCombo.mouseButton && keys.containsAll(otherCombo.keys);
	}

}
