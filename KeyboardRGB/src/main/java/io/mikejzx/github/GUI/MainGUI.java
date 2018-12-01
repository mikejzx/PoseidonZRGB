package io.mikejzx.github.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.mikejzx.github.KeyboardRGB.MainClass;
import io.mikejzx.github.KeyboardRGB.MainClass.LEDMode;
import io.mikejzx.github.KeyboardRGB.PrefsManager;
import io.mikejzx.github.KeyboardRGB.Utils;
import io.mikejzx.github.KeyboardRGB.LEDCtrl.LEDWave;

/*
 * This is the new GUI handler. Keep in mind that alot of code in this file is 
 * automatically generated by WindowBuilder. As I found it more effecient to use
 * that instead of programming every goddamn element myself.
*/

public class MainGUI implements ChangeListener, ItemListener {

	public static JButton btnApply;
	public static Thread labelThread;
	public static JFrame frmMainFrame;
	public static boolean labelThreadRunning = false;
	public static boolean windowShowing = false;
	
	private static Map<JRadioButton, Integer> tabIndices;
	private static Map<Integer, MainClass.LEDMode> tabMode;
	private static JTabbedPane tabs_ledmode;
	
	private static int selectedTab;
	private static JLabel lblConnection;
	
	private static JLabel lblDeveloper;
	private static Color label_colour;
	private static float label_timer;
	private static int[] label_rgbprev;
	private static int[] label_rgbtarget;
	
	private static final String CONNECTION_FALS_STR = "<html>Connection Status: <font color=red><strong>Not connected</strong></font></html>";
	private static final String CONNECTION_TRUE_STR = "<html>Connection Status: <font color=green><strong>O.K !</strong></font></html>";
	private static final Color COLOUR_TRANS =  new Color(0.0f, 0.0f, 0.0f, 0.0f);
	
	private static JCheckBox chCapsSustain;
	
	// Note: main instance refers to the MainGUI instance that is in MainClass.java !
	public MainGUI(boolean mainInstance) throws URISyntaxException {
		if (mainInstance) { return; }
		init();
	}
	
	public void initialise() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainGUI(false); // Call ctor
					frmMainFrame.setLocationByPlatform(true);
					
					if (PrefsManager.prefStartMinimised == 0) {
						windowRestore ();
					}
					else {
						windowMinimise ();
						if (PrefsManager.prefStartMinimised != 1) {
							PrefsManager.setPref_startMinimised(1);
						}
					}
					
