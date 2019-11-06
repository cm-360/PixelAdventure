package modules.pixadv.layouts.components.inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.io.user.KeyCombo;
import pixadv.graphics.layouts.components.LayoutComponent;
import pixadv.registry.Registry;

public class InventorySlot extends LayoutComponent {

	// TODO Allow free placement of slots
	
	private InventoryGrid parent;
	private int gx, gy;
	
	// Constructor
	public InventorySlot(InventoryGrid parent, int gx, int gy) {
		super(getExpressions());
		this.parent = parent;
		this.gx = gx; this.gy = gy;
	}
	
	private static HashMap<String, String> getExpressions() {
		// Static expressions
		HashMap<String, String> boundExpressions = new HashMap<String, String>();
		boundExpressions.put("x", "px + (32 * scale * grid_x)");
		boundExpressions.put("y", "(py + ph) - (32 * scale * (grid_y + 1))");
		boundExpressions.put("w", "32 * scale");
		boundExpressions.put("h", "32 * scale");
		return boundExpressions;
	}
	
	@Override
	public void paint(Graphics g, Registry registry, HashMap<String, Double> variables) {
		Rectangle r = LayoutComponent.makeScreenCoords(g.getClipBounds(), getBounds(variables));
		g.drawImage(registry.getTexture("pixadv/gui/inventory/generic/tile"), r.x, r.y, r.width, r.height, null);
		g.setColor(Color.BLACK);
		g.setFont(new Font(null, 0, 10));
		g.drawString(String.format("%d", (gx + 1) % 10), r.x + 5, r.y + 12);
	}
	
	@Override
	public HashMap<String, Double> getBounds(HashMap<String, Double> variables) {
		HashMap<String, Double> variablesNew = new HashMap<String, Double>(variables);
		variablesNew.put("grid_x", (double) gx);
		variablesNew.put("grid_y", (double) gy);
		return calculateBounds(boundExpressions, variablesNew);
	}
	
	// Interaction methods
	@Override
	public boolean processClick(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		if (LayoutComponent.makeScreenCoords(gBounds, getBounds(variables)).contains(p)) {
			parent.setSelected(String.format("%s_%s", gx, gy));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean processHover(Rectangle gBounds, Point p, KeyCombo keys, HashMap<String, Double> variables) {
		return true;
	}

}
