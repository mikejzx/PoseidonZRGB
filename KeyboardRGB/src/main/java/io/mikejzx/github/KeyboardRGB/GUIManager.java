package io.mikejzx.github.KeyboardRGB;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Handles the Graphical User-Interface of the application
*/

public class GUIManager {
	
	public static GUIWindow frame;
	
	public static JComboBox<String> combo;
	public static final String COMBOTYPE_STR_BACKLIT = "Backlit";
	public static final String COMBOTYPE_STR_REACTIVEBACKLIT = "Reactive + Backlight";
	public static final String COMBOTYPE_STR_WAVEH = "WAVEH";
	public static final String COMBOTYPE_STR_WAVEV = "WAVEV";
	public static final String COMBOTYPE_STR_RAIN = "Rain";
	public static final String COMBOTYPE_STR_RAND = "Random Dots";
	
	public static JButton buttonColour1, buttonColour2;
	public static JCheckBox checkCapsStay;
	
	public static boolean windowShowing = true;
	
	public void initialise() {
		System.out.println("GUIManage initialise.");
		
		initialiseFrame();
		initialisePanels();
		frame.initialiseMenus();
		if (PrefsManager.prefStartMinimised == 0) {
			showFrame (); // Show the window
		}
		else {
			windowMinimise ();
			if (PrefsManager.prefStartMinimised != 1) {
				PrefsManager.setPref_startMinimised(1);
			}
		}
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
				if (MainClass.sniffer != null) { MainClass.sniffer.deinitialise(); }
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
		buttonColour1 = new JButton("Set Primary Colour");
		buttonColour2 = new JButton("Set Secondary Colour");
		buttonColour1.addActionListener(frame);
		buttonColour2.addActionListener(frame);
		panel.add(buttonColour1);
		panel.add(buttonColour2);

		// Dropdown
		combo = new JComboBox<String>();
		combo.addItem(COMBOTYPE_STR_BACKLIT);
		combo.addItem(COMBOTYPE_STR_REACTIVEBACKLIT);
		combo.addItem(COMBOTYPE_STR_WAVEH);
		//combo.addItem(COMBOTYPE_STR_WAVEV);
		//combo.addItem(COMBOTYPE_STR_RAIN); (When implemented, may put a little reference to 'Dragon - Rain' just for fun? :P)
		//combo.addItem(COMBOTYPE_STR_RAND);
		combo.addItemListener(frame);
		combo.setSelectedIndex(1);
		panel.add(combo);
		
		// Checkbox
		checkCapsStay = new JCheckBox("Caps-Lock shows it's status");
		//checkCapsStay.addActionListener(frame);
		checkCapsStay.addItemListener(frame);
		if (PrefsManager.prefCapsSustain == 1) {
			checkCapsStay.setSelected(true);
		}
		else {
			checkCapsStay.setSelected(false);
		}
		panel.add(checkCapsStay);

		JLabel creditLabel = new JLabel ("Michael's Poseidon Z RGB Controller");
		JLabel versionLabel = new JLabel("Version: " + MainClass.SOFTWARE_VERSION);
		JLabel javaLabel = new JLabel("Proudly written in Java.");
		JLabel instrLabel = new JLabel("Make sure you've set your keyboard LED mode to 'static' in the official software!");
		panel.add(creditLabel);
		panel.add(versionLabel);
		panel.add(javaLabel);
		panel.add(instrLabel);
	}
	
	public static void windowMinimise () {
		if (!windowShowing) { return; }
		windowShowing = false;
		MainClass.refreshNotifyPopupVisibilityStates();
		
		frame.setVisible(false);
	}
	
	public static void windowRestore () {
		if (windowShowing) { return; }
		windowShowing = true;
		MainClass.refreshNotifyPopupVisibilityStates();
		
		frame.setVisible(true);
	}
}
