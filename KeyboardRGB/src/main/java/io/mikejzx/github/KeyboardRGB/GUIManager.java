package io.mikejzx.github.KeyboardRGB;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Handles the Graphical User-Interface of the application
*/

public class GUIManager {
	
	private static GUIWindow frame;
	
	public static JComboBox<String> combo;
	public static final String COMBOTYPE_STR_BACKLIT = "Backlit";
	public static final String COMBOTYPE_STR_REACTIVEBACKLIT = "Reactive + Backlight";
	public static final String COMBOTYPE_STR_RAIN = "Rain";
	public static final String COMBOTYPE_STR_RAND= "Random Dots";
	
	public void initialise() {
		System.out.println("GUIManage initialise.");
		
		initialiseFrame();
		initialisePanels();
		frame.initialiseMenus();
		showFrame (); // Show the window
	}
	
	private void initialiseFrame () {
		frame = new GUIWindow();
		frame.setSize(new Dimension(400, 300)); // 4:3 because why not
		frame.setLocationRelativeTo(null); // Centre the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Terminate program on window close
		frame.setResizable(false);
		frame.setTitle("Mike's Poseidon Z RGB LED Controller");
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				// Clean up the native hook.
				if (MainClass.sniffer != null) { MainClass.sniffer.Deinitialise(); }
				MainClass.kill();
				System.runFinalization();
				System.exit(0);
		    }
		});
	}
	
	private void showFrame () {
		frame.setVisible(true);
	}
	
	private void initialisePanels () {
		// Main panel
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		// Button
		JButton button1 = new JButton("Button 1");
		panel.add(button1);

		// Dropdown
		combo = new JComboBox<String>();
		combo.addItem(COMBOTYPE_STR_BACKLIT);
		combo.addItem(COMBOTYPE_STR_REACTIVEBACKLIT);
		combo.addItem(COMBOTYPE_STR_RAIN);
		combo.addItem(COMBOTYPE_STR_RAND);
		combo.addItemListener(frame);
		combo.setSelectedIndex(1);
		panel.add(combo);
		
		JLabel creditLabel = new JLabel ("Michael's Poseidon Z RGB Controller");
		JLabel versionLabel = new JLabel("Version: " + MainClass.SOFTWARE_VERSION);
		JLabel javaLabel = new JLabel("Proudly written in Java.");
		panel.add(creditLabel);
		panel.add(versionLabel);
		panel.add(javaLabel);
	}
}
