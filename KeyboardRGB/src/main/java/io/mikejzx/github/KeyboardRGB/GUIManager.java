package io.mikejzx.github.KeyboardRGB;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import io.mikejzx.github.KeyboardRGB.MainClass.LEDMode;

/*
 * Handles the Graphical User-Interface of the application
*/

public class GUIManager {
	
	public static GUIWindow frame;
	
	public static JComboBox<String> combo;
	public static final String COMBOTYPE_STR_BACKLIT = "Backlit";
	public static final String COMBOTYPE_STR_REACTIVEBACKLIT = "Reactive + Backlight";
	public static final String COMBOTYPE_STR_WAVEH = "WAVEH (IN DEV...)";
	public static final String COMBOTYPE_STR_WAVEV = "WAVEV (IN DEV...)";
	public static final String COMBOTYPE_STR_RAIN = "Rain";
	public static final String COMBOTYPE_STR_RAND = "Random Dots";
	
	public static JButton buttonColBacklit, buttonCol1React, buttonCol2React;
	public static GUISquare sqrColBacklit, sqrColReact1, sqrColReact2;
	public static JCheckBox checkCapsStay;
	public static JTabbedPane tabs;
	public static Map<JRadioButton, Integer> tabIndices;
	
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
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBounds(0, 0, 100, 100);
		frame.getContentPane().add(panel);
		
		// Tab pane
		List<JPanel> tabPanels = new ArrayList<JPanel>();
		
		//Color radColour =  new Color(200, 221, 242);
		Color radColour =  new Color(0.0f, 0.0f, 0.0f, 0.0f);
		ButtonGroup group = new ButtonGroup();
		tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		// Override height.
		tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() { @Override protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) { return 20; } } );
		LEDMode[] modes = LEDMode.values();
		tabIndices = new HashMap<JRadioButton, Integer>();
		for (int i = 0; i < modes.length; i++) {
			//if (!modes[i].getImplemented()) { continue; }
			
			LEDMode mode = modes[i];
			tabPanels.add(new JPanel());
			tabs.addTab(null, tabPanels.get(mode.getIdx()));
			JRadioButton tabRadio = new JRadioButton("");
			tabRadio.setBackground(radColour);
			tabRadio.setOpaque(false);
			tabRadio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (arg0.getSource() == tabRadio) {
						GUIManager.changeTab((JRadioButton)tabRadio);
					}
				}
			});
			JPanel tabLabel = new JPanel();
			JLabel lab = new JLabel(mode.name());
			if (!mode.getImplemented()) {
				tabs.setEnabledAt(i, false);
				lab.setForeground(new Color(172, 172, 172));
				tabRadio.setEnabled(false);
			}
			tabLabel.setOpaque(false);
			tabLabel.add(lab);
			tabLabel.add(tabRadio);
			tabs.setTabComponentAt(i, tabLabel);
			tabIndices.put(tabRadio, i);
			group.add(tabRadio);
			if (i == 0) { tabRadio.setSelected(true); }
		}
		panel.add(tabs);
		
		// BACKLIT -- -- -- --
		JPanel panBacklit = tabPanels.get(LEDMode.Backlit.getIdx());
		buttonColBacklit = new JButton("Set Colour");
		buttonColBacklit.addActionListener(frame);
		sqrColBacklit = new GUISquare(0, 0, new Color(255, 0, 0));
		panBacklit.add(buttonColBacklit);
		panBacklit.add(sqrColBacklit);
		
		// REACTIVE -- -- -- --
		JPanel panReactive = tabPanels.get(LEDMode.ReactiveBacklit.getIdx());
		buttonCol1React = new JButton("Set Colour (Primary)");
		buttonCol1React.addActionListener(frame);
		buttonCol2React = new JButton("Set Colour (Secondary)");
		buttonCol2React.addActionListener(frame);
		sqrColReact1 = new GUISquare(0, 0, new Color(255, 0, 0));
		sqrColReact2 = new GUISquare(0, 0, new Color(255, 0, 0));
		panReactive.add(buttonCol1React);
		panReactive.add(buttonCol2React);
		panReactive.add(sqrColReact1);
		panReactive.add(sqrColReact2);
		
		JPanel flowPanel = new JPanel(new FlowLayout());
		//frame.getContentPane().add(flowPanel);
		JLabel labVersion = new JLabel(MainClass.SOFTWARE_VERSION);
		flowPanel.add(labVersion);
		
		// Button
		/*
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
		combo.addItem(COMBOTYPE_STR_WAVEV);
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
		*/
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
	
	public static void changeTab(JRadioButton src) {
		tabs.setSelectedIndex(tabIndices.get(src));
	}
}
