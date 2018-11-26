package io.mikejzx.github.KeyboardRGB;

import java.awt.Color;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import io.mikejzx.github.KeyboardRGB.MainClass.LEDMode;

public class GUIWindow extends JFrame implements WindowListener, ActionListener, ItemListener {
	
	private static final long serialVersionUID = 1L;
	
	private JMenuItem menuItemQuit, menuItemAbout, menuItemMinimise;
	
	public void initialiseMenus () {
		JMenuBar menubar = new JMenuBar();
		
		// Create the file menu.
		JMenu menuFile = new JMenu("Goodies");
		menuFile.setMnemonic(KeyEvent.VK_G);
		menubar.add(menuFile);

		menuItemMinimise = new JMenuItem("Minimise to Tray");
		menuItemMinimise.addActionListener(this);
		menuItemMinimise.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		menuItemMinimise.getAccessibleContext().setAccessibleDescription("Minimise the application to the system tray.");
		menuFile.add(menuItemMinimise);
		
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(this);
		menuFile.add(menuItemAbout);
		
		menuFile.addSeparator();
		
		menuItemQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		menuItemQuit.addActionListener(this);
		menuItemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItemQuit.getAccessibleContext().setAccessibleDescription("Exit the application.");
		menuFile.add(menuItemQuit);

		setJMenuBar(menubar);
	}
	
	@Override
	public void windowClosed(WindowEvent e) {}

	// Implemented methods
	@Override
	public void windowActivated(WindowEvent e) { }
	@Override
	public void windowClosing(WindowEvent e) { }
	@Override
	public void windowDeactivated(WindowEvent e) { }
	@Override
	public void windowDeiconified(WindowEvent e) { }
	@Override
	public void windowIconified(WindowEvent e) { }
	@Override
	public void windowOpened(WindowEvent e) { }
	@Override
	public void actionPerformed(ActionEvent a) {
		Object src = a.getSource();
		if (src == menuItemQuit) {
			this.dispose();
			//MainClass.kill();
		}
		else if (src == GUIManager.buttonColour1) {
			Color newColour = JColorChooser.showDialog(null, "Change the primary colour.", Utils.toRGB(MainClass.colour1));
			if (newColour != null) {
				MainClass.colour1 = Utils.toHex(newColour);
				//System.out.print("COLOUR: " + newColour.getRed() + ", " + newColour.getGreen() + ", " + newColour.getBlue());
				//System.out.println("HEX: " + Utils.hex(Utils.toHex(newColour)));
				MainClass.setAllKeyLerpsZero();
				MainClass.refreshLEDColour();
				MainClass.refreshLEDs();
			}
		}
		else if (src == GUIManager.buttonColour2) {
			Color newColour = JColorChooser.showDialog(null, "Change the primary colour.", Utils.toRGB(MainClass.colour2));
			if (newColour != null) {
				MainClass.colour2 = Utils.toHex(newColour);
				MainClass.setAllKeyLerpsZero();
				MainClass.refreshLEDColour();
				MainClass.refreshLEDs();
			}
		}
		else if (src == menuItemMinimise) {
			GUIManager.windowMinimise();
		}
	}
	@Override
	public void itemStateChanged(ItemEvent a) { 
		ItemSelectable item = a.getItemSelectable();

		// The effect type was changed
		if (item == GUIManager.combo) {
			String str = item.getSelectedObjects()[0].toString();
			LEDMode mode = LEDMode.Backlit;
			switch (str) {
				// Backlit mode
				case (GUIManager.COMBOTYPE_STR_BACKLIT): {
					mode = LEDMode.Backlit;
				} break;
				
				case (GUIManager.COMBOTYPE_STR_REACTIVEBACKLIT): {
					mode = LEDMode.ReactiveBacklit;
				} break;
				
				case (GUIManager.COMBOTYPE_STR_WAVEH): {
					mode = LEDMode.WaveH;
				} break;
				
				case (GUIManager.COMBOTYPE_STR_WAVEV): {
					mode = LEDMode.WaveV;
				} break;
				
				case (GUIManager.COMBOTYPE_STR_RAIN): {
					mode = LEDMode.Rain;
				} break;
				
				case (GUIManager.COMBOTYPE_STR_RAND): {
					mode = LEDMode.Random;
				} break;
				
				// Undefined
				default: {
					str = "UNDEFINED_CASE[" + str + "]";
					mode = LEDMode.Backlit;
				} break;
			}
			System.out.println("Changed mode to " + str);
			MainClass.setLEDMode(mode);
		}
		if (item == GUIManager.checkCapsStay) {
			if (a.getStateChange() == ItemEvent.SELECTED) {
				MainClass.capsLockStays = true;
				PrefsManager.setPref_capsSustain(1);
			}
			else {
				MainClass.capsLockStays = false;
				PrefsManager.setPref_capsSustain(0);
			}
		}
	}
}