package pixadv.graphics.picasso;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import lib.expr.ExpUtil;
import lib.image.ImageUtil;
import pixadv.registry.Registry;
import pixadv.world.storage.world.World;
import pixadv.world.types.entities.EntityObject;
import pixadv.world.types.tiles.Chunk;

public class Picasso {

	private final Registry REG;
	
	private int tileTextureSize = 32;
	private double tileTextureScale = 1;
	public boolean debug = true;
	public boolean optimized = true;
	
	private String lastExceptionText = "";
	private long lastExceptionTime = -1;
	
	private HashMap<String, BufferedImage> chunkRenderCache = new HashMap<String, BufferedImage>();
	
	// Constructor
	public Picasso(Registry registry) {
		REG = registry;
	}
	
	// Main method
	@SuppressWarnings({ "unchecked", "static-access" })
	public void paint(Graphics g, World world, Point mouseLocation, HashMap<String, String> debugInfo) {
		// Set bounding box
			Rectangle gbounds = g.getClipBounds(), bounds;
		if (world == null) {
			// Draw loading message
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, gbounds.width, gbounds.height);
			int shade = (int) (127 * (Math.sin(System.currentTimeMillis() / 500.0) + 1));
			g.setColor(new Color(shade, shade, shade));
			g.setFont(new Font(null, 0, 12));
			g.drawString("Loading world...", 5, 15);
		} else {
			if (debug) {
				int border = -1;//150;
				bounds = new Rectangle(gbounds.x + border, gbounds.y + border, gbounds.width - 2 * border, gbounds.height - 2 * border);
			} else {
				bounds = gbounds;
			}
			// Correct mouse location
			if (mouseLocation == null)
				mouseLocation = new Point(0, 0);
			// Calculations for drawing
			int centerX = (int) (bounds.x + (bounds.width / 2) - (tileTextureSize * tileTextureScale) / 2);
			int centerY = (int) (bounds.y + (bounds.height / 2) - (tileTextureSize * tileTextureScale) / 2);
			int minX = (int) Math.round(((world.getCameraX() * (tileTextureSize * tileTextureScale) - bounds.width / 2)) / (tileTextureSize * tileTextureScale) - 0.05);
			int maxX = (int) Math.round(((world.getCameraX() * (tileTextureSize * tileTextureScale) + bounds.width / 2)) / (tileTextureSize * tileTextureScale) + 1.05);
			int minY = (int) Math.round(((world.getCameraY() * (tileTextureSize * tileTextureScale) - bounds.height / 2)) / (tileTextureSize * tileTextureScale) - 0.05);
			int maxY = (int) Math.round(((world.getCameraY() * (tileTextureSize * tileTextureScale) + bounds.height / 2)) / (tileTextureSize * tileTextureScale) + 1.05);
			Point mouseTile = getMouseTile(bounds, world, mouseLocation);
			// TODO draw background image
			g.drawImage(REG.getTexture("pixadv/tile/missing"), 0, 0, gbounds.width, gbounds.height, null);
			// Draw chunk grid with new, optimized method
			if (optimized) {
				// Get list of chunk updates
				ArrayList<String> chunkUpdates = world.getChunkUpdates();
				// Iterate over chunks
				for (int cx = (int) Math.round((minX + 0.5) / Chunk.getSize() - 0.5); cx <= (int) Math.round((maxX - 0.5) / Chunk.getSize() - 0.5); cx++)
					for (int cy = (int) Math.round((minY + 0.5) / Chunk.getSize() - 0.5); cy <= (int) Math.round((maxY - 0.5) / Chunk.getSize() - 0.5); cy++) {
						if (cy >= 0 && cy < world.getHeight() / Chunk.getSize()) {
							// Get actual chunk coordinates for proper caching
							Point ca = world.getChunkOf(cx * Chunk.getSize(), cy * Chunk.getSize());
							String chunkName = String.format("%d_%d", ca.x, ca.y);
							if (!chunkRenderCache.containsKey(chunkName) || chunkUpdates.contains(chunkName)) {
								// Create a new image for the chunk cache
								BufferedImage chunkImage = new BufferedImage(
										tileTextureSize * Chunk.getSize(),
										tileTextureSize * Chunk.getSize(),
										BufferedImage.TYPE_INT_ARGB);
								// Draw tiles to chunk image
								Graphics2D cg = chunkImage.createGraphics();
								for (int xc = 0; xc < Chunk.getSize(); xc++)
									for (int yc = 0; yc < Chunk.getSize(); yc++) {
										try {
											int x = cx * Chunk.getSize() + xc;
											int y = cy * Chunk.getSize() + yc;
											// Draw tile layers
											for (int l = 0; l < 3; l++) {
												BufferedImage texture = world.getTile(x, y, l).getTexture();
												if (l == 0)
													texture = ImageUtil.applyBrightness(texture, 0.5);
												cg.drawImage(texture,
														tileTextureSize * xc,
														tileTextureSize * (Chunk.getSize() - (yc + 1)),
														tileTextureSize, tileTextureSize, null);
											}
										} catch (Exception e) {
											// Save exception info
											lastExceptionTime = System.currentTimeMillis();
											lastExceptionText = String.format("Exception: Chunk (%d, %d)   %s: %s",
													cx, cy, e.getClass().getName(), e.getMessage());
										}
									}
								// Finalization
								cg.dispose();
								chunkRenderCache.put(chunkName, chunkImage);
								chunkUpdates.remove(chunkName);
							}
							// Draw entire chunk
							g.drawImage(chunkRenderCache.get(chunkName),
									centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (cx * Chunk.getSize() - world.getCameraX())),
									centerY - (int) Math.round((tileTextureSize * tileTextureScale) * ((cy + 1) * Chunk.getSize() - world.getCameraY() - 1)),
									(int) ((tileTextureSize * tileTextureScale) * Chunk.getSize()),
									(int) ((tileTextureSize * tileTextureScale) * Chunk.getSize()), null);
							// Draw chunk-specific debug info
							if (debug) {
								g.setColor(Color.WHITE);
								g.setFont(new Font(null, 0, 12));
								g.drawRect(
										centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (cx * Chunk.getSize() - world.getCameraX())),
										centerY - (int) Math.round((tileTextureSize * tileTextureScale) * ((cy + 1) * Chunk.getSize() - world.getCameraY() - 1)),
										(int) ((tileTextureSize * tileTextureScale) * Chunk.getSize()),
										(int) ((tileTextureSize * tileTextureScale) * Chunk.getSize()));
								g.drawString(String.format("%d, %d (%d, %d)", ca.x, ca.y, cx, cy),
										centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (cx * Chunk.getSize() - world.getCameraX())) + 5,
										centerY - (int) Math.round((tileTextureSize * tileTextureScale) * ((cy + 1) * Chunk.getSize() - world.getCameraY() - 1)) + 15);
							}
						}
					}
			}
			// Tile grid
			for (int x = minX; x < maxX; x++)
				for (int y = minY; y < maxY; y++) {
					if (y >= 0 && y < world.getHeight()) {
						boolean hover = x == mouseTile.x && y == mouseTile.y;
						try {
							if (!optimized) {
								// Original, unoptimized tile grid drawing method
								BufferedImage texture, textureNew;
								// Draw background tile layer
								texture = world.getTile(x, y, 0).getTexture();
								textureNew = new BufferedImage(texture.getWidth(null), texture.getHeight(null),
										BufferedImage.TYPE_INT_RGB);
								textureNew.getGraphics().drawImage(texture, 0, 0, null);
								g.drawImage(new RescaleOp(0.5f, 0, null).filter(textureNew, null),
										centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
										centerY - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
										(int) (tileTextureSize * tileTextureScale),
										(int) (tileTextureSize * tileTextureScale), null);
								// Draw midground tile layer
								texture = world.getTile(x, y, 1).getTexture();
								g.drawImage(world.getTile(x, y, 1).getTexture(),
										centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
										centerY - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
										(int) (tileTextureSize * tileTextureScale),
										(int) (tileTextureSize * tileTextureScale), null);
								// Draw foreground tile layer
								texture = world.getTile(x, y, 2).getTexture();
								g.drawImage(world.getTile(x, y, 2).getTexture(),
										centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
										centerY - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
										(int) (tileTextureSize * tileTextureScale),
										(int) (tileTextureSize * tileTextureScale), null);
							}
							// Draw tile-specific debug info
							if (hover) {
								// Tile outline
								g.setColor(Color.MAGENTA);
								g.drawRect(
										centerX + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
										centerY - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
										(int) (tileTextureSize * tileTextureScale) - 1,
										(int) (tileTextureSize * tileTextureScale) - 1);
								// Tile position
								if (debug) {
									g.setColor(Color.WHITE);
									g.fillRect(mouseLocation.x, mouseLocation.y - 30, 150, 25);
									g.setColor(Color.BLACK);
									g.setFont(new Font(null, 0, 10));
									g.drawString(String.format("(x%d, y%d)", x, y), mouseLocation.x + 3, mouseLocation.y - 20);
									g.drawString(String.format("%s, %s, %s",
											world.getTile(x, y, 0).getID(),
											world.getTile(x, y, 1).getID(),
											world.getTile(x, y, 2).getID()),
											mouseLocation.x + 3, mouseLocation.y - 8);
								}
							}
						} catch (Exception e) {
							// Save exception info
							lastExceptionTime = System.currentTimeMillis();
							lastExceptionText = String.format("Exception: Tile (%d, %d)   %s: %s",
									x, y, e.getClass().getName(), e.getMessage());
							e.printStackTrace();
						}
					}
				}
			// Draw entities
			for (EntityObject entity : world.getEntities()) {
				try {
					// Parse location info
					HashMap<String, String> data = new Gson().fromJson(entity.getData(), HashMap.class);
					double x = Double.parseDouble(data.get("x"));
					double y = Double.parseDouble(data.get("y"));
					int width = (int) Math.round((tileTextureSize * tileTextureScale) * Double.parseDouble(data.get("width")));
					int height = (int) Math.round((tileTextureSize * tileTextureScale) * Double.parseDouble(data.get("height")));
					if ((x >= minX && x < maxX) && (y >= minY && y < maxY)) {
						// Draw to panel
						g.drawImage(entity.getTexture(),
								(bounds.x + (bounds.width / 2) - width / 2) + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
								(bounds.y + (bounds.height / 2) - height / 2) - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
								width, height, null);
						if (debug) {
							// Draw bounding box
							g.setColor(Color.WHITE);
							g.drawRect(
									(bounds.x + (bounds.width / 2) - width / 2) + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
									(bounds.y + (bounds.height / 2) - height / 2) - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
									width - 1, height - 1);
							// Draw velocity vector arrow
							double xVel = Double.parseDouble(data.get("xVel"));
							double yVel = Double.parseDouble(data.get("yVel"));
							g.drawLine(
									(bounds.x + (bounds.width / 2)) + (int) Math.round((tileTextureSize * tileTextureScale) * (x - world.getCameraX())),
									(bounds.y + (bounds.height / 2)) - (int) Math.round((tileTextureSize * tileTextureScale) * (y - world.getCameraY())),
									(bounds.x + (bounds.width / 2)) + (int) Math.round((tileTextureSize * tileTextureScale) * ((x + xVel) - world.getCameraX())),
									(bounds.y + (bounds.height / 2)) - (int) Math.round((tileTextureSize * tileTextureScale) * ((y + yVel) - world.getCameraY())));
						}
					}
				} catch (Exception e) {
					// Save exception info
					lastExceptionTime = System.currentTimeMillis();
					lastExceptionText = String.format("Exception: Entity ID '%s'   %s: %s", entity.getID(),
							e.getClass().getName(), e.getMessage());
				}
			}
			// Draw general debug info
			if (debug) {
				// Camera position and bounding box
				g.setColor(Color.WHITE);
				g.drawRect(bounds.x + (bounds.width / 2) - 1, bounds.y + (bounds.height / 2) - 1, 2, 2);
				g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				// Debug info bar
				g.setColor(Color.BLACK);
				g.setFont(new Font(null, 0, 12));
				g.clearRect(0, 0, gbounds.width, 60);
				// Performance and screen data
				g.drawString(String.format("%s FPS   CRC Size: %d   EXC Size: %d   P: %s\u03BCs", debugInfo.get("fps"),
						chunkRenderCache.size(), ExpUtil.getCacheSize(), debugInfo.get("physics")), 5, 15);
				Point minChunk = world.getChunkOf(minX, minY), maxChunk = world.getChunkOf(maxX, maxY);
				g.drawString(String.format("Tiles: (x%d, y%d)-(x%d, y%d)  Chunks: (%d, %d)-(%d, %d)",
						minX, minY, maxX, maxY, minChunk.x, minChunk.y, maxChunk.x, maxChunk.y), 5, 30);
				g.drawString(String.format("Modules: %d", REG.listModules().size()), 5, 50);
				// World info
				String universeInfo = String.format("Current world: '%s'", world.getInfo("name"));
				g.drawString(universeInfo, (gbounds.width - (g.getFontMetrics().stringWidth(universeInfo) + 5)), 15);
				// Exceptions while rendering
				if (lastExceptionTime != -1 && System.currentTimeMillis() - lastExceptionTime < 15000) {
					g.clearRect(0, gbounds.height - 20, gbounds.width, 20);
					g.setColor(Color.RED);
					g.drawString(lastExceptionText, 5, gbounds.height - 5);
				}
			} else {
				// Draw FPS in corner
				g.setColor(Color.WHITE);
				g.drawString(debugInfo.get("fps") + " FPS", 5,  15);
			}
		}
	}
	
	
	// Utility methods
	public Point getMouseTile(Rectangle bounds, World world, Point mouseLocation) {
		// Calculations
		int mouseTileX = (int) Math.round((double) (mouseLocation.x - (bounds.x + bounds.width / 2)) / (tileTextureSize * tileTextureScale) + world.getCameraX());
		int mouseTileY = (int) Math.round((double) ((bounds.y + bounds.height / 2) - mouseLocation.y) / (tileTextureSize * tileTextureScale) + world.getCameraY());
		// Return point
		return new Point(mouseTileX, mouseTileY);
	}
	
	public void clearCache() {
		chunkRenderCache.clear();
	}
	
	// Info methods
	public void setTileScale(double scale) {
		tileTextureScale = scale;
	}

	public double getTileScale() {
		return tileTextureScale;
	}
	
}
