package pixadv.main;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import pixadv.graphics.swing.frames.ClientFrame;
import pixadv.graphics.swing.frames.CommandFrame;
import pixadv.network.Server;
import pixadv.registry.Registry;
import pixadv.world.storage.universe.LocalUniverse;

public class PixelAdventure {

	private static boolean hosting = false;
	private static int port = 43234;
	
	public static void main(String[] args) {
		if (args.length >= 1 && args[0].equals("server"))
			server(new File("./data/saves/Universe Zero"), port);
		else
			client();
	}
	
	public static void client() {
		// Set environment variables
		System.setProperty("sun.java2d.opengl", "true");
		// Create the game window
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientFrame clientFrame = new ClientFrame();
					CommandFrame commandFrame = new CommandFrame(clientFrame.getContentPane());
					commandFrame.setVisible(true);
					clientFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void server(File universeDir, int port) {
		try {
			// Load the registry
			System.out.println("** Registry Creation **");
			long start = System.currentTimeMillis();
			Registry registry = new Registry();
			System.out.printf("Registry loading completed in %d ms, loaded %d modules\n",
					System.currentTimeMillis() - start, registry.listModules().size());
			// Load the universe to use
			File universeCanonDir = universeDir.getCanonicalFile();
			LocalUniverse universe = LocalUniverse.load(registry, universeCanonDir);
			// Start hosting the game
			ServerSocket serverSocket = new ServerSocket(port);
			hosting = true;
			System.out.printf("Server hosted at '%s:%d'\n", serverSocket.getInetAddress().getHostAddress(), port);
			while (hosting) {
				try {
					Socket socket = serverSocket.accept();
					Server server = Server.connect(socket, universe, universeCanonDir);
					if (server != null)
						server.start();
				} catch (IOException e) {
					System.out.println("I/O error: " + e);
				}
			}
			serverSocket.close();
			// Save when server closed
			universe.saveAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
