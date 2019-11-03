package pixadv.graphics.swing.frames;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import pixadv.graphics.swing.components.GamePanel;

public class ClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private GamePanel contentPane;
	
	public ClientFrame() {
		String playerID = "0faa-ec35";
		String playerKey = "abcde";
		boolean server = false;
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(ClientFrame.class.getResource("/modules/pixadv/textures/gui/icon.png")));
		setTitle("A Pixel Adventure");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 480);
		contentPane = new GamePanel();
		if (server) {
			if (contentPane.loadUniverse("127.0.0.1:43234", String.format("%s:%s", playerID, playerKey)))
				contentPane.setID(playerID);
			else
				;
		} else {
			if (contentPane.loadUniverse(new File("./data/saves/Universe Zero")))
				contentPane.setID(playerID);
			else
				;
		}
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}
	
	@Override
	public GamePanel getContentPane() {
		return contentPane;
	}
	
	@Override
	public void dispose() {
		contentPane.exit();
		super.dispose();
		System.exit(0);
	}
	
	// Player token
	public void setID(String token) {
		contentPane.setID(token);
	}

}
