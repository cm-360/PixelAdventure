package pixadv.graphics.swing.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.google.gson.Gson;

import lib.io.user.KeyCombo;
import modules.pixadv.layouts.menus.StartMenu;
import modules.pixadv.objects.tiles.Air;
import modules.pixadv.objects.tiles.terra.Dirt;
import pixadv.graphics.layouts.MenuLayout;
import pixadv.graphics.picasso.Picasso;
import pixadv.world.storage.universe.LocalUniverse;
import pixadv.world.storage.universe.NetworkUniverse;
import pixadv.world.storage.universe.Universe;
import pixadv.world.storage.world.World;
import pixadv.world.types.entities.EntityObject;
import pixadv.world.types.tiles.TileObject;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private long lastRun = -1;
	private int frames = 0;
	private HashMap<String, String> debugInfo = new HashMap<String, String>();
	
	private Universe loadedUniverse;
	
	private Rectangle bounds;
	private Point mouseLocation;
	private Point mouseClickOrigin;
	private ArrayList<Integer> pressedKeys = new ArrayList<Integer>();
	private double cameraXOld, cameraYOld;
	
	private Rectangle lastBounds = new Rectangle();
	private MenuLayout currentLayout = new StartMenu();
	
	public boolean menu = true;
	
	public GamePanel() {
		debugInfo.put("fps", "-1");
		// User input thread
		new Thread(new Runnable() {
			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				while (true)
					try {
						EntityObject player = loadedUniverse.getPlayer();
						HashMap<String, String> data = new Gson().fromJson(player.getData(), HashMap.class);
						if (pressedKeys.contains(Character.getNumericValue('w')))
							data.put("yVel", "15");
						if (pressedKeys.contains(Character.getNumericValue('s')))
							;
						if (pressedKeys.contains(Character.getNumericValue('a')))
							data.put("xVel", "-5");
						if (pressedKeys.contains(Character.getNumericValue('d')))
							data.put("xVel", "5");
						//loadedUniverse.getPhysics().manualChange(player, new Gson().toJson(data));
						player.dataReceived(null, "physics", new Gson().toJson(data));
					} catch (NullPointerException e) {
						// Do nothing
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}, "PixAdv-InputThread").start();
		// Physics thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true)
					try {
						// Recalculate physics
						long start = System.nanoTime();
						loadedUniverse.getPhysics().recalculate();
						debugInfo.put("physics", Long.toString((System.nanoTime() - start) / 1000));
					} catch (NullPointerException e) {
						// Do nothing
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}, "PixAdv-PhysicsThread").start();
		// Repaint timer
		new Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repaint();
			}
		}).start();
		// Mouse events
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				mouseClickOrigin = arg0.getPoint();
				mouseLocation = mouseClickOrigin;
				boolean result = currentLayout.processClick(lastBounds, mouseLocation, new KeyCombo(arg0.getButton(), pressedKeys));
				// Process click as block interaction instead
				if (!result) {
					try {
						World world = loadedUniverse.currentWorld();
						cameraXOld = world.getCameraX();
						cameraYOld = world.getCameraY();
						if (arg0.getButton() == 3) {
							Point p = loadedUniverse.getRender().getMouseTile(bounds, world, mouseLocation);
							TileObject tile = world.getTile(p.x, p.y, 1);
							if (tile.getID().equals("air"))
								world.setTile(new Dirt(loadedUniverse.getRegistry(), ""), p.x, p.y, 1);
							else
								world.setTile(new Air(loadedUniverse.getRegistry(), ""), p.x, p.y, 1);
						}
					} catch (NullPointerException e) {
						// Do nothing
					} 
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				mouseLocation = arg0.getPoint();
				currentLayout.processHover(lastBounds, mouseLocation, new KeyCombo(-1, pressedKeys));
			}
			@Override
			public void mouseDragged(MouseEvent arg0) {
				mouseLocation = arg0.getPoint();
				boolean result = currentLayout.processHover(lastBounds, mouseLocation, new KeyCombo(-1, pressedKeys));
				// Process drag as camera
				if (!result) {
					try {
						World world = loadedUniverse.currentWorld();
						Picasso picasso = loadedUniverse.getRender();
						world.setCameraX(cameraXOld + ((mouseClickOrigin.getX() - mouseLocation.getX()) / picasso.getTileSize()));
						world.setCameraY(cameraYOld + ((mouseLocation.getY() - mouseClickOrigin.getY()) / picasso.getTileSize()));
					} catch (NullPointerException e) {
						// Do nothing
					}
				}
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				try {
					Picasso picasso = loadedUniverse.getRender();
					if (arg0.getWheelRotation() > 0 && picasso.getTileSize() > 8) {
						picasso.setTileSize(picasso.getTileSize() - 4);
					} else if (arg0.getWheelRotation() < 0 && picasso.getTileSize() < 128) {
						picasso.setTileSize(picasso.getTileSize() + 4);
					} else return;
					picasso.clearCache();
				} catch (NullPointerException e) {
					// Do nothing
				}
			}
		});
		// Key events
		setFocusable(true);
		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				int key = arg0.getKeyCode();
				if (!pressedKeys.contains(key))
					pressedKeys.add(key);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				pressedKeys.remove((Object) arg0.getKeyChar());
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				// Do nothing
			}
		});
	}
	
	public boolean loadUniverse(File universeDir) {
		// Load universe
		loadedUniverse = LocalUniverse.load(universeDir);
		return loadedUniverse != null;
	}
	
	public boolean loadUniverse(String ip, String token) {
		// Download universe
		loadedUniverse = NetworkUniverse.connect(ip, token);
		return loadedUniverse != null;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		lastBounds = g.getClipBounds();
		// Calculate FPS
		long current = System.currentTimeMillis();
		if (lastRun == -1)
			lastRun = current;
		else if (current - lastRun > 1000) {
			debugInfo.put("fps", Integer.toString(frames));
			lastRun = current;
			frames = 0;
		}
		// Save panel bounds
		bounds = g.getClipBounds();
		// Paint with renderer
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, g.getClipBounds().width, lastBounds.height);
		if (loadedUniverse != null)
			loadedUniverse.getRender().paint(g, loadedUniverse.currentWorld(), mouseLocation, debugInfo);
		// Paint current menu
		if (menu)
			currentLayout.paint(g, loadedUniverse.getRegistry());
		frames++;
	}
	
	// Player token
	public void setID(String token) {
		loadedUniverse.setID(token);
	}
	
	// Access methods
	public Universe getLoadedUniverse() {
		return loadedUniverse;
	}
	
	// Closing method
	public void exit() {
		try {
			if (loadedUniverse.getClass() == NetworkUniverse.class)
				((NetworkUniverse) loadedUniverse).disconnect();
			else
				((LocalUniverse) loadedUniverse).saveAll();
		} catch (NullPointerException e) {
			// Do nothing
		}
	}

}
