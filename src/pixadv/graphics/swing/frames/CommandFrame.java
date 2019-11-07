package pixadv.graphics.swing.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import pixadv.graphics.layouts.MenuLayout;
import pixadv.graphics.swing.components.GamePanel;
import pixadv.world.storage.universe.Universe;

public class CommandFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private GamePanel gamePanel;
	private JTextField commandField;
	private JTextPane historyPane;

	/**
	 * Create the frame.
	 */
	public CommandFrame(GamePanel panel) {
		gamePanel = panel;
		setTitle("Commands");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane historyScroll = new JScrollPane();
		historyScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(historyScroll, BorderLayout.CENTER);
		
		historyPane = new JTextPane();
		historyPane.setEditable(false);
		historyScroll.setViewportView(historyPane);
		
		commandField = new JTextField();
		commandField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String command = commandField.getText().replaceAll("\\s+", " ");
					if (command.length() > 0) {
						try {
							// Add command to history
							addHistory("> " + command);
							boolean unknown = false;
							// Interpret command
							String[] commandSplit = command.split("\\s");
							if (commandSplit[0].equals("phys")) {
								Universe universe = gamePanel.getLoadedUniverse();
								universe.getPhysics().manualChange(universe.getPlayer(), commandSplit[1]);
								addHistory(String.format("  Set physics data '%s' on player.", commandSplit[1]));
							} else if (command.equals("redraw")) {
								gamePanel.getLoadedUniverse().getRender().clearCache();
								addHistory("  Chunk cache cleared.");
							} else if (commandSplit[0].equals("scale")) {
								MenuLayout.menuScale = Double.parseDouble(commandSplit[1]);
								addHistory(String.format("  Set menu scale to %s", commandSplit[1]));
							} else if (commandSplit[0].equals("toggle")) {
								if (commandSplit[1].equals("debug")) {
									// Toggle debug info drawing
									gamePanel.getLoadedUniverse().getRender().debug = !gamePanel.getLoadedUniverse().getRender().debug;
									addHistory("  Toggled debug info.");
								} else if (commandSplit[1].equals("menu")) {
									// Toggle menu layer drawing 
									gamePanel.menu = !gamePanel.menu;
									addHistory("  Toggled menu.");
								} else if (commandSplit[1].equals("optimized")) {
									// Toggle tile drawing optimization
									gamePanel.getLoadedUniverse().getRender().optimized = !gamePanel.getLoadedUniverse().getRender().optimized;
									gamePanel.getLoadedUniverse().getRender().clearCache();
									addHistory("  Toggled tile optimization.");
								} else {
									addHistory(String.format("  Unknown argument '%s'.", commandSplit[1]));
									unknown = true;
								}
							} else {
								addHistory("  Unknown command.");
								unknown = true;
							}
							// Clear bar if command was processed
							if (!unknown)
								commandField.setText("");
						} catch (Exception e) {
							// Add exception info to history
							addHistory("  Exception caught while executing command!");
							addHistory("  " + e.getClass().getName() + ": " + e.getMessage());
							// Print stacktrace
							System.out.printf("Exception caught while executing command '%s'!\n", command);
							e.printStackTrace();
						}
					}
				} catch (NullPointerException e) {
					// Do nothing
				}
				
			}
		});
		contentPane.add(commandField, BorderLayout.SOUTH);
		commandField.setColumns(10);
	}
	
	// Utility method
	private void addHistory(String line) {
		historyPane.setText(String.format("%s%s\n", historyPane.getText(), line));
	}

}
