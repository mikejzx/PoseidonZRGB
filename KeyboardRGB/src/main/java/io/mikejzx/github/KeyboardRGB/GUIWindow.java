package io.mikejzx.github.KeyboardRGB;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import io.mikejzx.github.KeyboardRGB.MainClass.LEDMode;

public class GUIWindow extends JFrame implements WindowListener, ActionListener, ItemListener {
	
	private static final long serialVersionUID = 1L;
	
	private JMenuItem menuItemQuit;
	
	public void initialiseMenus () {
		JMenuBar menubar = new JMenuBar();
		
		// Create the file menu.
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menubar.add(menuFile);

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
	}
}