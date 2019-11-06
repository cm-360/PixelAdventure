package lib.io.user;

import java.util.ArrayList;

public class KeyCombo {

	private int mouseButton;
	private ArrayList<Integer> keys;
	
	// Constructor
	public KeyCombo(int mouseButton, ArrayList<Integer> keyValues) {
		this.mouseButton = mouseButton;
		this.keys = keyValues;
	}
	
	// Access methods
	public int getMouse() {
		return mouseButton;
	}
	
	public ArrayList<Integer> getKeys() {
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
