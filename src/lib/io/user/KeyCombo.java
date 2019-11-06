package lib.io.user;

import java.util.ArrayList;

public class KeyCombo {

	private int mouseButton;
	private ArrayList<Integer> keys;
	
	public KeyCombo(int mouseButton, ArrayList<Integer> keyValues) {
		this.mouseButton = mouseButton;
		this.keys = keyValues;
	}
	
	public int getMouse() {
		return mouseButton;
	}
	
	public ArrayList<Integer> getKeys() {
		return keys;
	}

}
