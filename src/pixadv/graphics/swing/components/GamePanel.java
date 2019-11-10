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
import pixadv.graphics.layouts.components.MenuComponent;
import pixadv.graphics.picasso.Picasso;
import pixadv.graphics.swing.util.InputProcessor;
import pixadv.registry.Registry;
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
	
	private Registry registry;
	private Universe loadedUniverse;
	
	private Rectangle bounds;
	private Point mouseLocation;
	private Point mouseClickOrigin;
	private ArrayList<Integer> pressedKeys = new ArrayList<Integer>();
	
	private double cameraXOld, cameraYOld;
	
	private Rectangle lastBounds = new Rectangle();
	private MenuLayout currentMenu = new StartMenu(this);
	
	// For debugging
	public boolean menu = true;
	
	// Constructor
	public GamePanel() {
		debugInfo.put("fps", "-1");
		// User input thread
		new Thread(new Runnable() {
			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				while (true)
					try {
						// Process movement input
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
						// Process other keyboard input
						
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
		// Load the registry
		System.out.println("** Registry Creation **");
		long start = System.currentTimeMillis();
		registry = new Registry();
		System.out.printf("Registry loading completed in %d ms, loaded %d modules\n",
				System.currentTimeMillis() - start, registry.listModules().size());
		// Mouse events
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				// Save current mouse info
				mouseClickOrigin = arg0.getPoint();
				mouseLocation = mouseClickOrigin;
				// Attempt to focus
				InputProcessor inputProcessor = currentMenu.getInputProcessor();
				inputProcessor.mousePressed(lastBounds, mouseLocation, new KeyCombo(arg0.getButton(), pressedKeys));
				MenuComponent focused = inputProcessor.getFocusedComponent();
				// Process click as block interaction instead
				if (focused == null) {
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
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Save current mouse info
				mouseLocation = arg0.getPoint();
				currentMenu.getInputProcessor().mouseReleased(lastBounds, mouseLocation, new KeyCombo(arg0.getButton(), pressedKeys));
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				// Save current mouse info
				mouseLocation = arg0.getPoint();
				currentMenu.getInputProcessor().mouseMoved(lastBounds, mouseLocation, new KeyCombo(-1, pressedKeys));
			}
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// Save current mouse info
				mouseLocation = arg0.getPoint();
				currentMenu.getInputProcessor().mouseMoved(lastBounds, mouseLocation, new KeyCombo(-1, pressedKeys));
				try {
					World world = loadedUniverse.currentWorld();
					Picasso picasso = loadedUniverse.getRender();
					world.setCameraX(cameraXOld + ((mouseClickOrigin.getX() - mouseLocation.getX()) / (32 * picasso.getTileScale())));
					world.setCameraY(cameraYOld + ((mouseLocation.getY() - mouseClickOrigin.getY()) / (32 * picasso.getTileScale())));
				} catch (NullPointerException e) {
					// Do nothing
				}
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				try {
					Picasso picasso = loadedUniverse.getRender();
					if (arg0.getWheelRotation() > 0 && picasso.getTileScale() > 0.5) {
						picasso.setTileScale(picasso.getTileScale() - 0.25);
					} else if (arg0.getWheelRotation() < 0 && picasso.getTileScale() < 10) {
						picasso.setTileScale(picasso.getTileScale() + 0.25);
					}
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
	
	// Control methods
	public boolean loadUniverse(Registry registry, File universeDir) {
		// Load universe
		loadedUniverse = LocalUniverse.load(registry, universeDir);
		return loadedUniverse != null;
	}
	
	public boolean loadUniverse(Registry registry, String ip, String token) {
		// Download universe
		loadedUniverse = NetworkUniverse.connect(registry, ip, token);
		return loadedUniverse != null;
	}
	
	public void setMenu(MenuLayout menu) {
		currentMenu = menu;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		lastBounds = g.getClipBounds();
		// Rescale the menu
		MenuLayout.autoScale(lastBounds.width, lastBounds.height);
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
			currentMenu.paint(g, registry);
		frames++;
	}
	
	// Player token
	public void setID(String token) {
		loadedUniverse.setID(token);
	}
	
	// Access methods
	public Registry getRegistry() {
		return registry;
	}
	
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
