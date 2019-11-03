package pixadv.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import pixadv.world.storage.universe.Universe;
import pixadv.world.storage.world.NetworkWorld;
import pixadv.world.types.tiles.Chunk;

public class Client extends Thread{

	private Socket socket;
	private Universe universe;
	
	private HashMap<String, String> requests = new HashMap<String, String>();
	
	// Constructor
	public Client(Socket socket, Universe universe) {
		super("PixAdv-Client-" + socket.getInetAddress().getHostAddress());
		this.socket = socket;
		this.universe = universe;
	}
	
	// Thread methods
	@Override
	public void run() {
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
			//
			while (!socket.isClosed()) {
				try {
					// TODO process all received requests
					
					synchronized (requests) {
						// Send all waiting requests to the server
						for (Object request : requests.keySet().toArray()) {
							if (requests.get(request) == null) {
								// Send request to server
								socketOut.println(request + "\n~");
								socketOut.flush();
								// Wait for response
								String line, response = "";
								while (true) {
									line = socketIn.readLine();
									if (line.equals("~response")) {
										break;
									} else if (line.equals("~disconnected")) {
										socket.close();
										break;
									} else {
										response += line + "\n";
									}
								}
								requests.put(request.toString(), response);
							}
						}
						requests.notifyAll();
					}
				} catch (Exception e) {
					e.printStackTrace();
					socket.close();
					break;
				}
			}
			System.out.printf("Connection to '%s' closed.\n", socket.getInetAddress().getHostAddress());
		} catch (IOException e) {
			System.out.printf("Connection to '%s' failed!\n", socket.getInetAddress().getHostAddress());
			System.out.printf("  %s: %s\n", e.getClass().getName(), e.getMessage());
		}
	}
	
	public void disconnect() {
		requests.put("disconnect", null);
	}
	
	// Access methods
	public Chunk requestChunk(NetworkWorld world, int cx, int cy) {
		synchronized (requests) {
			try {
				// Add request to queue and wait
				String request = String.format("getChunk(%s,%d,%d)", world.getInfo("name"), cx, cy);
				requests.put(request, null);
				while (requests.get(request) == null)
					requests.wait();
				// Check if request was accepted by server
				if (requests.get(request).equals("denied")) {
					return null;
				} else {
					Chunk chunk = Chunk.load(world.getRegistry(), requests.get(request), world);

					// Return now loaded chunk
					return chunk;
				}
			} catch (InterruptedException e) {
				// Thread was interrupted
				e.printStackTrace();
				return null;
			}
		}
	}

}