					// Override height. (Put here so that it doesn't get called in design view. Because it will cause an error.)
					//tabs_ledmode.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() { @Override protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) { return 20; } } );		
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("[MainGUI.java] finished");
			}
		});
	}

	private void init() throws URISyntaxException {
		frmMainFrame = new JFrame();
		frmMainFrame.setTitle("Mike's Poseidon Z RGB Controller");
		frmMainFrame.setResizable(false);
		frmMainFrame.setBounds(100, 100, 400, 300);
		frmMainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmMainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				
				MainClass.kill();
			}
		});
		
		ButtonGroup group = new ButtonGroup();
		LEDMode[] modes = LEDMode.values();
		tabIndices = new HashMap<JRadioButton, Integer>();
		tabMode = new HashMap<Integer, LEDMode>();
		tabs_ledmode = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel panel_back = new JPanel();
		JPanel panel_reac = new JPanel();
		JPanel panel_wave = new JPanel();
		JPanel panel_rain = new JPanel();
		JPanel panel_rand = new JPanel();
		JPanel[] tabPanels = new JPanel[] {
			panel_back,
			panel_reac,
			panel_wave,
			panel_rain,
			panel_rand
		};
		
		tabs_ledmode.addTab("backl", null, panel_back, null);
		
		JPanel sqrCol_backlit = new GUISquare(new Color(255, 0, 0));
		JButton btnCol_backl = new GUIColourPickerButton("Set Colour", "Set colour", MainClass.ledContBacklit.colours, 0, (GUISquare)sqrCol_backlit);
		
		GroupLayout gl_panel_back = new GroupLayout(panel_back);
		gl_panel_back.setHorizontalGroup(
			gl_panel_back.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_back.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCol_backl, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sqrCol_backlit, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(455, Short.MAX_VALUE))
		);
		gl_panel_back.setVerticalGroup(
			gl_panel_back.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_back.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_back.createParallelGroup(Alignment.LEADING, false)
						.addComponent(sqrCol_backlit, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCol_backl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(241))
		);
		panel_back.setLayout(gl_panel_back);
		tabs_ledmode.addTab("react", null, panel_reac, null);
		
		JPanel sqrCol0_react = new GUISquare(new Color(255, 0, 0));
		JPanel sqrCol1_react = new GUISquare(new Color(255, 0, 0));
		JButton btnCol0_react = new GUIColourPickerButton("Set Colour (Primary)", "Set primary colour (reactive mode)", MainClass.ledContReactive.colours, 0, (GUISquare)sqrCol0_react);
		JButton btnCol1_react = new GUIColourPickerButton("Set Colour (Secondary)", "Set secondary colour (reactive mode)", MainClass.ledContReactive.colours, 1, (GUISquare)sqrCol1_react);
		
		chCapsSustain = new JCheckBox("Caps-Lock sustain when ON");
		chCapsSustain.addItemListener(this);
		if (PrefsManager.prefCapsSustain == 1) {
			chCapsSustain.setSelected(true);
		}
		else {
			chCapsSustain.setSelected(false);
		}
		
		GroupLayout gl_panel_reac = new GroupLayout(panel_reac);
		gl_panel_reac.setHorizontalGroup(
			gl_panel_reac.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_reac.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_reac.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_reac.createSequentialGroup()
							.addGroup(gl_panel_reac.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnCol0_react, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnCol1_react, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_reac.createParallelGroup(Alignment.LEADING)
								.addComponent(sqrCol0_react, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(sqrCol1_react, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)))
						.addComponent(chCapsSustain))
					.addContainerGap(189, Short.MAX_VALUE))
		);
		gl_panel_reac.setVerticalGroup(
			gl_panel_reac.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_reac.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_reac.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCol0_react, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(sqrCol0_react, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_reac.createParallelGroup(Alignment.LEADING)
						.addComponent(sqrCol1_react, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCol1_react, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chCapsSustain)
					.addContainerGap(64, Short.MAX_VALUE))
		);
		panel_reac.setLayout(gl_panel_reac);
		tabs_ledmode.addTab("waveh", null, panel_wave, null);
		
		JComboBox<String> combo_waveDir = new JComboBox<String>();
		combo_waveDir.setModel(new DefaultComboBoxModel<String>(new String[] {"UP,", "DOWN,", "RIGHT,", "LEFT,"}));
		combo_waveDir.setSelectedIndex(1);
		combo_waveDir.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int idx = combo_waveDir.getSelectedIndex();
				LEDWave.WaveDirection dir = LEDWave.WaveDirection.values()[idx];
				MainClass.ledContWave.waveDir = dir;
			}
		});
		
		JLabel lblWaveDirection = new JLabel("Wave Direction:");
		GroupLayout gl_panel_wave = new GroupLayout(panel_wave);
		gl_panel_wave.setHorizontalGroup(
			gl_panel_wave.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_wave.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblWaveDirection)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(combo_waveDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(425, Short.MAX_VALUE))
		);
		gl_panel_wave.setVerticalGroup(
			gl_panel_wave.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_wave.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_wave.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWaveDirection)
						.addComponent(combo_waveDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(250, Short.MAX_VALUE))
		);
		panel_wave.setLayout(gl_panel_wave);
		tabs_ledmode.addTab("rain", null, panel_rain, null);
		GroupLayout gl_panel_rain = new GroupLayout(panel_rain);
		gl_panel_rain.setHorizontalGroup(
			gl_panel_rain.createParallelGroup(Alignment.LEADING)
				.addGap(0, 373, Short.MAX_VALUE)
		);
		gl_panel_rain.setVerticalGroup(
			gl_panel_rain.createParallelGroup(Alignment.LEADING)
				.addGap(0, 152, Short.MAX_VALUE)
		);
		panel_rain.setLayout(gl_panel_rain);
		tabs_ledmode.addTab("rand", null, panel_rand, null);
		GroupLayout gl_panel_rand = new GroupLayout(panel_rand);
		gl_panel_rand.setHorizontalGroup(
			gl_panel_rand.createParallelGroup(Alignment.LEADING)
				.addGap(0, 577, Short.MAX_VALUE)
		);
		gl_panel_rand.setVerticalGroup(
			gl_panel_rand.createParallelGroup(Alignment.LEADING)
				.addGap(0, 275, Short.MAX_VALUE)
		);
		panel_rand.setLayout(gl_panel_rand);
		
		tabs_ledmode.addChangeListener(this);
		
		JPanel panel_bottom = new JPanel();
		
		JLabel lblVersion = new JLabel("Version: " + MainClass.SOFTWARE_VERSION);
		lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConnection = new JLabel("<html>Connection Status: Loading...</html>");
		lblConnection.setHorizontalAlignment(SwingConstants.RIGHT);
		
		btnApply = new JButton("Apply");
		btnApply.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnApply.setEnabled(false);
		btnApply.setToolTipText("Apply the LED changes.");
		btnApply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int x = 0;
					int idx = tabs_ledmode.getSelectedIndex();
					Enumeration<AbstractButton> e = group.getElements();
					for (Enumeration<AbstractButton> i = e; e.hasMoreElements();) {
						JRadioButton src = (JRadioButton)i.nextElement();
						if (x == idx) {
							src.setSelected(true);
							MainGUI.changeTab(src);
						}
						x++;
					}
					btnApply.setEnabled(false);
					applyLEDMode();
				}
			});
		GroupLayout groupLayout = new GroupLayout(frmMainFrame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabs_ledmode, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
						.addComponent(panel_bottom, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(tabs_ledmode, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_bottom, GroupLayout.PREFERRED_SIZE, 68, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		lblDeveloper = new JLabel("Developed by Michael");
		lblDeveloper.setHorizontalAlignment(SwingConstants.RIGHT);
		
		class LabelThread implements Runnable {
			public void run () {
				labelThreadRunning = true;
				MainGUI.updateCreditLabel();
				labelThreadRunning = false;
				System.out.println("[MainGUI.java, LabelThread] Thread completed");
			}
		}
		LabelThread labThr = new LabelThread();
		labelThread = new Thread(labThr);
		labelThread.start();
		
		// Open website link.
		// SRC:: https://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel#527877
		// Thanks to McDowell
		final String BTN_LINK = "https://mikejzx.github.io";
		final String BTN_DISP = "https://mikejzx.github.io";
		JButton btnWebsite = new JButton(BTN_DISP);
		final URI uri = new URI(BTN_LINK);
		class OpenUrlAction implements ActionListener {
			@Override public void actionPerformed (ActionEvent e) { openUrl (uri); }
		}
		btnWebsite.setText("<html><font color=\"#0066cc\"><u>" + BTN_DISP + "</u></font></html>");
		btnWebsite.setBorderPainted(false);
		btnWebsite.setOpaque(false);
		btnWebsite.setBackground(COLOUR_TRANS);
		btnWebsite.setToolTipText("The developer's website.\nClick to follow link...");
		btnWebsite.addActionListener(new OpenUrlAction());
		btnWebsite.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnWebsite.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnWebsite.setContentAreaFilled(false);
		btnWebsite.setFocusPainted(false);
		
		JButton btnReset = new JButton("RESET");
		btnReset.setEnabled(false);
		
		GroupLayout gl_panel_bottom = new GroupLayout(panel_bottom);
		gl_panel_bottom.setHorizontalGroup(
			gl_panel_bottom.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_bottom.createSequentialGroup()
					.addGroup(gl_panel_bottom.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_panel_bottom.createSequentialGroup()
							.addComponent(btnWebsite, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
							.addGap(129)
							.addComponent(lblDeveloper))
						.addComponent(lblVersion)
						.addGroup(gl_panel_bottom.createSequentialGroup()
							.addComponent(btnApply)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnReset)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblConnection)))
					.addContainerGap())
		);
		gl_panel_bottom.setVerticalGroup(
			gl_panel_bottom.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_bottom.createSequentialGroup()
					.addGroup(gl_panel_bottom.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_bottom.createSequentialGroup()
							.addGroup(gl_panel_bottom.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_bottom.createSequentialGroup()
									.addContainerGap()
									.addComponent(lblConnection)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblVersion))
								.addComponent(btnApply))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_bottom.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblDeveloper)
								.addComponent(btnWebsite)))
						.addComponent(btnReset))
					.addContainerGap())
		);
		panel_bottom.setLayout(gl_panel_bottom);
		frmMainFrame.getContentPane().setLayout(groupLayout);

		for (int i = 0; i < tabPanels.length; i++) {
			//if (!modes[i].getImplemented()) { continue; }
			LEDMode mode = modes[i];
			tabMode.put(i, mode);
			JRadioButton tabRadio = new JRadioButton("");
			tabRadio.setBackground(COLOUR_TRANS);
			tabRadio.setOpaque(false);
			tabRadio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (arg0.getSource() == tabRadio) {
						MainGUI.changeTab((JRadioButton)tabRadio);
					}
					applyLEDMode();
				}
			});
			JPanel tabLabel = new JPanel(new GridBagLayout()); // GridBagLayout fixed everything O_O
			JLabel lab = new JLabel(mode.name());
			if (!mode.getImplemented()) {
				tabs_ledmode.setEnabledAt(i, false);
				lab.setForeground(new Color(172, 172, 172));
				tabRadio.setEnabled(false);
			}
			tabLabel.setOpaque(false);
			tabLabel.add(lab);
			tabLabel.add(tabRadio);
			tabs_ledmode.setTabComponentAt(i, tabLabel);
			tabIndices.put(tabRadio, i);
			group.add(tabRadio);
			if (i == 0) { tabRadio.setSelected(true); }
		}
	}

	public static void changeTab(JRadioButton src) {
		int idx = tabIndices.get(src);
		tabs_ledmode.setSelectedIndex(idx);
		selectedTab = idx;
		btnApply.setEnabled(false);
	}

	// On tab change
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (selectedTab != tabs_ledmode.getSelectedIndex()) {
			btnApply.setEnabled(true);
		}
		else {
			btnApply.setEnabled(false);
		}
	}
	
	private void applyLEDMode() {
		LEDMode mode = tabMode.get(tabs_ledmode.getSelectedIndex());
		MainClass.setLEDMode(mode);
	}
	
	public static void setConectionStatus (boolean connected) {
		String str = connected ? CONNECTION_TRUE_STR : CONNECTION_FALS_STR;
		lblConnection.setText(str);
	}
	
	private static void openUrl(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} 
			catch (IOException e) {  
				e.printStackTrace();
			}
		} 
		else { 
			System.err.println("[MainGUI.java] Browser not supproted.");
		}
	}
	
	// Just for fun
	private static void updateCreditLabel () {
		label_timer = 0.0f;
		label_rgbprev = new int[] {
			0, 0, 0
		};
		label_rgbtarget = new int[] {
			255, 128, 64
		};
		int[] lerpedrgb = new int[] {
			0, 0, 0
		};
		int[] roundrgb = new int[] {
			0, 0, 0
		};
		
		while (!MainClass.kill) {
			if (label_timer < 1.0f) {
				label_timer += 0.1f;
			}
			else {
				label_timer = 0.0f;
				for (int i = 0; i < label_rgbtarget.length; i++) { 
					label_rgbprev[i] = label_rgbtarget[i]; 
					label_rgbtarget[i] = ThreadLocalRandom.current().nextInt(0, 255);
				}
				
				// Makes sure that at least 1 value is 0xFF
				int sel = ThreadLocalRandom.current().nextInt(0, 3);
				label_rgbtarget[sel] = 255;
			}
			
			for (int i = 0; i < lerpedrgb.length; i++) {
				lerpedrgb[i] = Utils.lerp(label_rgbprev[i], label_rgbtarget[i], label_timer);
				roundrgb[i] = Utils.clamp(lerpedrgb[i], 0, 255);
			}
			label_colour = new Color(roundrgb[0], roundrgb[1], roundrgb[2]);
			//System.out.println("r:" + roundrgb[0] + " g:" + roundrgb[1] + " b:" + roundrgb[2]);
			
			lblDeveloper.setForeground(label_colour);
			
			try { Thread.sleep(100); } catch (InterruptedException e) 
			{ e.printStackTrace(); }
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == chCapsSustain) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				MainClass.capsLockStays = true;
				PrefsManager.setPref_capsSustain(1);
			}
			else {
				MainClass.capsLockStays = false;
				PrefsManager.setPref_capsSustain(0);
			}
		}
	}
	
	public static void windowMinimise () {
		if (!windowShowing) { return; }
		windowShowing = false;
		MainClass.refreshNotifyPopupVisibilityStates();
		
		frmMainFrame.setVisible(false);
	}
	
	public static void windowRestore () {
		if (windowShowing) { return; }
		windowShowing = true;
		MainClass.refreshNotifyPopupVisibilityStates();
		
		frmMainFrame.setVisible(true);
	}
}
